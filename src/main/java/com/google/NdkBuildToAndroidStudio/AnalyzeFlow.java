package com.google.NdkBuildToAndroidStudio;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.*;

/**
 * Created by jomof on 1/8/16.
 */
public class AnalyzeFlow {

    // Map key = output, map value = inputs
    public static ListMultimap<String, Set<CommandInput>> accept(List<ClassifyCommands.Command> commands) {

        // For each filename, record the last command that created it.
        Map<String, Integer> outputToCommand = new HashMap<String, Integer>();

        // For each command, the set of terminal inputs.
        Map<Integer, Set<CommandInput>> outputToTerminals = new HashMap<Integer, Set<CommandInput>>();

        // For each command, the set of outputs that was consumed.
        Map<Integer, Set<String>> commandOutputsConsumed = new HashMap<Integer, Set<String>>();

        for (int i = 0; i < commands.size(); ++i) {
            ClassifyCommands.Command current = commands.get(i);
            commandOutputsConsumed.put(i, new HashSet<String>());

            // For each input, find the line the created it or null if this is a terminal input.
            Set<CommandInput> terminals = new HashSet<CommandInput>();
            for (String input : current.inputs) {
                if (outputToCommand.containsKey(input)) {
                    int inputCommandIndex = outputToCommand.get(input);
                    terminals.addAll(outputToTerminals.get(inputCommandIndex));

                    // Record this a consumed output.
                    commandOutputsConsumed.get(inputCommandIndex).add(input);
                    continue;
                }
                terminals.add(new CommandInput(input, current));
            }
            outputToTerminals.put(i, terminals);

            // Record the files output by this command
            for (String output : current.outputs) {
                outputToCommand.put(output, i);
            }
        }

        // Emit the outputs that are never consumed.
        ListMultimap<String, Set<CommandInput>> result = ArrayListMultimap.create();
        for (int i = 0; i < commands.size(); ++i) {
            ClassifyCommands.Command current = commands.get(i);
            Set<String> outputsConsumed = commandOutputsConsumed.get(i);
            for (String output : current.outputs) {
                if (!outputsConsumed.contains(output)) {
                    result.put(output, outputToTerminals.get(i));
                }
            }
        }
        return result;
    }


    public static class CommandInput {
        public final String filename;
        public final ClassifyCommands.Command command;

        CommandInput(String filename, ClassifyCommands.Command command) {
            this.filename = filename;
            this.command = command;
        }

        @Override
        public boolean equals(Object that) {
            if (that == null || !(that instanceof CommandInput)) {
                return false;
            }
            return toString().equals(that.toString());
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public String toString() {
            return String.format("%s -> %s", filename, command);
        }
    }
}
