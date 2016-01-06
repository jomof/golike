import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jomof on 12/11/15.
 */
public class NdkBuildParser {
    private LinkedList<Node> stack_ = new LinkedList<Node>();

    private BlockExpression ididToBlock(IdentifierExpression left, IdentifierExpression right) {
        List<Node> expressions = new ArrayList<Node>();
        expressions.add(left);
        expressions.add(right);
        return new BlockExpression(expressions);
    };

    public List<Node> parse(String string) {
        NdkBuildTokenizer.apply(string, new NdkBuildTokenReceiver() {
            @Override
            public void amp() {

            }

            @Override
            public void ampAmp() {

            }

            @Override
            public void append() {

            }

            @Override
            public void assign() {
                stack_.push(new TokenNode(Type.TYPE_SIMPLE_ASSIGN_OPERATOR, ":="));
            }

            @Override
            public void assignConditional() {

            }

            @Override
            public void at() {

            }

            @Override
            public void closeBracket() {

            }

            @Override
            public void closeParen() {

            }

            @Override
            public void comma() {

            }

            @Override
            public void comment(String comment) {

            }

            @Override
            public void define() {

            }

            @Override
            public void dollarOpenParen() {

            }

            @Override
            public void doubleQuote() {

            }

            @Override
            public void endef() {
                stack_.push(new TokenNode(Type.TYPE_ENDEF_KEYWORD, "endef"));

            }

            @Override
            public void endif() {
                stack_.push(new TokenNode(Type.TYPE_ENDIF_KEYWORD, "endif"));
            }

            @Override
            public void endline() {
                reduce();
            }

            @Override
            public void equals() {

            }

            @Override
            public void expected(String string) {

            }

            @Override
            public void greaterThan() {

            }

            @Override
            public void identifier(String identifier) {
                stack_.push(new IdentifierExpression(identifier));
            }

            @Override
            public void ifdef() {
                stack_.push(new TokenNode(Type.TYPE_IFDEF_KEYWORD, "ifdef"));
            }

            @Override
            public void ifeq() {

            }

            @Override
            public void ifneq() {

            }

            @Override
            public void include() {

            }

            @Override
            public void lessThan() {

            }

            @Override
            public void number(String number) {

            }

            @Override
            public void openBracket() {

            }

            @Override
            public void openParen() {

            }

            @Override
            public void pipe() {

            }

            @Override
            public void plus() {

            }

            @Override
            public void semicolon() {

            }

            @Override
            public void star() {

            }

            @Override
            public void whitespace(String whitespace) {
               stack_.push(new TokenNode(Type.TYPE_WHITESPACE, null));
            }
        });

        reduce();
        return stack_;
    }

    private void reduce() {
        if (stack_.size() <= 1) {
            return;
        }
        LinkedList<Node> save = new LinkedList<Node>();
        try {
            Node node = popIgnoreWhitespaceSave(save);
            switch (node.type) {
                case TYPE_ENDEF_KEYWORD: {
                    Node node2 = popSave(save);
                    Node node3 = popIgnoreWhitespaceSave(save);
                    if (node3.type != Type.TYPE_IFDEF_FRAGMENT) {
                        throw new RuntimeException(node3.type.toString());
                    }
                    IfDefFragment expression2 = (IfDefFragment) node3;
                    pushSave(new IfDefExpression(expression2.identifier, node2), save);
                    reduce();
                    return;
                }
                case TYPE_BLOCK_EXPRESSION: {
                    if (stack_.size() == 0) {
                        return;
                    }
                    Node node2 = popSave(save);

                    if (node2.type == Type.TYPE_WHITESPACE) {
                        Node node3 = popSave(save);
                        switch(node3.type) {
                            case TYPE_IDENTIFIER_EXPRESSION: {
                                pushSave(blockIdToBlock((BlockExpression) node, (IdentifierExpression) node3), save);
                                reduce();
                                return;
                            }
                        }
                        node2 = node3; // Eat the whitespace and continue
                    }

                    switch (node2.type) {
                        case TYPE_SIMPLE_ASSIGN_OPERATOR:
                            Node node3 = popIgnoreWhitespaceSave(save);
                            switch (node3.type) {
                                case TYPE_IDENTIFIER_EXPRESSION:
                                    pushSave(new AssignmentExpression((IdentifierExpression) node3, node), save);
                                    reduce();
                                    return;
                                default:
                                    throw new RuntimeException(node2.type.toString());
                            }

                        case TYPE_IFDEF_FRAGMENT:
                            return;
                        default:
                            throw new RuntimeException(node2.type.toString());
                    }
                }
                case TYPE_IDENTIFIER_EXPRESSION: {
                    IdentifierExpression identifier = (IdentifierExpression) node;
                    Node node2 = popSave(save);
                    if (node2.type == Type.TYPE_WHITESPACE) {
                        Node node3 = popSave(save);
                        if (node3.type == Type.TYPE_IDENTIFIER_EXPRESSION) {
                            pushSave(ididToBlock((IdentifierExpression) node3, identifier), save);
                            reduce();
                            return;
                        }

                        node2 = node3;
                    }
                    switch(node2.type) {
                        case TYPE_SIMPLE_ASSIGN_OPERATOR:
                            Node node3 = popIgnoreWhitespaceSave(save);
                            switch (node3.type) {
                                case TYPE_IDENTIFIER_EXPRESSION:
                                    pushSave(new AssignmentExpression((IdentifierExpression) node3, node), save);
                                    reduce();
                                    return;
                                default:
                                    throw new RuntimeException(node2.type.toString());
                            }
                        case TYPE_IFDEF_KEYWORD: {
                            pushSave(new IfDefFragment(identifier.identifier), save);
                            reduce();
                            return;
                        }
                        default:
                            throw new RuntimeException(node2.type.toString());
                    }
                }
                default:
                    return;
            }
        } finally {
            for (Node node : save) {
                stack_.push(node);
            }
        }
    }


    private Node popSave(LinkedList<Node> save) {
        Node result = stack_.pop();
        save.push(result);
        return result;
    }

    private Node popIgnoreWhitespaceSave(LinkedList<Node> save) {
        Node result = stack_.pop();
        save.push(result);
        if (result.type == Type.TYPE_WHITESPACE) {
            result = stack_.pop();
            save.push(result);
        }
        return result;
    }

    private void pushSave(Node node, LinkedList<Node> save) {
        stack_.push(node);
        save.clear();
    }

    private BlockExpression blockIdToBlock(BlockExpression left, IdentifierExpression right) {
        List<Node> expressions = new ArrayList<Node>();
        expressions.add(right);
        expressions.addAll(left.expressions);
        return new BlockExpression(expressions);
    }

    enum Type {
        TYPE_WHITESPACE,
        TYPE_IFDEF_KEYWORD,
        TYPE_IFDEF_FRAGMENT, // ifdef <block or identifier>
        TYPE_IFDEF_EXPRESSSION,
        TYPE_ENDEF_KEYWORD,
        TYPE_ENDIF_KEYWORD,
        TYPE_SIMPLE_ASSIGN_OPERATOR,
        TYPE_SIMPLE_ASSIGN_EXPRESSION,
        TYPE_BLOCK_EXPRESSION,
        TYPE_IDENTIFIER_EXPRESSION
    }

    class Node {
        final Type type;
        Node(Type type) {
            this.type = type;
        }
    }

    class TokenNode extends Node {
        final String string;
        TokenNode(Type type, String string) {
            super(type);
            this.string = string;
        }
    }

    class IdentifierExpression extends Node {
        final String identifier;
        IdentifierExpression(String identifier) {
            super(Type.TYPE_IDENTIFIER_EXPRESSION);
            this.identifier = identifier;
        }
    }

    class IfDefFragment extends Node {
        final String identifier;
        IfDefFragment(String identifier) {
            super(Type.TYPE_IFDEF_FRAGMENT);
            this.identifier = identifier;
        }
    }

    class IfDefExpression extends Node {
        final String identifier;
        final Node body;
        IfDefExpression(String identifier, Node body) {
            super(Type.TYPE_IFDEF_EXPRESSSION);
            this.identifier = identifier;
            this.body = body;
        }
    }

    class BlockExpression extends Node {
        final List<Node> expressions;
        BlockExpression(List<Node> expressions) {
            super(Type.TYPE_BLOCK_EXPRESSION);
            this.expressions = expressions;
        }
    }

    class AssignmentExpression extends Node {
        final IdentifierExpression left;
        final Node right;
        AssignmentExpression(IdentifierExpression left, Node right) {
            super(Type.TYPE_SIMPLE_ASSIGN_EXPRESSION);
            this.left = left;
            this.right = right;
        }
    }
}
