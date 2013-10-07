package jasmine.plugin.readers;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class DirectoryReader extends SuiteReader {

    static public String SPEC_PATTERN = "Spec.js";

    private final File directory;

    public DirectoryReader(File directory) {
        this.directory = directory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getSuite() {
        List<String> suite = getEmptySuite();

        IOFileFilter specsOnly = new NameFileFilter("*" + SPEC_PATTERN);
        Iterator<File> iterator = FileUtils.iterateFiles(directory, specsOnly, TrueFileFilter.INSTANCE);
        while (iterator.hasNext()) {
            File spec = iterator.next();
            String path = spec.getAbsolutePath();

            suite.add(path);
        }

        return suite;
    }

}