package com.google.NdkBuildToAndroidStudio;

import com.google.common.collect.ListMultimap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;


/**
 * Created by jomof on 1/15/16.
 */
public class Main {
    private static String getNdkResult(String projectPath, String flags) throws IOException, InterruptedException {
        String command = String.format("ndk-build NDK_PROJECT_PATH=%s %s", projectPath, flags);

        Process proc = Runtime.getRuntime().exec(
                new String[]{"/bin/bash", "-c",
                        String.format("%s -B -n", command)}
        );

        // any error message?
        StreamGobbler errorGobbler = new
                StreamGobbler(proc.getErrorStream(), "ERROR");

        // any output?
        StreamGobbler outputGobbler = new
                StreamGobbler(proc.getInputStream(), "OUTPUT");

        outputGobbler.output.append(String.format("set-ndk-build-flags %s\n", flags));

        // kick them off
        errorGobbler.start();
        outputGobbler.start();

        proc.waitFor();
        return outputGobbler.output.toString();
    }

    public static String ndkBuildToJson(String path) throws IOException, InterruptedException {
        StringBuilder total = new StringBuilder();
        total.append(getNdkResult(path, "NDK_DEBUG=0"));
        total.append(getNdkResult(path, "NDK_DEBUG=1"));

        Parser parser = new Parser();
        List<Parser.Node> nodes = parser.parse(total.toString());
        List<ClassifyCommands.Command> commands = ClassifyCommands.accept(nodes);
        ListMultimap<String, Set<AnalyzeFlow.CommandInput>> io = AnalyzeFlow.accept(commands);
        String json = GenerateJson.accept("/projects/MyProject/jni/Android.mk", io);
        return json.toString();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.printf(ndkBuildToJson(args[0]));
    }

    private static class StreamGobbler extends Thread {
        InputStream is;
        String type;
        StringBuilder output = new StringBuilder();

        StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    output.append(line);
                    output.append("\n");
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
