import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by jomof on 1/4/16.
 */
public class NdkBuildParserTest {

    @Test
    public void simpleTest() throws FileNotFoundException {
        FileReader reader = new FileReader("support-files/android-ndk-android-mk/hello-jni/jni/Android.mk");

    }
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }
}