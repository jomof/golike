import java.util.*;

/**
 * Created by jomof on 1/8/16.
 */
public class AnalyzeFlow {

    static Map<String, Set<String>> accept(List<ClassifyCommands.Command> commands) {

        // Gather output -> inputs map
        Map<String, Set<String>> outToIn = new HashMap<String, Set<String>>();
        for (ClassifyCommands.Command command : commands) {
            for (String output : command.outputs) {
                Set<String> inputs = outToIn.get(output);
                if (inputs == null) {
                    inputs = new HashSet<String>();
                    outToIn.put(output, inputs);
                }
                inputs.addAll(command.inputs);
            }
        }

        // Reduce to just external inputs
        Set<String> externalOutputs = new HashSet<String>();
        externalOutputs.addAll(outToIn.keySet());

        for (ClassifyCommands.Command command : commands) {
            for (String input : command.inputs) {
                externalOutputs.remove(input);
            }
        }

        // Follow outputs to terminal leafs
        Map<String, Set<String>> io = new HashMap<String, Set<String>>();
        for (String output : externalOutputs) {
            Set<String> leafInputs = new HashSet<String>();
            follow(output, outToIn, leafInputs);
            io.put(output, leafInputs);
        }

        return io;
    }

    private static void follow(String output, Map<String, Set<String>> outToIn, Set<String> leafInputs) {
        Set<String> inputs = outToIn.get(output);
        for (String input : inputs) {
            if (outToIn.containsKey(input)) {
                follow(input, outToIn, leafInputs);
                continue;
            }
            leafInputs.add(input);
        }
    }
}
