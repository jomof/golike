/**
 * Created by jomof on 1/4/16.
 */
interface TokenReceiver {

    void comment(String comment);

    void endline();

    void command(String identifier);

    void argument(String identifier);

    void whitespace(String whitespace);
}
