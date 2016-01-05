/**
 * Created by jomof on 1/4/16.
 */
public interface TokenReceiver {
    void whitespace(String whitespace);
    void comment(String comment);
    void include();
    void identifier(String identifier);
    void rvalue(String rvalue);
    void assign(); // :=
    void append(); // +=
    void ifeq();
    void endif();
    void dollarOpenParen();
    void openParen();
    void comma();
    void closeParen();
    void endline();
}
