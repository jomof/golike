import java.util.*;

/**
 * Created by jomof on 1/8/16.
 */
public class AnalyzeFlow {

    // Map key = output, map value = inputs
    static Map<String, Set<File>> accept(List<ClassifyCommands.Command> commands) {

        // Gather output -> inputs map
        Map<String, Set<File>> outToIn = new HashMap<String, Set<File>>();
        for (ClassifyCommands.Command command : commands) {
            for (String output : command.outputs) {
                Set<File> inputs = outToIn.get(output);
                if (inputs == null) {
                    inputs = new HashSet<File>();
                    outToIn.put(output, inputs);
                }
                for (String input : command.inputs) {
                    inputs.add(new File(input, command));
                }
            }
        }

        // Reduce to just external inputs
        Set<String> externalOutputs = new HashSet<String>();
        externalOutputs.addAll(outToIn.keySet());

        for (ClassifyCommands.Command command : commands) {
            for (final String input : command.inputs) {
                externalOutputs.remove(input);
            }
        }

        // Follow outputs to terminal leafs
        Map<String, Set<File>> io = new HashMap<String, Set<File>>();
        for (String output : externalOutputs) {
            Set<File> leafInputs = new HashSet<File>();
            follow(output, outToIn, leafInputs);
            io.put(output, leafInputs);
        }

        return io;
    }

    private static void follow(String output, Map<String, Set<File>> outToIn, Set<File> leafInputs) {
        Set<File> inputs = outToIn.get(output);
        for (File input : inputs) {
            if (outToIn.containsKey(input.filename)) {
                follow(input.filename, outToIn, leafInputs);
                continue;
            }
            leafInputs.add(input);
        }
    }

    static class File {
        public String filename;
        public ClassifyCommands.Command command;

        File(String filename, ClassifyCommands.Command command) {
            this.filename = filename;
            this.command = command;
        }

        @Override
        public boolean equals(Object that) {
            if (that == null || !(that instanceof File)) {
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
