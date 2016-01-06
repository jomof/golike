/**
 * Created by jomof on 1/4/16.
 */
public interface NdkBuildTokenReceiver {
    void amp(); // &

    void ampAmp(); // &&

    void append(); // +=

    void assign(); // :=

    void assignConditional(); // ?=

    void at(); // @ (suppress echo)

    void closeBracket();

    void closeParen();

    void comma();

    void comment(String comment);

    void define();

    void dollarOpenParen();

    void doubleQuote();

    void endef();

    void endif();

    void endline();

    void equals(); // =

    void expected(String string);

    void greaterThan();

    void identifier(String identifier);

    void ifdef();

    void ifeq();

    void ifneq();

    void include();

    void lessThan();

    void number(String number);

    void openBracket();

    void openParen();

    void pipe(); // |

    void plus(); // +

    void semicolon(); // ;

    void star(); // *

    void whitespace(String whitespace);
}
