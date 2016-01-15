package com.google.NdkBuildToAndroidStudio;

/**
 * Created by jomof on 1/4/16.
 */
public interface TokenReceiver {

    void argument(String identifier);

    void command(String identifier);

    void comment(String comment);

    void endline();

    void whitespace(String whitespace);
}
