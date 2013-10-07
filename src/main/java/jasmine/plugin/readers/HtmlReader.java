package jasmine.plugin.readers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: boof
 * Date: 10/4/13
 * Time: 11:17 AM
 */
public class HtmlReader extends SuiteReader {

    static public String SPEC_PATTERN = "Spec.js";

    private final File runnerHtmlFile;

    public HtmlReader(File runnerHtmlFile) {
        this.runnerHtmlFile = runnerHtmlFile;
    }

    @Override
    public List<String> getSuite() {
        List<String> suite = getEmptySuite();
        String parent = runnerHtmlFile.getParent();

        try {
            Elements specs = getSpecScripts(parent);
            for (Element spec : specs) {
                String src = spec.attr("src");
                File file = new File(parent, src);
                boolean hit = file.exists();

                String path = file.getAbsolutePath();
                if (hit) {
                    suite.add(path);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return suite;
    }

    private Elements getSpecScripts(String parent) throws IOException {
        Document document = Jsoup.parse(runnerHtmlFile, "UTF-8", "file://" + parent);
        return document.select("script[src$=" + SPEC_PATTERN + "]");
    }

}
