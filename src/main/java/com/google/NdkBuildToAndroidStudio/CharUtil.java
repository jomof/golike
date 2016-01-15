package com.google.NdkBuildToAndroidStudio;

/**
 * Created by jomof on 1/5/16.
 */
class CharUtil {
    static boolean isWhitespace(char c) {
        return c == ' ' || c == '\t';
    }

    static boolean isIdentifierStart(char c) {
        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || (c == '_') || (c == '-') || (c == '.')
                || (c == '/') || (c == '%') ;

    }

    static boolean isIdentifier(char c) {
        return isIdentifierStart(c)
                || isNumber(c)
                || (c == '+')
                || (c == '*');
    }

    public static boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }
}
