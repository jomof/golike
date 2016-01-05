import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by jomof on 1/4/16.
 */
public class NdkBuildTokenizerTest {
    class StringPrintingTokenReceiver implements TokenReceiver {
        public StringBuilder sb = new StringBuilder();

        @Override
        public void whitespace(String whitespace) {
            sb.append(whitespace);
        }

        @Override
        public void comment(String comment) {
            sb.append(String.format("{c:%s}", comment));

        }

        @Override
        public void include() {
            sb.append(String.format("{include}"));

        }

        @Override
        public void identifier(String identifier) {
            sb.append(String.format("{id:%s}", identifier));

        }

        @Override
        public void rvalue(String rvalue) {
            sb.append(String.format("{rv:%s}", rvalue));
        }

        @Override
        public void assign() {
            sb.append(String.format("{:=}"));

        }

        @Override
        public void append() {
            sb.append(String.format("{+=}"));
        }

        @Override
        public void ifeq() {
            sb.append(String.format("{ifeq}"));
        }

        @Override
        public void endif() {
            sb.append(String.format("{endif}"));
        }

        @Override
        public void dollarOpenParen() {
            sb.append(String.format("$("));
        }

        @Override
        public void openParen() {
            sb.append(String.format("("));
        }

        @Override
        public void comma() {
            sb.append("{,}");

        }

        @Override
        public void closeParen() {
            sb.append(String.format(")"));
        }

        @Override
        public void endline() {
            sb.append("\n");

        }
    }

    private void checkFile(String file) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(file));
        StringPrintingTokenReceiver receiver = new StringPrintingTokenReceiver();
        receiver.sb.append(String.format("-----%s\n", file));
        try {
            NdkBuildTokenizer.apply(
                    new String(encoded, StandardCharsets.UTF_8), receiver);
        } finally {
            System.out.printf(receiver.sb.toString());
        }
    }

    private String checkString(String string) throws IOException {
        StringPrintingTokenReceiver receiver = new StringPrintingTokenReceiver();
        try {
            NdkBuildTokenizer.apply(
                    string,
                    receiver);
        } catch (Throwable e) {
            System.out.printf(receiver.sb.toString());
        }
        return receiver.sb.toString();
    }

    private void checkStringEquals(String target, String expected) throws IOException {
        String result = checkString(target);
        if (!result.equals(expected)) {
            throw new RuntimeException(String.format("Expected %s but got %s", expected, result));
        }
    }


    @Test
    public void checkMacro() throws IOException {
        checkStringEquals("$(dog)", "$({id:dog})");
    }

    @Test
    public void nestedParen() throws IOException {
        checkStringEquals("ifeq ($(A),$(B))", "{ifeq} ($({id:A}){,}$({id:B}))");
    }

    @Test
    public void lineContinuation1() throws IOException {
        checkStringEquals("a := b \\ \n a", "{id:a} {:=}{rv:b a}");
    }

    @Test
    public void lineContinuation2() throws IOException {
        checkStringEquals("a := b \\\n a", "{id:a} {:=}{rv:b a}");
    }

    @Test
    public void lineContinuation3() throws IOException {
        checkStringEquals("a := b \\\na", "{id:a} {:=}{rv:b a}");
    }

    @Test
    public void lineContinuation4() throws IOException {
        checkStringEquals("a:=b\\\nc\\\n\nd", "{id:a}{:=}{rv:b c}\n{id:d}");
    }

    @Test
    public void lineContinuation5() throws IOException {
        checkStringEquals("a := b \\\n\ta", "{id:a} {:=}{rv:b a}");
    }

    @Test
    public void macroWithForwardSlash() throws IOException {
        checkStringEquals("$(call import-module, a/b)", "$({id:call} {id:import-module}{,} {id:a/b})");
    }

    @Test
    public void broken() throws IOException {
        checkFile("support-files/android-ndk-android-mk/gles3jni/jni/Android-18.mk");
    }

    @Test
    public void simpleApply() throws IOException {
        checkFile("support-files/android-ndk-android-mk/hello-neon/jni/Application.mk");
        checkFile("support-files/android-ndk-android-mk/hello-neon/jni/Android.mk");
        checkFile("support-files/android-ndk-android-mk/hello-jni/jni/Application.mk");
        checkFile("support-files/android-ndk-android-mk/hello-jni/jni/Android.mk");
        checkFile("support-files/android-ndk-android-mk/bitmap-plasma/jni/Application.mk");
        checkFile("support-files/android-ndk-android-mk/bitmap-plasma/jni/Android.mk");
        checkFile("support-files/android-ndk-android-mk/native-audio/jni/Application.mk");
        checkFile("support-files/android-ndk-android-mk/native-audio/jni/Android.mk");
        checkFile("support-files/android-ndk-android-mk/Teapot/jni/Application.mk");
        checkFile("support-files/android-ndk-android-mk/Teapot/jni/Android.mk");
        checkFile("support-files/android-ndk-android-mk/hello-gl2/jni/Application.mk");
        checkFile("support-files/android-ndk-android-mk/hello-gl2/jni/Android.mk");
        checkFile("support-files/android-ndk-android-mk/hello-gl2/Android.mk");
        checkFile("support-files/android-ndk-android-mk/gles3jni/jni/Android-18.mk");
        checkFile("support-files/android-ndk-android-mk/gles3jni/jni/Android-11.mk");
        checkFile("support-files/android-ndk-android-mk/gles3jni/jni/Application.mk");
        checkFile("support-files/android-ndk-android-mk/test-libstdc++/jni/Application.mk");
        checkFile("support-files/android-ndk-android-mk/test-libstdc++/jni/Android.mk");
        checkFile("support-files/android-ndk-android-mk/native-plasma/jni/Application.mk");
        checkFile("support-files/android-ndk-android-mk/native-plasma/jni/Android.mk");
        checkFile("support-files/android-ndk-android-mk/module-exports/jni/Application.mk");
        checkFile("support-files/android-ndk-android-mk/module-exports/jni/Android.mk");
        checkFile("support-files/android-ndk-android-mk/two-libs/jni/Application.mk");
        checkFile("support-files/android-ndk-android-mk/two-libs/jni/Android.mk");
        checkFile("support-files/android-ndk-android-mk/native-media/jni/Application.mk");
        checkFile("support-files/android-ndk-android-mk/native-media/jni/Android.mk");
        checkFile("support-files/android-ndk-android-mk/native-activity/jni/Application.mk");
        checkFile("support-files/android-ndk-android-mk/native-activity/jni/Android.mk");
        checkFile("support-files/android-ndk-android-mk/native-activity/Android.mk");
        checkFile("support-files/android-ndk-android-mk/MoreTeapots/jni/Application.mk");
        checkFile("support-files/android-ndk-android-mk/MoreTeapots/jni/Android.mk");
        checkFile("support-files/android-ndk-android-mk/native-codec/jni/Application.mk");
        checkFile("support-files/android-ndk-android-mk/native-codec/jni/Android.mk");
        checkFile("support-files/android-ndk-android-mk/san-angeles/jni/Application.mk");
        checkFile("support-files/android-ndk-android-mk/san-angeles/jni/Android.mk");
    }
}