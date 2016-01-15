package com.google.NdkBuildToAndroidStudio;

/**
 * Created by jomof on 1/4/16.
 */
public class Tokenizer {
    private State state = State.FIRST;

    private static int readWhitespace(String string, int index, TokenReceiver receiver) {
        int start = index;
        while(index < string.length()
                && CharUtil.isWhitespace(string.charAt(index))) {
            ++index;
        }

        receiver.whitespace(string.substring(start, index));

        if (index == string.length()) {
            return -1;
        }
        return index;
    }

    private static int readComment(String string, int index, TokenReceiver receiver) {
        int start = index;
        while(index < string.length()
                && Character.getType(string.charAt(index)) != Character.CONTROL) {
            ++index;
        }

        receiver.comment(string.substring(start, index));

        if (index == string.length()) {
            return -1;
        }
        return index;
    }

    public static void apply(String string, TokenReceiver receiver) {
        Tokenizer tokenizer = new Tokenizer();
        int index = 0;
        string = Decontinuizer.apply(string);
        while (index != -1 && index != string.length()) {
            index = tokenizer.readToken(string, index, receiver);
        }
        receiver.endline();
    }

    private void commandOrArgument(TokenReceiver receiver, String identifier) {
        switch (state) {
            case FIRST:
                receiver.command(identifier);
                state = State.NOT_FIRST;
                break;
            case NOT_FIRST:
                receiver.argument(identifier);
                break;
            default:
                throw new RuntimeException(state.toString());

        }
    }

    private int readIdentifier(String string, int index, TokenReceiver receiver) {
        int start = index;
        ++index;
        while(index < string.length()
                && !CharUtil.isWhitespace(string.charAt(index))
                && Character.getType(string.charAt(index)) != Character.CONTROL) {
            ++index;
        }

        String identifier = string.substring(start, index);

        commandOrArgument(receiver, identifier);

        if (index == string.length()) {
            return -1;
        }
        return index;
    }

    private int readString(String string, int index, TokenReceiver receiver) {
        int start = index;
        ++index;
        while (index < string.length()
                && string.charAt(index) != '\"') {
            ++index;
        }
        ++index;

        commandOrArgument(receiver, string.substring(start, index));

        if (index == string.length()) {
            return -1;
        }
        return index;
    }

    private int readToken(String string, int index, TokenReceiver receiver) {
        if (string.length() == index) {
            return -1;
        }
        switch (string.charAt(index)) {
            case '#':
                return readComment(string, index, receiver);
            case ' ':
                return readWhitespace(string, index, receiver);
            case '\"':
                return readString(string, index, receiver);
            case '\r':
            case '\n':
                receiver.endline();
                ++index;
                state = State.FIRST;
                return index;
        }

        if (CharUtil.isWhitespace(string.charAt(index))) {
            return readWhitespace(string, index, receiver);
        }

        return readIdentifier(string, index, receiver);
    }

    private enum State {
        FIRST,
        NOT_FIRST
    }
}
