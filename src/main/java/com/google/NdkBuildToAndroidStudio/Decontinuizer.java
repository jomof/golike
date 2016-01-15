package com.google.NdkBuildToAndroidStudio;

/**
 * Created by jomof on 1/5/16.
 */
class Decontinuizer {

    private static int eatPastLineFeed(String string, int index) {
        if (index == string.length()) {
            return index;
        }
        while(CharUtil.isWhitespace(string.charAt(index))) index++;
        while(string.charAt(index) == '\r') index++;
        if (string.charAt(index) == '\n') {
            index++;
        }
        if (index == string.length()) {
            return index - 1;
        }
        while(CharUtil.isWhitespace(string.charAt(index))) index++;
        return index;
    }

    private static void decontinue(String string, int index, StringBuilder sb) {
        int start = 0;
        while(index != string.length()) {
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
    }

    public static String apply(String string) {
        StringBuilder sb = new StringBuilder();
        decontinue(string, 0, sb);
        return sb.toString();
    }
}
