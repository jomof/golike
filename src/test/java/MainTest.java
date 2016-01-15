import org.junit.Test;

import java.io.*;

/**
 * Created by jomof on 1/15/16.
 */
public class MainTest {
    class StreamGobbler extends Thread
    {
        InputStream is;
        String type;

        StreamGobbler(InputStream is, String type)
        {
            this.is = is;
            this.type = type;
        }

        public void run()
        {
            try
            {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ( (line = br.readLine()) != null)
                    System.out.println(type + ">" + line);
            } catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
    }

    @Test
    public void doubleTarget() throws IOException, InterruptedException {
        Process proc = Runtime.getRuntime().exec(
                new String[] { "/bin/bash", "-c", "ndk-build NDK_PROJECT_PATH=support-files/android-ndk-r10e/samples/Teapot" }
        );

        // any error message?
        StreamGobbler errorGobbler = new
                StreamGobbler(proc.getErrorStream(), "ERROR");

        // any output?
        StreamGobbler outputGobbler = new
                StreamGobbler(proc.getInputStream(), "OUTPUT");

        // kick them off
        errorGobbler.start();
        outputGobbler.start();

        int exitVal = proc.waitFor();
        System.out.println("ExitValue: " + exitVal);
    }
}
