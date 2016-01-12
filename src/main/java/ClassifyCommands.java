import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Find compiler commands (g++, gcc, clang) and extract inputs and outputs according to the command line rules of that
 * tool
 */
class ClassifyCommands {

    private static CommandClassifier classifiers[] = {
            new GccClassifier(),
            new GccArClassifier()
    };

    static List<Command> accept(List<Parser.Node> nodes) {
        List<Command> commands = new ArrayList<Command>();

        for (Parser.Node node : nodes) {
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

    interface CommandClassifier {
        Command createCommand(Parser.CommandExpression command);

        boolean isMatch(Parser.CommandExpression command);
    }

    static class Command {
        final Parser.CommandExpression command;
        final List<String> inputs;
        final List<String> outputs;

        Command(Parser.CommandExpression command, List<String> inputs, List<String> outputs) {
            this.command = command;
            this.inputs = inputs;
            this.outputs = outputs;
        }

        @Override
        public int hashCode() {
            return command.hashCode();
        }

        @Override
        public String toString() {
            return command.toString();
        }
    }

    static class GccArClassifier implements CommandClassifier {

        @Override
        public Command createCommand(Parser.CommandExpression command) {
            List<String> inputs = new ArrayList<String>();
            List<String> outputs = new ArrayList<String>();
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
            return new Command(command, inputs, outputs);
        }

        @Override
        public boolean isMatch(Parser.CommandExpression command) {
            return command.command.endsWith("gcc-ar");
        }
    }

    static class GccClassifier implements CommandClassifier {
        private static List<String> outputFlags = Arrays.asList("-o");
        private static List<String> ignoreFlags = Arrays.asList("-F", "-I", "-MF", "-MQ", "-MT");

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
            return new Command(command, inputs, outputs);
        }

        @Override
        public boolean isMatch(Parser.CommandExpression command) {
            return command.command.endsWith("gcc")
                    || command.command.endsWith("g++");
        }
    }
}
