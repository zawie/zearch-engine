package zearch.engine;

import zearch.util.IndexHashesEntry;

import java.util.Iterator;
import java.util.Map;

public interface ISearchEngineToModel {

    Stream<IndexHashesEntry> getAllIndexEntries();

    int getNumberOfIndexEntries();

    int[] computeMinhashes(String query);

    Map<String, String> getData(Long id);
}
