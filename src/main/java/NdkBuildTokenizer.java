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


    private static int readWhitespace(String string, int index, NdkBuildTokenReceiver receiver) {
        int start = index;
        while(index < string.length()
                && NdkBuildCharUtil.isWhitespace(string.charAt(index))) {
            ++index;
        }

        receiver.whitespace(string.substring(start, index));

        if (index == string.length()) {
            return -1;
        }
        return index;
    }

    private static int readComment(String string, int index, NdkBuildTokenReceiver receiver) {
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

    private static int readNumber(String string, int index, NdkBuildTokenReceiver receiver) {
        int start = index;
        ++index;
        while (index < string.length()
                && NdkBuildCharUtil.isNumber(string.charAt(index))) {
            ++index;
        }
        receiver.number(string.substring(start, index));
        return index;
    }

    private static int readIdentifier(String string, int index, NdkBuildTokenReceiver receiver) {
        int start = index;
        ++index;
        while(index < string.length()
                && NdkBuildCharUtil.isIdentifier(string.charAt(index))) {
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

        if (identifier.equals("ifneq")) {
            receiver.ifneq();
            return index;
        }

        if (identifier.equals("endif")) {
            receiver.endif();
            return index;
        }

        if (identifier.equals("define")) {
            receiver.define();
            return index;
        }

        if (identifier.equals("endef")) {
            receiver.endef();
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
                && !NdkBuildCharUtil.isWhitespace(string.charAt(index))) {
            ++index;
        }

        throw new RuntimeException(String.format("{b:%s}", string.substring(start, index)));
    }

    private static int readRValue_(String string, int index, NdkBuildTokenReceiver receiver) {
        int start = index;
        StringBuilder sb = new StringBuilder();
        while(index < string.length()
                && Character.getType(string.charAt(index)) != Character.CONTROL) {
            ++index;
        }
        sb.append(string.substring(start, index));

        // Recursively parse the RValue
        apply(string.substring(start, index), receiver);

        if (index == string.length()) {
            return -1;
        }
        return index;
    }

    private static int readColonOperator(String string, int index, NdkBuildTokenReceiver receiver) {
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
                return index;
        }
        receiver.expected(":");
        return index;
    }

    private static int readEqualsOperator(String string, int index, NdkBuildTokenReceiver receiver) {
        ++index;
        if (index == string.length()) {
            return -1;
        }
        ++index;
        receiver.equals();
        if (index == string.length()) {
            return -1;
        }

        return index;
    }

    private static int readPlusOperator(String string, int index, NdkBuildTokenReceiver receiver) {
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

                return index;
        }
        receiver.plus();
        return index;
    }

    private static int readDollarOperator(String string, int index, NdkBuildTokenReceiver receiver) {
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
        receiver.expected("$");
        return index;
    }

    private static int readAmpOperator(String string, int index, NdkBuildTokenReceiver receiver) {
        ++index;
        if (index == string.length()) {
            return -1;
        }
        switch(string.charAt(index)) {
            case '&':
                ++index;
                receiver.ampAmp();
                return index;
        }
        receiver.amp();
        return index;
    }

    private static int readQuestionOperator(String string, int index, NdkBuildTokenReceiver receiver) {
        ++index;
        if (index == string.length()) {
            return -1;
        }
        switch(string.charAt(index)) {
            case '=':
                ++index;
                receiver.assignConditional();
                return index;
        }
        return index;
    }

    private static int readToken(String string, int index, NdkBuildTokenReceiver receiver) {
        if (string.length() == index) {
            return -1;
        }
        switch (string.charAt(index)) {
            case '#':
                return readComment(string, index, receiver);
            case ' ':
                return readWhitespace(string, index, receiver);
            case ':':
                return readColonOperator(string, index, receiver);
            case '=':
                return readEqualsOperator(string, index, receiver);
            case '+':
                return readPlusOperator(string, index, receiver);
            case '$':
                return readDollarOperator(string, index, receiver);
            case '&':
                return readAmpOperator(string, index, receiver);
            case '?':
                return readQuestionOperator(string, index, receiver);
            case ';':
                receiver.semicolon();
                ++index;
                return index;
            case ',':
                receiver.comma();
                ++index;
                return index;
            case '|':
                receiver.pipe();
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
            case '[':
                receiver.openBracket();
                ++index;
                return index;
            case ']':
                receiver.closeBracket();
                ++index;
                return index;
            case '<':
                receiver.lessThan();
                ++index;
                return index;
            case '>':
                receiver.greaterThan();
                ++index;
                return index;
            case '@':
                receiver.at();
                ++index;
                return index;
            case '*':
                receiver.star();
                ++index;
                return index;
            case '\"':
                receiver.doubleQuote();
                ++index;
                return index;
            case '\r':
            case '\n':
                receiver.endline();
                ++index;
                return index;
            case '\'':
            case '!':
                receiver.expected(string.substring(index, index + 1));
                ++index;
                return index;
        }

        if (NdkBuildCharUtil.isWhitespace(string.charAt(index))) {
            return readWhitespace(string, index, receiver);
        }

        if (NdkBuildCharUtil.isIdentifierStart(string.charAt(index))) {
            return readIdentifier(string, index, receiver);
        }

        if (NdkBuildCharUtil.isNumber(string.charAt(index))) {
            return readNumber(string, index, receiver);
        }

        return readEverythingElse(string, index);
    }

    public static void apply(String string, NdkBuildTokenReceiver receiver) {
        int index = 0;
        string = NdkBuildDecontinuizer.apply(string);
        while(index != -1 && index != string.length()) {
            index = readToken(string, index, receiver);
        }
        receiver.endline();
    }
}
