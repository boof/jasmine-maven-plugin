package jasmine.plugin;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import java.io.File;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: boof
 * Date: 10/4/13
 * Time: 12:25 PM
 */
public class JasmineRunnerTest {
    @Test
    public void testRun() throws Exception {
        URL resource = JasmineRunnerTest.class.getResource("SpecRunner.html");
        File directory = new File(resource.getPath()).getParentFile();
        Log log = mock(Log.class);

        JasmineRunner runner = new JasmineRunner(directory, log);
        runner.run();
    }
}
