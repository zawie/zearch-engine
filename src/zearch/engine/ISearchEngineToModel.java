package zearch.engine;

import zearch.util.IndexHashesEntry;

import java.util.Iterator;
import java.util.Map;

public interface ISearchEngineToModel {

    Iterator<IndexHashesEntry> getAllIndexEntries();

    int getNumberOfIndexEntries();

    int[] computeMinhashes(String query);

    Map<String, String> getData(Long id);
}
