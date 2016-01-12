import java.util.*;

/**
 * Created by jomof on 1/8/16.
 */
public class AnalyzeFlow {

    // Map key = output, map value = inputs
    static Map<File, Set<File>> accept(List<ClassifyCommands.Command> commands) {

        // Gather output -> inputs map
        Map<File, Set<File>> outToIn = new HashMap<File, Set<File>>();
        for (ClassifyCommands.Command command : commands) {
            for (String output : command.outputs) {
                Set<File> inputs = outToIn.get(output);
                if (inputs == null) {
                    inputs = new HashSet<File>();
                    outToIn.put(new File(output, command), inputs);
                }
                for (String input : command.inputs) {
                    inputs.add(new File(input, command));
                }
            }
        }

        // Reduce to just external inputs
        Set<File> externalOutputs = new HashSet<File>();
        externalOutputs.addAll(outToIn.keySet());

        for (ClassifyCommands.Command command : commands) {
            for (String input : command.inputs) {
                externalOutputs.remove(new File(input, command));
            }
        }

        // Follow outputs to terminal leafs
        Map<File, Set<File>> io = new HashMap<File, Set<File>>();
        for (File output : externalOutputs) {
            Set<File> leafInputs = new HashSet<File>();
            follow(output, outToIn, leafInputs);
            io.put(output, leafInputs);
        }

        return io;
    }

    private static void follow(File output, Map<File, Set<File>> outToIn, Set<File> leafInputs) {
        Set<File> inputs = outToIn.get(output);
        for (File input : inputs) {
            if (outToIn.containsKey(input)) {
                follow(input, outToIn, leafInputs);
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
            return String.format("%s <- %s", filename, command);
        }
    }
}
