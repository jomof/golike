package com.google.NdkBuildToAndroidStudio;

import com.google.common.collect.ListMultimap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jomof on 1/12/16.
 */
public class GenerateJson {
    private static Pattern toolchainPattern = Pattern.compile(".*[\\/|\\\\](.*?)[\\/|\\\\](.*?)\\.");
    private static Pattern outputFilePattern = Pattern.compile("(.*)\\.");
    private final StringBuilder librariesBuilder = new StringBuilder();
    private final StringBuilder toolchainBuilder = new StringBuilder();
    private final Map<String, String> toolChainToCCompiler = new HashMap<String, String>();
    private final Map<String, String> toolChainToCppCompiler = new HashMap<String, String>();
    private final Output outputs[];

    GenerateJson(ListMultimap<String, Set<AnalyzeFlow.CommandInput>> outputs) {
        this.outputs = new Output[outputs.size()];
        int i = 0;
        for (Map.Entry<String, Set<AnalyzeFlow.CommandInput>> output : outputs.entries()) {
            this.outputs[i] = new Output(output.getKey(), output.getValue());
            ++i;
        }
    }

    public static String accept(
            String fullPathToAndroidMk,
            ListMultimap<String, Set<AnalyzeFlow.CommandInput>> outputs) {
        GenerateJson generate = new GenerateJson(outputs);

        generate.findReleaseFlavors();
        generate.findToolchainNames();
        generate.findLibraryNames();
        generate.findToolChainCompilers();

        generate.emitLibraries();

        // Visit each tool chain
        Set<String> toolchains = new HashSet<String>();
        for (int i = 0; i < generate.outputs.length; ++i) {
            toolchains.add(generate.outputs[i].toolchain);
        }
        for (String toolchain : toolchains) {
            generate.visitToolchain(toolchain);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("    buildFiles : [\"" + fullPathToAndroidMk + "\"],\n");
        sb.append("    libraries : [\n");
        sb.append(generate.librariesBuilder);
        sb.append("\n    ],\n");
        sb.append("    toolchains : [\n");
        sb.append(generate.toolchainBuilder);
        sb.append("\n    ]\n");
        sb.append("}\n");
        return sb.toString();
    }

    private boolean areLibraryNamesUnique() {
        Set<String> uniqueNames = new HashSet<String>();
        for (int i = 0; i < outputs.length; ++i) {
            uniqueNames.add(outputs[i].libraryName);
        }
        return uniqueNames.size() == outputs.length;
    }

    private void emitLibraries() {
        for (int i = 0; i < outputs.length; ++i) {
            if (librariesBuilder.length() > 0) {
                librariesBuilder.append(",\n");
            }

            librariesBuilder.append("        \"" + outputs[i].libraryName + "\" : {\n");
            //      librariesBuilder.append("            index : " + i + ",\n");
            librariesBuilder.append("            executable : \"ndk-build\",\n");
            librariesBuilder.append("            args : [\"" + outputs[i].outputName + "\"],\n");
            librariesBuilder.append("            toolchain : \"" + outputs[i].toolchain + "\",\n");
            librariesBuilder.append("            output : \"" + outputs[i].outputName + "\",\n");
            librariesBuilder.append("            files : [\n");
            StringBuilder sub = new StringBuilder();
            for (AnalyzeFlow.CommandInput input : outputs[i].commandInputs) {
                if (sub.length() > 0) {
                    sub.append(" },\n ");
                }
                sub.append("               { src : \"" + input.filename + "\",\n");
                sub.append("                 flags : [ ");
                StringBuilder argBuilder = new StringBuilder();
                for (Parser.ArgumentExpression arg : input.command.command.args) {
                    if (argBuilder.length() > 0) {
                        argBuilder.append(", ");
                    }
                    argBuilder.append("\"" + arg.arg + "\"");
                }
                sub.append(argBuilder);
                sub.append("]\n");
                sub.append("               }");
            }
            librariesBuilder.append(sub);
            librariesBuilder.append("\n            ]\n");
            librariesBuilder.append("        }");
        }
    }

    private String findLibraryNameByOutputFilePatternPattern(String output) {
        Matcher matcher = outputFilePattern.matcher(output);
        while (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }

    private String findLibraryNameByToolchainPattern(String output) {
        Matcher matcher = toolchainPattern.matcher(output);
        while (matcher.find()) {
            return matcher.group(1) + "-" + matcher.group(2);
        }

        return "";
    }

    private void findLibraryNames() {

        for (int i = 0; i < outputs.length; ++i) {
            String pattern = findLibraryNameByToolchainPattern(outputs[i].outputName);
            if (pattern.length() > 0) {
                outputs[i].libraryName =
                        pattern + "-" + outputs[i].releaseFlavor;
            }
        }

        if (areLibraryNamesUnique()) {
            return;
        }

        for (int i = 0; i < outputs.length; ++i) {
            outputs[i].libraryName =
                    findLibraryNameByOutputFilePatternPattern(outputs[i].outputName);
        }

        if (areLibraryNamesUnique()) {
            return;
        }

        for (int i = 0; i < outputs.length; ++i) {
            outputs[i].libraryName =
                    findLibraryNameByOutputFilePatternPattern(outputs[i].outputName) + "-" + outputs[i].releaseFlavor;
        }

        if (areLibraryNamesUnique()) {
            return;
        }

        throw new RuntimeException("Library names not unique");
    }

    private void findReleaseFlavors() {
        for (int i = 0; i < outputs.length; ++i) {
            Set<String> flavors = new HashSet<String>();
            for (AnalyzeFlow.CommandInput input : outputs[i].commandInputs) {
                Boolean ndebug = input.command.isFlagDefined("NDEBUG");
                if (ndebug == null) {
                    continue;
                }
                flavors.add(ndebug ? "Debug" : "Release");
            }
            if (flavors.size() == 1) {
                outputs[i].releaseFlavor = flavors.iterator().next();
            } else if (flavors.size() == 0) {
                // Assume release
                outputs[i].releaseFlavor = "Release";
            } else {
                throw new RuntimeException("Mixed build flavors");
            }
        }
    }

    private void findToolChainCompilers() {
        for (int i = 0; i < outputs.length; ++i) {
            String toolchain = outputs[i].toolchain;
            Set<String> cCompilers = new HashSet<String>();
            Set<String> cppCompilers = new HashSet<String>();
            for (AnalyzeFlow.CommandInput command : outputs[i].commandInputs) {
                String compilerCommand = command.command.command.command;
                if (command.filename.endsWith(".c")) {
                    cCompilers.add(compilerCommand);
                } else {
                    cppCompilers.add(compilerCommand);
                }
            }

            if (cCompilers.size() == 0 && cppCompilers.size() == 0) {
                throw new RuntimeException("No compilers in toolchain.");
            }

            if (cCompilers.size() > 1) {
                throw new RuntimeException("Too many c compilers in toolchain.");
            }

            if (cppCompilers.size() > 1) {
                throw new RuntimeException("Too many cpp compilers in toolchain.");
            }

            if (cCompilers.size() == 1) {
                toolChainToCCompiler.put(toolchain, cCompilers.iterator().next());
            }

            if (cppCompilers.size() == 1) {
                toolChainToCppCompiler.put(toolchain, cppCompilers.iterator().next());
            }
        }
    }

    private String findToolChainName(String output) {
        Matcher matcher = toolchainPattern.matcher(output);
        while (matcher.find()) {
            return "toolchain-" + matcher.group(1);
        }

        return "toolchain";
    }

    private void findToolchainNames() {
        for (int i = 0; i < outputs.length; ++i) {
            outputs[i].toolchain = findToolChainName(outputs[i].outputName);
        }
    }

    private void visitToolchain(String toolchain) {
        if (toolchainBuilder.length() > 0) {
            toolchainBuilder.append(",\n");
        }
        toolchainBuilder.append("        " + toolchain + " : {\n");
        if (toolChainToCCompiler.containsKey(toolchain)) {
            toolchainBuilder.append("            cCompilerExecutable : \"" + toolChainToCCompiler.get(toolchain) + "\"");
            if (toolChainToCppCompiler.containsKey(toolchain)) {
                toolchainBuilder.append(",");
            }
            toolchainBuilder.append("\n");
        }
        if (toolChainToCppCompiler.containsKey(toolchain)) {
            toolchainBuilder.append("            cppCompilerExecutable : \"" + toolChainToCppCompiler.get(toolchain) + "\"\n");
        }
        toolchainBuilder.append("        }");

    }

    private class Output {
        final String outputName;
        final Set<AnalyzeFlow.CommandInput> commandInputs;
        String libraryName;
        String releaseFlavor;
        String toolchain;

        Output(String outputName, Set<AnalyzeFlow.CommandInput> commandInputs) {
            this.outputName = outputName;
            this.commandInputs = commandInputs;
        }
    }
}
