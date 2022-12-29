package zearch.server;

import zearch.engine.SearchResult;

public interface IServerToModel {
    SearchResult search(String query);
}
