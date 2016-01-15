import com.google.NdkBuildToAndroidStudio.Main;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by jomof on 1/15/16.
 */
public class MainTest {
    private void checkFlow(String json, int expectedHashCode) throws IOException {
        if (expectedHashCode != json.toString().hashCode()) {
            throw new RuntimeException(
                    String.format("Expected hashCode '%s' but got '%s '%s'", expectedHashCode, json.hashCode(), json));
        }
    }

    @Test
    public void teapotBasic() throws IOException, InterruptedException {
        String result = Main.ndkBuildToJson("support-files/android-ndk-r10e/samples/Teapot");
        checkFlow(result, -496724433);
    }
}
