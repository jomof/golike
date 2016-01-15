import com.google.NdkBuildToAndroidStudio.TokenReceiver;
import com.google.NdkBuildToAndroidStudio.Tokenizer;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by jomof on 1/4/16.
 */
public class TokenizerTest {
    private static void checkFile(String file) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(file));
        StringPrintingTokenReceiver receiver = new StringPrintingTokenReceiver();
        receiver.sb.append(String.format("-----%s\n", file));
        try {
            Tokenizer.apply(
                    new String(encoded, StandardCharsets.UTF_8), receiver);
        } finally {
            System.out.printf("%s", receiver.sb.toString());
        }
    }

    private static String checkString(String string) throws IOException {
        StringPrintingTokenReceiver receiver = new StringPrintingTokenReceiver();
        try {
            Tokenizer.apply(
                    string,
                    receiver);
        } catch (Throwable e) {
            System.out.printf(receiver.sb.toString());
        }
        return receiver.sb.toString();
    }

    private static void checkStringEquals(String target, String expected) throws IOException {
        String result = checkString(target);
        if (!result.equals(expected + "\n")) {
            throw new RuntimeException(String.format("Expected '%s' but got '%s'", expected, result));
        }
    }

    @Test
    public void at() throws IOException {
        checkStringEquals("@echo yo dog", "{command:@echo} {arg:yo} {arg:dog}");
    }

    @Test
    public void bracket() throws IOException {
        checkStringEquals("@echo \"[yo] dog\"", "{command:@echo} {arg:\"[yo] dog\"}");
    }

    @Test
    public void broken() throws IOException {
        checkFile("support-files/android-ndk-r10e/build/core/setup-toolchain.mk");
    }

    @Test
    public void checkNumber() throws IOException {
        checkStringEquals("x 1", "{command:x} {arg:1}");
    }

    @Test
    public void equals() throws IOException {
        checkStringEquals("a = b \\\n\ta", "{command:a} {arg:=} {arg:b} {arg:a}");
    }

    @Test
    public void equals1() throws IOException {
        checkStringEquals("a := b=c", "{command:a} {arg::=} {arg:b=c}");
    }

    @Test
    public void lineContinuation1() throws IOException {
        checkStringEquals("a := b \\ \n a", "{command:a} {arg::=} {arg:b} {arg:a}");
    }

    @Test
    public void lineContinuation2() throws IOException {
        checkStringEquals("a := b \\\n a", "{command:a} {arg::=} {arg:b} {arg:a}");
    }

    @Test
    public void lineContinuation3() throws IOException {
        checkStringEquals("a := b \\\na", "{command:a} {arg::=} {arg:b} {arg:a}");
    }

    @Test
    public void lineContinuation4() throws IOException {
        checkStringEquals("a:=b\\\nc\\\n\nd", "{command:a:=b} {arg:c} \n" +
                "{command:d}");
    }

    @Test
    public void lineContinuation5() throws IOException {
        checkStringEquals("a := b \\\n\ta", "{command:a} {arg::=} {arg:b} {arg:a}");
    }

    @Test
    public void lineContinuation6() throws IOException {
        checkStringEquals("a := b\\\na\\", "{command:a} {arg::=} {arg:b} {arg:a} ");
    }

    @Test
    public void nestedParen() throws IOException {
        checkStringEquals("ifeq ($(A),$(B))", "{command:ifeq} {arg:($(A),$(B))}");
    }

    @Test
    public void quote() throws IOException {
        checkStringEquals("@echo \"yo dog\"", "{command:@echo} {arg:\"yo dog\"}");
    }

   // @Test
    public void simpleApply() throws IOException {
        checkFile("support-files/android-ndk-r10e/toolchains/aarch64-linux-android-clang3.5/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/aarch64-linux-android-clang3.5/setup.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/aarch64-linux-android-clang3.6/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/aarch64-linux-android-clang3.6/setup.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/x86-4.9/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/x86-4.9/setup.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/aarch64-linux-android-4.9/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/aarch64-linux-android-4.9/setup.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/mipsel-linux-android-clang3.5/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/mipsel-linux-android-clang3.5/setup.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/mipsel-linux-android-4.8/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/mipsel-linux-android-4.8/setup.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/mipsel-linux-android-clang3.6/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/mipsel-linux-android-clang3.6/setup.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/mips64el-linux-android-4.9/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/mips64el-linux-android-4.9/setup.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/arm-linux-androideabi-4.9/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/arm-linux-androideabi-4.9/setup.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/mips64el-linux-android-clang3.6/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/mips64el-linux-android-clang3.6/setup.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/mips64el-linux-android-clang3.5/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/mips64el-linux-android-clang3.5/setup.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/arm-linux-androideabi-clang3.5/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/arm-linux-androideabi-clang3.5/setup.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/x86_64-4.9/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/x86_64-4.9/setup.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/mipsel-linux-android-4.9/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/mipsel-linux-android-4.9/setup.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/x86_64-clang3.5/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/x86_64-clang3.5/setup.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/llvm-3.5/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/llvm-3.5/setup.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/llvm-3.5/setup-common.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/arm-linux-androideabi-clang3.6/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/arm-linux-androideabi-clang3.6/setup.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/x86-clang3.5/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/x86-clang3.5/setup.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/arm-linux-androideabi-4.8/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/arm-linux-androideabi-4.8/setup.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/x86-4.8/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/x86-4.8/setup.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/llvm-3.6/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/llvm-3.6/setup.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/llvm-3.6/setup-common.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/x86_64-clang3.6/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/x86_64-clang3.6/setup.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/x86-clang3.6/config.mk");
        checkFile("support-files/android-ndk-r10e/toolchains/x86-clang3.6/setup.mk");
        checkFile("support-files/android-ndk-r10e/samples/hello-neon/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/samples/hello-neon/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/samples/hello-jni/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/samples/hello-jni/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/samples/bitmap-plasma/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/samples/bitmap-plasma/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/samples/native-audio/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/samples/native-audio/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/samples/Teapot/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/samples/Teapot/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/samples/hello-gl2/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/samples/hello-gl2/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/samples/gles3jni/jni/Android-18.mk");
        checkFile("support-files/android-ndk-r10e/samples/gles3jni/jni/Android-11.mk");
        checkFile("support-files/android-ndk-r10e/samples/gles3jni/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/samples/test-libstdc++/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/samples/test-libstdc++/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/samples/HelloComputeNDK/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/samples/HelloComputeNDK/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/samples/native-plasma/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/samples/native-plasma/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/samples/module-exports/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/samples/module-exports/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/samples/two-libs/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/samples/two-libs/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/samples/native-media/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/samples/native-media/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/samples/native-activity/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/samples/native-activity/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/samples/MoreTeapots/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/samples/MoreTeapots/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/samples/native-codec/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/samples/native-codec/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/samples/san-angeles/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/samples/san-angeles/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/sources/android/cpufeatures/Android.mk");
        checkFile("support-files/android-ndk-r10e/sources/android/crazy_linker/Android.mk");
        checkFile("support-files/android-ndk-r10e/sources/android/crazy_linker/tests/Android.mk");
        checkFile("support-files/android-ndk-r10e/sources/android/ndk_helper/Android.mk");
        checkFile("support-files/android-ndk-r10e/sources/android/libportable/Android.mk");
        checkFile("support-files/android-ndk-r10e/sources/android/support/Android.mk");
        checkFile("support-files/android-ndk-r10e/sources/android/support/tests/Android.mk");
        checkFile("support-files/android-ndk-r10e/sources/android/native_app_glue/Android.mk");
        checkFile("support-files/android-ndk-r10e/sources/android/compiler-rt/Android.mk");
        checkFile("support-files/android-ndk-r10e/sources/cxx-stl/stlport/Android.mk");
        checkFile("support-files/android-ndk-r10e/sources/cxx-stl/gnu-libstdc++/Android.mk");
        checkFile("support-files/android-ndk-r10e/sources/cxx-stl/llvm-libc++/test/Android.mk");
        checkFile("support-files/android-ndk-r10e/sources/cxx-stl/llvm-libc++/Android.mk");
        checkFile("support-files/android-ndk-r10e/sources/cxx-stl/gabi++/sources.mk");
        checkFile("support-files/android-ndk-r10e/sources/cxx-stl/gabi++/Android.mk");
        checkFile("support-files/android-ndk-r10e/sources/cxx-stl/gabi++/tests/Android.mk");
        checkFile("support-files/android-ndk-r10e/sources/cxx-stl/llvm-libc++abi/sources.mk");
        checkFile("support-files/android-ndk-r10e/sources/cxx-stl/system/setup.mk");
        checkFile("support-files/android-ndk-r10e/sources/cxx-stl/system/Android.mk");
        checkFile("support-files/android-ndk-r10e/sources/cpufeatures/Android.mk");
        checkFile("support-files/android-ndk-r10e/sources/third_party/googletest/Android.mk");
        checkFile("support-files/android-ndk-r10e/build/core/build-static-library.mk");
        checkFile("support-files/android-ndk-r10e/build/core/setup-app.mk");
        checkFile("support-files/android-ndk-r10e/build/core/prebuilt-shared-library.mk");
        checkFile("support-files/android-ndk-r10e/build/core/add-toolchain.mk");
        checkFile("support-files/android-ndk-r10e/build/core/setup-imports.mk");
        checkFile("support-files/android-ndk-r10e/build/core/build-local.mk");
        checkFile("support-files/android-ndk-r10e/build/core/clear-vars.mk");
        checkFile("support-files/android-ndk-r10e/build/core/setup-toolchain.mk");
        checkFile("support-files/android-ndk-r10e/build/core/definitions-graph.mk");
        checkFile("support-files/android-ndk-r10e/build/core/definitions.mk");
        checkFile("support-files/android-ndk-r10e/build/core/build-binary.mk");
        checkFile("support-files/android-ndk-r10e/build/core/main.mk");
        checkFile("support-files/android-ndk-r10e/build/core/build-executable.mk");
        checkFile("support-files/android-ndk-r10e/build/core/init.mk");
        checkFile("support-files/android-ndk-r10e/build/core/build-all.mk");
        checkFile("support-files/android-ndk-r10e/build/core/build-shared-library.mk");
        checkFile("support-files/android-ndk-r10e/build/core/check-cygwin-make.mk");
        checkFile("support-files/android-ndk-r10e/build/core/add-application.mk");
        checkFile("support-files/android-ndk-r10e/build/core/definitions-utils.mk");
        checkFile("support-files/android-ndk-r10e/build/core/definitions-tests.mk");
        checkFile("support-files/android-ndk-r10e/build/core/definitions-host.mk");
        checkFile("support-files/android-ndk-r10e/build/core/default-application.mk");
        checkFile("support-files/android-ndk-r10e/build/core/build-module.mk");
        checkFile("support-files/android-ndk-r10e/build/core/add-platform.mk");
        checkFile("support-files/android-ndk-r10e/build/core/import-locals.mk");
        checkFile("support-files/android-ndk-r10e/build/core/setup-abi.mk");
        checkFile("support-files/android-ndk-r10e/build/core/prebuilt-library.mk");
        checkFile("support-files/android-ndk-r10e/build/core/default-build-commands.mk");
        checkFile("support-files/android-ndk-r10e/build/core/prebuilt-static-library.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-gnustl-full/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-gnustl-full/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue19851-sigsetjmp/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue19851-sigsetjmp/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-gabi++/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-gabi++/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-stlport/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-stlport/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-libc++-shared-full/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-libc++-shared-full/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-gnustl_shared-exception/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-gnustl_shared-exception/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue61659-neon-assignment/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue61659-neon-assignment/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-libc++-static-full/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-libc++-static-full/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-gnustl_static-exception/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-gnustl_static-exception/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-googletest-stlport/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-googletest-stlport/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/whole-static-libs/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/whole-static-libs/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/bitfield/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/bitfield/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/crazy_linker/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/crazy_linker/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-cxx-init-array/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-cxx-init-array/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue62910-gcc4.8.2-libstdc++-nth-element-segfault/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue62910-gcc4.8.2-libstdc++-nth-element-segfault/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/multi-static-instances/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/multi-static-instances/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue22165-typeinfo/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue22165-typeinfo/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-stlport-rtti/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-stlport-rtti/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-libc++-static/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-libc++-static/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-cpufeatures/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-cpufeatures/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-unwind-struct/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-unwind-struct/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue39680-chrono-resolution/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue39680-chrono-resolution/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-gnustl-1/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-gnustl-1/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/gnustl-shared-1/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/gnustl-shared-1/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue42891-boost-1_52/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue42891-boost-1_52/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-stlport_shared-exception/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-stlport_shared-exception/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue46718-iostream-crash-stlport/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue46718-iostream-crash-stlport/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue35933-lambda/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue35933-lambda/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/b16355626-bad-atof-strtod/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/b16355626-bad-atof-strtod/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-stlport-copy_vector_into_a_set/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-stlport-copy_vector_into_a_set/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-libc++/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-libc++/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-wait/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-wait/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/fenv/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/fenv/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue38121/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue38121/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-yasm/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-yasm/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-basic-exceptions/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-basic-exceptions/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-compiler-bug-1/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-compiler-bug-1/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/clone/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/clone/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue46718-iostream-crash-gnustl/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue46718-iostream-crash-gnustl/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/b16355858/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/b16355858/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/stat/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/stat/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-android-support/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-android-support/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/emm/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/emm/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue28598-linker-global-ref/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue28598-linker-global-ref/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-openmp/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-openmp/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-googletest-gnustl/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-googletest-gnustl/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue20176-__gnu_Unwind_Find_exidx/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/issue20176-__gnu_Unwind_Find_exidx/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-libc++-shared/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-libc++-shared/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-gnustl-copy_vector_into_a_set/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-gnustl-copy_vector_into_a_set/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/static-executable/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/static-executable/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-gnustl-2/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-gnustl-2/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/exceptions-crash/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/exceptions-crash/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/b8708181-Vector4/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/b8708181-Vector4/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-basic-rtti/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-basic-rtti/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-stlport_static-exception/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-stlport_static-exception/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/hard-float/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/hard-float/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-googletest-full/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/device/test-googletest-full/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/prebuilt-copy/prebuilts/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/prebuilt-copy/prebuilts/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/prebuilt-copy/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/prebuilt-copy/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue81440-non-ascii-comment/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue81440-non-ascii-comment/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue65705-asm-pc/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue65705-asm-pc/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue20862-libpng-O0/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue20862-libpng-O0/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue79114-__builtin___stpncpy_chk/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue79114-__builtin___stpncpy_chk/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/multiple-static-const/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/multiple-static-const/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue41387-uniform-initialized-rvalue/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue41387-uniform-initialized-rvalue/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/import-install/path1/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/import-install/path2/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/import-install/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/import-install/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/warn-thin-archive-is-for-static-libraries/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/awk-trailing-r/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/awk-trailing-r/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue52805-set_new_handler/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue52805-set_new_handler/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue21132-__ARM_ARCH__/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue21132-__ARM_ARCH__/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue22345-ICE-postreload/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue22345-ICE-postreload/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/warn-non-system-libs-in-linker-flags/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/ssax-instructions/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/ssax-instructions/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/cortex-a53-835769/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/cortex-a53-835769/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/pthread-rwlock-initializer/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/pthread-rwlock-initializer/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue53711-un_h/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue53711-un_h/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/system-cpp-headers/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/system-cpp-headers/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/stlport-static-assert/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/stlport-static-assert/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue42841-LOCAL_PATH/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue42841-LOCAL_PATH/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue40625-SL_IID_ANDROIDBUFFERQUEUESOURCE/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue40625-SL_IID_ANDROIDBUFFERQUEUESOURCE/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue36131-flto-c++11/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue36131-flto-c++11/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/check-armeabi-v7a-prebuilts/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/check-armeabi-v7a-prebuilts/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue34613-neon/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue34613-neon/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/multi-abi/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/multi-abi/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/test-c-only-flags/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/test-c-only-flags/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue66668-libc++-std-feof/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue66668-libc++-std-feof/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/ucontext/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/ucontext/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/build-assembly-file/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/build-assembly-file/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/lambda-defarg3/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/lambda-defarg3/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/merge-string-literals/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/merge-string-literals/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/graphite-loop/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/graphite-loop/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue41770-_GLIBCXX_HAS_GTHREADS/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue41770-_GLIBCXX_HAS_GTHREADS/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue38441-Elf32_auxv_t/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue38441-Elf32_auxv_t/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue17144-byteswap/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue17144-byteswap/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/test-stlport_static-exception-force-rebuild/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/test-stlport_static-exception-force-rebuild/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue22336-ICE-emit-rtl/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue22336-ICE-emit-rtl/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/topological-sort/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/topological-sort/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue53646-stlport-stl_confix_h/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue53646-stlport-stl_confix_h/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue56508-gcc4.7-ICE/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue56508-gcc4.7-ICE/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue54623-dcraw_common-x86-segfault/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue54623-dcraw_common-x86-segfault/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/test-gnustl-chrono/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/test-gnustl-chrono/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue39824-__BYTE_ORDER/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue39824-__BYTE_ORDER/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/no-installable-modules/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/no-installable-modules/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue-gcc59052-partial-specialization-of-template/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue-gcc59052-partial-specialization-of-template/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/c++-stl-source-extensions/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/c++-stl-source-extensions/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/prebuild-stlport/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/prebuild-stlport/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/fenv/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/fenv/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue52819-STLPORT_FORCE_REBUILD/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue52819-STLPORT_FORCE_REBUILD/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/multi-module-path/path1/foo/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/multi-module-path/path2/bar/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/multi-module-path/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/gnustl-force-all/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/gnustl-force-all/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/ndk-build-unit-tests/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/warn-noabi/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/warn-noabi/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/stlport-src-suffix/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/stlport-src-suffix/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue41297-atomic-64bit/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue41297-atomic-64bit/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/mips-fp4/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/mips-fp4/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/thin-archives/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/thin-archives/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue64679-prctl/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue64679-prctl/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/b14825026-aarch64-FP_LO_REGS/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/b14825026-aarch64-FP_LO_REGS/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/absolute-src-file-paths/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/absolute-src-file-paths/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/clang-include-gnu-libc++/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/clang-include-gnu-libc++/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/b8247455-hidden-cxa/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/b8247455-hidden-cxa/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/build-mode/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/build-mode/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/deprecate-__set_errno/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/deprecate-__set_errno/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/ndk-out/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/flto/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/flto/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/warn-bad-modules/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/warn-bad-modules/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue54465-invalid-asm-operand-out-of-range/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue54465-invalid-asm-operand-out-of-range/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/stdint-c++/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/stdint-c++/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue79115-confusing-ld.gold-warning/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue79115-confusing-ld.gold-warning/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/cpp-extensions/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/cpp-extensions/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/warn-no-modules/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/b9193874-neon/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/b9193874-neon/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/import-static/bar/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/import-static/foo/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/import-static/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/ansi/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/ansi/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/b14811006-GOT_PREL-optimization/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/b14811006-GOT_PREL-optimization/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/clang-multiple-arm-enable-ehabi/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/clang-multiple-arm-enable-ehabi/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/gnustl-force-exceptions/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/gnustl-force-exceptions/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/project-properties/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/project-properties/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue53163-OpenSLES_AndroidConfiguration/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue53163-OpenSLES_AndroidConfiguration/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue58135-_C_LABEL_STRING/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue58135-_C_LABEL_STRING/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/gnustl-force-rtti/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/gnustl-force-rtti/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/wchar_t-size/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/wchar_t-size/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/warn-no-ldflags-in-static-libraries/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/warn-no-ldflags-in-static-libraries/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/warn-no-ldlibs-in-static-libraries/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/warn-no-ldlibs-in-static-libraries/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/test-inet-defs/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/test-inet-defs/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/gnustl-force-none/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/gnustl-force-none/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/target-c-includes/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/target-c-includes/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue53404-backward-compatibility/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue53404-backward-compatibility/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue39983-PAGE_SIZE/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/build/issue39983-PAGE_SIZE/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/abcc/jni/Application.mk");
        checkFile("support-files/android-ndk-r10e/tests/abcc/jni/Android.mk");
        checkFile("support-files/android-ndk-r10e/tests/abcc/Android.mk");
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

    @Test
    public void twoCommands() throws IOException {
        checkStringEquals("ls\nmkdir", "{command:ls}\n" +
                "{command:mkdir}");
    }

    static class StringPrintingTokenReceiver implements TokenReceiver {
        public StringBuilder sb = new StringBuilder();

        @Override
        public void argument(String identifier) {
            sb.append(String.format("{arg:%s}", identifier));
        }

        @Override
        public void command(String identifier) {
            sb.append(String.format("{command:%s}", identifier));
        }

        @Override
        public void comment(String comment) {
            sb.append(String.format("{c:%s}", comment));
        }

        @Override
        public void endline() {
            sb.append("\n");
        }

        @Override
        public void whitespace(String whitespace) {
            sb.append(whitespace);
        }
    }
}