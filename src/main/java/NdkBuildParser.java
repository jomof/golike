import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jomof on 12/11/15.
 */
public class NdkBuildParser {
    enum Type {
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
    };

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

    private LinkedList<Node> stack = new LinkedList<Node>();

    private BlockExpression ididToBlock(IdentifierExpression left, IdentifierExpression right) {
        List<Node> expressions = new ArrayList<Node>();
        expressions.add(left);
        expressions.add(right);
        return new BlockExpression(expressions);
    }

    private void reduce() {

        Node node = stack.pop();
        switch (node.type) {
            case TYPE_BLOCK_EXPRESSION: {
                Node node2 = stack.pop();
                switch (node2.type) {
                    case TYPE_SIMPLE_ASSIGN_OPERATOR:
                        Node node3 = stack.pop();
                        switch (node3.type) {
                            case TYPE_IDENTIFIER_EXPRESSION:
                                stack.push(new AssignmentExpression((IdentifierExpression) node3, node));
                                reduce();
                                return;
                        }

                    default:
                        throw new RuntimeException();
                }
            }
            case TYPE_IDENTIFIER_EXPRESSION: {
                Node node2 = stack.pop();
                switch (node2.type) {
                    case TYPE_IDENTIFIER_EXPRESSION:
                        stack.push(ididToBlock((IdentifierExpression) node2, (IdentifierExpression) node));
                        reduce();
                        return;
                    case TYPE_SIMPLE_ASSIGN_OPERATOR:
                        Node node3 = stack.pop();
                        switch (node3.type) {
                            case TYPE_IDENTIFIER_EXPRESSION:
                                stack.push(new AssignmentExpression((IdentifierExpression) node3, node));
                                reduce();
                                return;
                        }

                }
            }
            default:
                stack.push(node);
                return;
        }
    }


    Node parse(String string) {
        NdkBuildTokenizer.apply(string, new NdkBuildTokenReceiver() {
            @Override
            public void whitespace(String whitespace) {

            }

            @Override
            public void comment(String comment) {

            }

            @Override
            public void include() {

            }

            @Override
            public void define() {

            }

            @Override
            public void endef() {

            }

            @Override
            public void identifier(String identifier) {
                stack.push(new IdentifierExpression(identifier));
            }

            @Override
            public void number(String number) {

            }

            @Override
            public void at() {

            }

            @Override
            public void equals() {

            }

            @Override
            public void assign() {
                stack.push(new TokenNode(Type.TYPE_SIMPLE_ASSIGN_OPERATOR, ":="));
            }

            @Override
            public void assignConditional() {

            }

            @Override
            public void append() {

            }

            @Override
            public void ifeq() {

            }

            @Override
            public void ifneq() {

            }

            @Override
            public void endif() {

            }

            @Override
            public void dollarOpenParen() {

            }

            @Override
            public void openParen() {

            }

            @Override
            public void closeParen() {

            }

            @Override
            public void openBracket() {

            }

            @Override
            public void closeBracket() {

            }

            @Override
            public void lessThan() {

            }

            @Override
            public void greaterThan() {

            }

            @Override
            public void pipe() {

            }

            @Override
            public void star() {

            }

            @Override
            public void semicolon() {

            }

            @Override
            public void plus() {

            }

            @Override
            public void amp() {

            }

            @Override
            public void ampAmp() {

            }

            @Override
            public void comma() {

            }

            @Override
            public void doubleQuote() {

            }

            @Override
            public void expected(String string) {

            }

            @Override
            public void endline() {
                reduce();
            }
        });

        reduce();
        if (stack.size() != 1) {
            throw new RuntimeException();
        }

        return stack.get(0);
    }


}
