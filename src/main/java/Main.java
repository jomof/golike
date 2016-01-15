import java.io.*;

/**
 * Created by jomof on 1/15/16.
 */
public class Main {
    public static void Main(String[] args, InputStream inputStream, PrintStream outStream) throws IOException {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
        }
        catch (IOException e) {
            System.out.printf("IOException reading System.in %s\n", e);
            throw e;
        }
        finally {
            if (in != null) {
                in.close();
            }
        }

    }
    public static void Main(String[] args) throws IOException {
        Main(args, System.in, System.out);
    }
}
