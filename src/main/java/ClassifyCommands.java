import java.util.ArrayList;
import java.util.List;
import static Parser.Node;
import static Parser.CommandExpression;

/**
 * Created by jomof on 1/8/16.
 */
class ClassifyCommands {

    class Command {
        final List<String> inputs;
        final List<String> outputs;
        Command(List<String> inputs, List<String> outputs) {
            this.inputs = inputs;
            this.outputs = outputs;
        }
    }

    interface CommandClassifier {
        boolean isMatch(CommandExpression command);
        Command createCommand(CommandExpression command);
    }

    class GccClassifier implements CommandClassifier {
        @Override
        public boolean isMatch(CommandExpression command) {
            if (command.)
            return false;
        }

        @Override
        public Command createCommand(CommandExpression command) {
            return null;
        }
    }

    private static CommandClassifier classifiers[] = {};

    static void accept(List<Node> nodes) {
        List<Command> commands = new ArrayList<Command>();

        for(Node node : nodes) {
            if (node.type != Parser.Type.TYPE_COMMAND_EXPRESSION) {
                continue;
            }
            CommandExpression expr = (CommandExpression) node
            for (CommandClassifier classifier : classifiers) {
                if (classifier.isMatch(node)) {
                    commands.add(classifier.createCommand(node));
                }
            }
        }
    }
}
