build:
	javac -cp ./src:./lib/jsoup-1.15.3.jar:./lib/h2-2.1.214.jar ./src/zearch/spider/Spider.java ./src/zearch/query/SearchEngine.java  -d ./out
clean:
	rm -rf ./out/*