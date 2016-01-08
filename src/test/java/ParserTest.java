import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by jomof on 1/4/16.
 */
public class ParserTest {

    private static void checkFile(String file) throws IOException {
        System.out.printf("----%s\n", file);
        byte[] encoded = Files.readAllBytes(Paths.get(file));
        Parser parser = new Parser();
        treeString(parser.parse(new String(encoded, StandardCharsets.UTF_8)));
    }

    private static void expectParsed(String target, String expected) {
        Parser parser = new Parser();
        String result = treeString(parser.parse(target));
        if (!result.equals(expected)) {
            throw new RuntimeException(String.format("Expected %s but got %s", expected, result));
        }
    }

    private static String treeString(List<Parser.Node> nodes) {
        StringBuilder sb = new StringBuilder();
        treeStringBuilder(nodes, sb);
        return sb.toString();
    }

    private static void treeStringBuilder(Parser.Node node, StringBuilder sb) {
        switch(node.type) {
            case TYPE_COMMAND_EXPRESSION: {
                Parser.CommandExpression command = (Parser.CommandExpression) node;
                sb.append("(command ");
                sb.append(command.command);
                if (command.args.size() > 0) {
                    sb.append(" [");
                    StringBuilder sub = new StringBuilder();
                    for (Parser.ArgumentExpression arg : command.args) {
                        sub.append(arg.arg);
                        sub.append(" ");
                    }
                    sub.setLength(sub.length() - 1);
                    sb.append(sub);
                    sb.append("]");

                }
                sb.append(")");

                return;
            }
            default:
                throw new RuntimeException(node.type.toString());
        }
    }

    private static void treeStringBuilder(List<Parser.Node> nodes, StringBuilder sb) {
        for(Parser.Node node : nodes) {
            treeStringBuilder(node, sb);
            sb.append("\n");
        }
        sb.setLength(sb.length() - 1);
    }

    @Test
    public void ndkBuildExample() throws FileNotFoundException {
        expectParsed("/usr/local/google/home/jomof/bin/android-ndk-r10e/toolchains/aarch64-linux-android-4.9/prebuilt/linux-x86_64/bin/aarch64-linux-android-gcc" +
                        " -MMD -MP -MF ./obj/local/arm64-v8a/objs-debug/hello-jni/hello-jni.o.d" +
                        " -fpic -ffunction-sections -funwind-tables -fstack-protector-strong -no-canonical-prefixes" +
                        " -O2 -g -DNDEBUG -fomit-frame-pointer -fstrict-aliasing -funswitch-loops" +
                        " -finline-limit=300 -O0 -UNDEBUG -fno-omit-frame-pointer -fno-strict-aliasing" +
                        " -Ijni -DANDROID  -Wa,--noexecstack -Wformat -Werror=format-security    " +
                        "-I/usr/local/google/home/jomof/bin/android-ndk-r10e/platforms/android-21/arch-arm64/usr/include " +
                        "-c  jni/hello-jni.c -o ./obj/local/arm64-v8a/objs-debug/hello-jni/hello-jni.o\n",
                "(command /usr/local/google/home/jomof/bin/android-ndk-r10e/toolchains/aarch64-linux-android-4.9/prebuilt/linux-x86_64/bin/aarch64-linux-android-gcc " +
                        "[-MMD -MP -MF ./obj/local/arm64-v8a/objs-debug/hello-jni/hello-jni.o.d " +
                        "-fpic -ffunction-sections -funwind-tables -fstack-protector-strong -no-canonical-prefixes " +
                        "-O2 -g -DNDEBUG -fomit-frame-pointer -fstrict-aliasing -funswitch-loops " +
                        "-finline-limit=300 -O0 -UNDEBUG -fno-omit-frame-pointer -fno-strict-aliasing " +
                        "-Ijni -DANDROID -Wa,--noexecstack -Wformat -Werror=format-security " +
                        "-I/usr/local/google/home/jomof/bin/android-ndk-r10e/platforms/android-21/arch-arm64/usr/include " +
                        "-c jni/hello-jni.c -o ./obj/local/arm64-v8a/objs-debug/hello-jni/hello-jni.o])");
    }

    @Test
    public void simpleCommand() throws FileNotFoundException {
        expectParsed("ls", "(command ls)");
    }

    @Test
    public void simpleCommandWithParameter() throws FileNotFoundException {
        expectParsed("ls -rf", "(command ls [-rf])");
    }

    @Test
    public void twoCommands() throws FileNotFoundException {
        expectParsed("ls\nmkdir", "(command ls)\n" +
                "(command mkdir)");
    }


}