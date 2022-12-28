package zearch.spider;

import java.io.Reader;
import java.net.URL;
import java.util.Map;

public interface ISpiderToModel {

    void index(URL nextUrl, Map<String, String> metaData, String text);
}
