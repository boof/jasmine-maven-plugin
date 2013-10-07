package jasmine.plugin.readers;

import java.util.ArrayList;
import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: boof
* Date: 10/4/13
* Time: 11:15 AM
*/
public class SuiteReader {

    protected List<String> getEmptySuite() {
        return new ArrayList<String>();
    }
    public List<String> getSuite() {
        return getEmptySuite();
    }

}
