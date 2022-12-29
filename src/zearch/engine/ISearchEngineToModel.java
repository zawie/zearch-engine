package zearch.engine;

import zearch.database.IndexEntry;

import java.util.Iterator;
import java.util.Map;

public interface ISearchEngineToModel {

    Iterator<IndexEntry> getAllIndexEntries();

    int getNumberOfIndexEntries();

    int[] computeMinhashes(String query);

    Map<String, String> getData(Long id);
}
