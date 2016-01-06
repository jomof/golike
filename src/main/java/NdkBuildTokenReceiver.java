/**
 * Created by jomof on 1/4/16.
 */
public interface NdkBuildTokenReceiver {
    void whitespace(String whitespace);
    void comment(String comment);
    void include();
    void define();
    void endef();
    void identifier(String identifier);
    void number(String number);
    void at(); // @ (suppress echo)
    void equals(); // =
    void assign(); // :=
    void assignConditional(); // ?=
    void append(); // +=
    void ifeq();
    void ifneq();
    void endif();
    void dollarOpenParen();
    void openParen();
    void closeParen();
    void openBracket();
    void closeBracket();
    void lessThan();
    void greaterThan();
    void semicolon(); // ;
    void pipe(); // |
    void star(); // *
    void plus(); // +
    void amp(); // &
    void ampAmp(); // &&
    void comma();
    void doubleQuote();
    void expected(String string);
    void endline();
}
