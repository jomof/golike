import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Find compiler commands (g++, gcc, clang) and extract inputs and outputs according to the command line rules of that
 * tool
 */
class ClassifyCommands {

    static class Command {
        final List<String> inputs;
        final List<String> outputs;
        Command(List<String> inputs, List<String> outputs) {
            this.inputs = inputs;
            this.outputs = outputs;
        }
    }

    interface CommandClassifier {
        boolean isMatch(Parser.CommandExpression command);
        Command createCommand(Parser.CommandExpression command);
    }

    static class GccArClassifier implements CommandClassifier {

        @Override
        public boolean isMatch(Parser.CommandExpression command) {
            return command.command.endsWith("gcc-ar");
        }

        @Override
        public Command createCommand(Parser.CommandExpression command) {
            List<String> inputs = new ArrayList<String>();
            List<String> outputs = new ArrayList<String>();
            String last = null;
            int count = 0;
            for (Parser.ArgumentExpression arg : command.args) {
                if (arg.arg.startsWith("-")) {
                    continue;
                }
                if (count == 0) {
                    ++count; // Skip command flags
                    continue;
                }
                if (count == 1) {
                    outputs.add(arg.arg);
                    ++count;
                    continue;
                }
                inputs.add(arg.arg);

            }
            return new Command(inputs, outputs);
        }
    }

    static class GccClassifier implements CommandClassifier {
        private static List<String> outputFlags = Arrays.asList("-o");
        private static List<String> ignoreFlags = Arrays.asList("-F", "-I", "-MF", "-MQ", "-MT");

        @Override
        public boolean isMatch(Parser.CommandExpression command) {
            return command.command.endsWith("gcc")
                    || command.command.endsWith("gcc-ar")
                    || command.command.endsWith("g++");
        }

        @Override
        public Command createCommand(Parser.CommandExpression command) {
            List<String> inputs = new ArrayList<String>();
            List<String> outputs = new ArrayList<String>();
            String last = null;
            for (Parser.ArgumentExpression arg : command.args) {
                if (arg.arg.startsWith("-")) {
                    last = arg.arg;
                    continue;
                }
                if (outputFlags.contains(last)) {
                    outputs.add(arg.arg);
                    continue;
                }
                if (ignoreFlags.contains(last)) {
                    continue;
                }
                inputs.add(arg.arg);

            }
            return new Command(inputs, outputs);
        }
    }

    private static CommandClassifier classifiers[] = {
            new GccClassifier(),
            new GccArClassifier()
    };

    static List<Command> accept(List<Parser.Node> nodes) {
        List<Command> commands = new ArrayList<Command>();

        for(Parser.Node node : nodes) {
            if (node.type != Parser.Type.TYPE_COMMAND_EXPRESSION) {
                continue;
            }
            Parser.CommandExpression expr = (Parser.CommandExpression) node;
            for (CommandClassifier classifier : classifiers) {
                if (classifier.isMatch(expr)) {
                    commands.add(classifier.createCommand(expr));
                }
            }
        }
        return commands;
    }
}
