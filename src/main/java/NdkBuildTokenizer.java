/**
 * Created by jomof on 1/4/16.
 * @see     Character#COMBINING_SPACING_MARK
 * @see     Character#CONNECTOR_PUNCTUATION
 * @see     Character#CONTROL
 * @see     Character#CURRENCY_SYMBOL
 * @see     Character#DASH_PUNCTUATION
 * @see     Character#DECIMAL_DIGIT_NUMBER
 * @see     Character#ENCLOSING_MARK
 * @see     Character#END_PUNCTUATION
 * @see     Character#FINAL_QUOTE_PUNCTUATION
 * @see     Character#FORMAT
 * @see     Character#INITIAL_QUOTE_PUNCTUATION
 * @see     Character#LETTER_NUMBER
 * @see     Character#LINE_SEPARATOR
 * @see     Character#LOWERCASE_LETTER
 * @see     Character#MATH_SYMBOL
 * @see     Character#MODIFIER_LETTER
 * @see     Character#MODIFIER_SYMBOL
 * @see     Character#NON_SPACING_MARK
 * @see     Character#OTHER_LETTER
 * @see     Character#OTHER_NUMBER
 * @see     Character#OTHER_PUNCTUATION
 * @see     Character#OTHER_SYMBOL
 * @see     Character#PARAGRAPH_SEPARATOR
 * @see     Character#PRIVATE_USE
 * @see     Character#SPACE_SEPARATOR
 * @see     Character#START_PUNCTUATION
 * @see     Character#SURROGATE
 * @see     Character#TITLECASE_LETTER
 * @see     Character#UNASSIGNED
 * @see     Character#UPPERCASE_LETTER
 */
public class NdkBuildTokenizer {

    private static boolean isWhitespace(char c) {
        return c == ' ' || c == '\t';
    }

    private static boolean isIdentifierStart(char c) {
        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || (c == '_') || (c == '-') || (c == '.')
                || (c == '/');

    }

    private static boolean isIdentifier(char c) {
        return isIdentifierStart(c) || (c >= '0' && c <= '9');
    }

    private static int readWhitespace(String string, int index, TokenReceiver receiver) {
        int start = index;
        while(index < string.length()
                && isWhitespace(string.charAt(index))) {
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

    private static int readIdentifier(String string, int index, TokenReceiver receiver) {
        int start = index;
        ++index;
        while(index < string.length()
                && isIdentifier(string.charAt(index))) {
            ++index;
        }

        String identifier = string.substring(start, index);

        if (identifier.equals("include")) {
            receiver.include();
            return index;
        }

        if (identifier.equals("ifeq")) {
            receiver.ifeq();
            return index;
        }

        if (identifier.equals("endif")) {
            receiver.endif();
            return index;
        }

        receiver.identifier(identifier);
        if (index == string.length()) {
            return -1;
        }
        return index;
    }

    private static int readEverythingElse(String string, int index) {
        int start = index;
        while(index < string.length()
                && !isWhitespace(string.charAt(index))) {
            ++index;
        }

        throw new RuntimeException(String.format("{b:%s}", string.substring(start, index)));
    }

    private static int eatPastLineFeed(String string, int index) {
        while(isWhitespace(string.charAt(index))) index++;
        while(string.charAt(index) == '\r') index++;
        if (string.charAt(index) == '\n') {
            index++;
        }
        while(isWhitespace(string.charAt(index))) index++;
        return index;
    }

    private static int readRValue(String string, int index, TokenReceiver receiver) {
        int start = index;
        StringBuilder sb = new StringBuilder();
        while(index < string.length()
                && Character.getType(string.charAt(index)) != Character.CONTROL) {
            if (string.charAt(index) == '\\') {
                sb.append(string.substring(start, index));
                // Make sure there is at least one space delimiting lines
                if (string.charAt(index - 1) != ' ' ) {
                    sb.append(" ");
                }
                start = index = eatPastLineFeed(string, index + 1);
            } else {
                ++index;
            }
        }
        sb.append(string.substring(start, index));
        receiver.rvalue(sb.toString().trim());

        if (index == string.length()) {
            return -1;
        }
        return index;
    }

    private static int readColonOperator(String string, int index, TokenReceiver receiver) {
        ++index;
        if (index == string.length()) {
            return -1;
        }
        switch(string.charAt(index)) {
            case '=':
                ++index;
                receiver.assign();
                if (index == string.length()) {
                    return -1;
                }

                // Remove leading whitespace
                return readRValue(string, index, receiver);
        }
        return readEverythingElse(string, index - 1);
    }

    private static int readPlusOperator(String string, int index, TokenReceiver receiver) {
        ++index;
        if (index == string.length()) {
            return -1;
        }
        switch(string.charAt(index)) {
            case '=':
                ++index;
                receiver.append();
                if (index == string.length()) {
                    return -1;
                }

                // Remove leading whitespace
                return readRValue(string, index, receiver);
        }
        return readEverythingElse(string, index - 1);
    }

    private static int readDollarOperator(String string, int index, TokenReceiver receiver) {
        ++index;
        if (index == string.length()) {
            return -1;
        }
        switch(string.charAt(index)) {
            case '(':
                ++index;
                receiver.dollarOpenParen();
                return index;
        }
        return readEverythingElse(string, index - 1);
    }

    private static int readToken(String string, int index, TokenReceiver receiver) {
        switch (string.charAt(index)) {
            case '#':
                return readComment(string, index, receiver);
            case ' ':
                return readWhitespace(string, index, receiver);
            case ':':
                return readColonOperator(string, index, receiver);
            case '+':
                return readPlusOperator(string, index, receiver);
            case '$':
                return readDollarOperator(string, index, receiver);
            case ',':
                receiver.comma();
                ++index;
                return index;
            case '(':
                receiver.openParen();
                ++index;
                return index;
            case ')':
                receiver.closeParen();
                ++index;
                return index;
            case '\r':
            case '\n':
                receiver.endline();
                ++index;
                return index;
        }
        if (isWhitespace(string.charAt(index))) {
            return readWhitespace(string, index, receiver);
        }
        if (isIdentifierStart(string.charAt(index))) {
            return readIdentifier(string, index, receiver);
        }

        return readEverythingElse(string, index);
    }

    public static void apply(String string, TokenReceiver receiver) {
        int index = 0;
        while(index != -1) {
            index = readToken(string, index, receiver);
            if (index == string.length()) {
                return;
            }

        }
    }
}
