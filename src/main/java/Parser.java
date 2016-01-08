import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jomof on 12/11/15.
 */
class Parser {
    private LinkedList<LinkedList<Node>> stack_ =  new LinkedList<LinkedList<Node>>();

    public List<Node> parse(String string) {
        stack_.push(new LinkedList<Node>());

        Tokenizer.apply(string, new TokenReceiver() {

            @Override
            public void comment(String comment) {
            }

            @Override
            public void endline() {
                reduce();
            }

            @Override
            public void command(String command) {
                stack_.get(0).push(new CommandExpression(command));
            }

            @Override
            public void argument(String argument) {
                stack_.get(0).push(new ArgumentExpression(argument));
            }

            @Override
            public void whitespace(String whitespace) {
                stack_.get(0).push(new WhitespaceNode());
                reduce();
            }
        });

        reduce();
        if (stack_.size() > 1) {
            throw new RuntimeException(); // Unclosed?
        }
        return stack_.pop();
    }

    private Node popIgnoreWhitespaceSave(LinkedList<Node> save) {
        if (stack_.get(0).size() == 0) {
            return null;
        }
        Node result = stack_.get(0).pop();
        save.push(result);
        if (result.type == Type.TYPE_WHITESPACE) {
            if (stack_.get(0).size() == 0) {
                return null;
            }
            result = stack_.get(0).pop();
            save.push(result);
        }
        return result;
    }

    private void pushSave(Node node, LinkedList<Node> save) {
        stack_.get(0).push(node);
        save.clear();
    }

    private void reduce() {
        if (stack_.get(0).size() <= 1) {
            return;
        }
        reduceTokens();
    }

    private void reduceTokens() {
        LinkedList<Node> save = new LinkedList<Node>();
        try {
            Node node = popIgnoreWhitespaceSave(save);
            switch(node.type) {
                case TYPE_ARGUMENT_EXPRESSION:
                    ArgumentExpression arg = (ArgumentExpression) node;
                    Node node2 = popIgnoreWhitespaceSave(save);
                    if (node2 == null) {
                        return;
                    }
                    switch (node2.type) {
                        case TYPE_COMMAND_EXPRESSION:
                            CommandExpression command = (CommandExpression) node2;
                            List<ArgumentExpression> args = new ArrayList<ArgumentExpression>(command.args);
                            args.add(arg);
                            pushSave(new CommandExpression(command.value, args), save);
                            reduce();
                            break;
                    }
            }
        } finally {
            for (Node node : save) {
                stack_.get(0).push(node);
            }
        }

    }

    enum Type {
        TYPE_WHITESPACE,
        TYPE_COMMAND_EXPRESSION,
        TYPE_ARGUMENT_EXPRESSION,
    }

    static class Node {
        final Type type;
        Node(Type type) {
            this.type = type;
        }
    }

    static class WhitespaceNode extends Node {
        WhitespaceNode() {
            super(Type.TYPE_WHITESPACE);;
        }
    }

    static class ArgumentExpression extends Node {
        final String value;
        ArgumentExpression(String value) {
            super(Type.TYPE_ARGUMENT_EXPRESSION);
            this.value = value;
        }
    }

    static class CommandExpression extends Node {
        final String value;
        final List<ArgumentExpression> args;

        CommandExpression(String value) {
            super(Type.TYPE_COMMAND_EXPRESSION);
            this.value = value;
            this.args = new ArrayList<ArgumentExpression>();
        }

        CommandExpression(String value, List<ArgumentExpression> args) {
            super(Type.TYPE_COMMAND_EXPRESSION);
            this.value = value;
            this.args = args;
        }
    }
}
