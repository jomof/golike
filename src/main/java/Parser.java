import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jomof on 12/11/15.
 */
class Parser {
    private LinkedList<LinkedList<Node>> stack_ =  new LinkedList<LinkedList<Node>>();

    private static boolean isBlockable(Node node) {
        return node.type == Type.TYPE_IDENTIFIER_EXPRESSION
                || node.type == Type.TYPE_BLOCK_EXPRESSION
                || node.type == Type.TYPE_MACRO_EXPRESSION
                || node.type == Type.TYPE_CONCAT_EXPRESSION
                || node.type == Type.TYPE_SIMPLE_ASSIGN_EXPRESSION;
    }

    private static boolean isConcatable(Node node) {
        return node.type == Type.TYPE_IDENTIFIER_EXPRESSION
                || node.type == Type.TYPE_MACRO_EXPRESSION
                || node.type == Type.TYPE_CONCAT_EXPRESSION;
    }

    private static boolean isRvalue(Node node) {
        return node.type == Type.TYPE_IDENTIFIER_EXPRESSION
                || node.type == Type.TYPE_SIMPLE_ASSIGN_EXPRESSION
                || node.type == Type.TYPE_BLOCK_EXPRESSION
                || node.type == Type.TYPE_MACRO_EXPRESSION
                || node.type == Type.TYPE_CONCAT_EXPRESSION;
    }

    private static boolean isLvalue(Node node) {
        return node.type == Type.TYPE_IDENTIFIER_EXPRESSION;
    }

    private static boolean isAssignmentOperator(Type type) {
        return type == Type.TYPE_SIMPLE_ASSIGN_OPERATOR
                || type == Type.TYPE_APPEND_ASSIGN_OPERATOR
                || type == Type.TYPE_EQUALS_OPERATOR;

    }

    private static void blockAdd(List<Node> expressions, Node node) {
        switch (node.type) {
            case TYPE_BLOCK_EXPRESSION:
                expressions.addAll(((BlockExpression) node).expressions);
                return;
            default:
                expressions.add(node);
                return;
        }
    }

    private static void concatAdd(List<Node> expressions, Node node) {
        switch (node.type) {
            case TYPE_CONCAT_EXPRESSION:
                expressions.addAll(((ConcatExpression) node).expressions);
                return;
            default:
                expressions.add(node);
                return;
        }
    }

    private static BlockExpression blockify(Node left, Node right) {
        List<Node> expressions = new ArrayList<Node>();
        blockAdd(expressions, left);
        blockAdd(expressions, right);
        return new BlockExpression(expressions);
    }

    private static ConcatExpression concatify(Node left, Node right) {
        List<Node> expressions = new ArrayList<Node>();
        concatAdd(expressions, left);
        concatAdd(expressions, right);
        return new ConcatExpression(expressions);
    }

    public List<Node> parse(String string) {
        stack_.push(new LinkedList<Node>());

        Tokenizer.apply(string, new TokenReceiver() {
            @Override
            public void amp() {

            }

            @Override
            public void ampAmp() {

            }

            @Override
            public void append() {
                stack_.get(0).push(new TokenNode(Type.TYPE_APPEND_ASSIGN_OPERATOR, "+="));
            }

            @Override
            public void assign() {
                stack_.get(0).push(new TokenNode(Type.TYPE_SIMPLE_ASSIGN_OPERATOR, ":="));
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
                reduce();
                stack_.get(0).push(new TokenNode(Type.TYPE_CLOSE_PAREN, ")"));
                reduce();
                if (stack_.get(0).size() != 1) {
                    throw new RuntimeException();
                }
                stack_.get(1).push(stack_.get(0).pop());
                stack_.pop();
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
                stack_.push(new LinkedList<Node>());
                stack_.get(0).push(new TokenNode(Type.TYPE_DOLLAR_OPEN_PAREN, "$("));

            }

            @Override
            public void doubleQuote() {

            }

            @Override
            public void endef() {
                stack_.get(0).push(new TokenNode(Type.TYPE_ENDEF_KEYWORD, "endef"));

            }

            @Override
            public void endif() {
                stack_.get(0).push(new TokenNode(Type.TYPE_ENDIF_KEYWORD, "endif"));
            }

            @Override
            public void endline() {
                reduce();
            }

            @Override
            public void equals() {
                stack_.get(0).push(new TokenNode(Type.TYPE_EQUALS_OPERATOR, "="));
            }

            @Override
            public void expected(String string) {

            }

            @Override
            public void greaterThan() {

            }

            @Override
            public void identifier(String identifier) {
                stack_.get(0).push(new IdentifierExpression(identifier));
            }

            @Override
            public void ifdef() {
                stack_.get(0).push(new TokenNode(Type.TYPE_IFDEF_KEYWORD, "ifdef"));
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
                stack_.get(0).push(new TokenNode(Type.TYPE_WHITESPACE, null));
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

    private Node popSave(LinkedList<Node> save) {
        Node result = stack_.get(0).pop();
        save.push(result);
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

        reduceBlocks();
        reduceConcats();
        reduceAssignmentExpressions();
        reduceTokens();

    }

    private void reduceAssignmentExpressions() {
        if (stack_.get(0).size() < 3) {
            return;
        }
        LinkedList<Node> save = new LinkedList<Node>();
        try {
            Node node = popIgnoreWhitespaceSave(save);
            if (isRvalue(node) && stack_.get(0).size() > 0) {
                Node node2 = popIgnoreWhitespaceSave(save);
                if (isAssignmentOperator(node2.type)) {
                    Node node3 = popIgnoreWhitespaceSave(save);
                    if (isLvalue(node3)) {
                        pushSave(new AssignmentExpression(node2.type, (IdentifierExpression) node3, node), save);
                        reduce();
                    }
                }
            }

        } finally {
            for (Node node : save) {
                stack_.get(0).push(node);
            }
            save.clear();
        }
    }

    private void reduceBlocks() {
        if (stack_.get(0).size() == 0) {
            return;
        }
        LinkedList<Node> save = new LinkedList<Node>();

        try {
            Node node = popIgnoreWhitespaceSave(save);
            if (isBlockable(node) && stack_.get(0).size() > 0) {
                Node node2 = popSave(save);
                if (node2.type == Type.TYPE_WHITESPACE && stack_.get(0).size() > 0) {
                    Node node3 = popSave(save);
                    if (isBlockable(node3)) {
                        pushSave(blockify(node3, node), save);
                        reduce();
                    }
                }
            }

        } finally {
            for (Node node : save) {
                stack_.get(0).push(node);
            }
            save.clear();
        }
    }

    private void reduceConcats() {
        if (stack_.get(0).size() == 0) {
            return;
        }
        LinkedList<Node> save = new LinkedList<Node>();
        try {
            Node node = popIgnoreWhitespaceSave(save);
            if (isConcatable(node) && stack_.get(0).size() > 0) {
                Node node2 = popSave(save);
                if (isConcatable(node2)) {
                    pushSave(concatify(node2, node), save);
                    reduce();
                }
            }

        } finally {
            for (Node node : save) {
                stack_.get(0).push(node);
            }
            save.clear();
        }
    }

    private void reduceTokens() {
        LinkedList<Node> save = new LinkedList<Node>();
        try {
            Node node = popIgnoreWhitespaceSave(save);
            switch (node.type) {
                case TYPE_CLOSE_PAREN: {
                    Node node2 = popIgnoreWhitespaceSave(save);
                    Node node3 = popIgnoreWhitespaceSave(save);
                    switch (node3.type) {
                        case TYPE_DOLLAR_OPEN_PAREN: {
                            pushSave(new MacroExpression(node2), save);
                            reduce();
                            return;
                        }
                        default:
                            throw new RuntimeException(node2.type.toString());
                    }
                }
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
                    Node node2 = popIgnoreWhitespaceSave(save);
                    if (node2 == null) {
                        return;
                    }

                    switch (node2.type) {
                        case TYPE_DOLLAR_OPEN_PAREN:
                        case TYPE_IFDEF_FRAGMENT:
                            return;
                        default:
                            throw new RuntimeException(node2.type.toString());
                    }
                }
                case TYPE_IDENTIFIER_EXPRESSION: {
                    IdentifierExpression identifier = (IdentifierExpression) node;
                    Node node2 = popIgnoreWhitespaceSave(save);
                    if (node2 == null) {
                        return;
                    }

                    switch (node2.type) {
                        case TYPE_IFDEF_KEYWORD: {
                            pushSave(new IfDefFragment(identifier.identifier), save);
                            reduce();
                            return;
                        }
                        default:
                            return;
                    }
                }
                default:
                    return;
            }
        } finally {
            for (Node node : save) {
                stack_.get(0).push(node);
            }
        }

    }

    enum Type {
        TYPE_WHITESPACE,
        TYPE_DOLLAR_OPEN_PAREN,
        TYPE_CLOSE_PAREN,
        TYPE_IFDEF_KEYWORD,
        TYPE_IFDEF_FRAGMENT, // ifdef <block or identifier>
        TYPE_IFDEF_EXPRESSSION,
        TYPE_ENDEF_KEYWORD,
        TYPE_ENDIF_KEYWORD,
        TYPE_SIMPLE_ASSIGN_OPERATOR,
        TYPE_APPEND_ASSIGN_OPERATOR,
        TYPE_EQUALS_OPERATOR,
        TYPE_SIMPLE_ASSIGN_EXPRESSION,
        TYPE_BLOCK_EXPRESSION,
        TYPE_IDENTIFIER_EXPRESSION,
        TYPE_MACRO_EXPRESSION,
        TYPE_CONCAT_EXPRESSION
    }

    static class Node {
        final Type type;

        Node(Type type) {
            this.type = type;
        }
    }

    static class TokenNode extends Node {
        final String string;

        TokenNode(Type type, String string) {
            super(type);
            this.string = string;
        }
    }

    static class IdentifierExpression extends Node {
        final String identifier;

        IdentifierExpression(String identifier) {
            super(Type.TYPE_IDENTIFIER_EXPRESSION);
            this.identifier = identifier;
        }
    }

    static class IfDefFragment extends Node {
        final String identifier;

        IfDefFragment(String identifier) {
            super(Type.TYPE_IFDEF_FRAGMENT);
            this.identifier = identifier;
        }
    }

    static class MacroExpression extends Node {
        final Node body;

        MacroExpression(Node body) {
            super(Type.TYPE_MACRO_EXPRESSION);
            this.body = body;
        }
    }

    static class IfDefExpression extends Node {
        final String identifier;
        final Node body;

        IfDefExpression(String identifier, Node body) {
            super(Type.TYPE_IFDEF_EXPRESSSION);
            this.identifier = identifier;
            this.body = body;
        }
    }

    static class BlockExpression extends Node {
        final List<Node> expressions;

        BlockExpression(List<Node> expressions) {
            super(Type.TYPE_BLOCK_EXPRESSION);
            this.expressions = expressions;
        }
    }

    static class ConcatExpression extends Node {
        final List<Node> expressions;

        ConcatExpression(List<Node> expressions) {
            super(Type.TYPE_CONCAT_EXPRESSION);
            this.expressions = expressions;
        }
    }

    static class AssignmentExpression extends Node {
        final Type operator;
        final IdentifierExpression left;
        final Node right;

        AssignmentExpression(Type operator, IdentifierExpression left, Node right) {
            super(Type.TYPE_SIMPLE_ASSIGN_EXPRESSION);
            if (!isAssignmentOperator(operator)) {
                throw new RuntimeException(operator.toString());
            }
            this.operator = operator;
            this.left = left;
            this.right = right;
        }
    }
}
