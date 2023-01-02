build:
	mkdir -p out
	make build-monolith
	make build-crawler
build-crawler:
	mkdir -p out
	javac -cp ./src:./lib/jsoup-1.15.3.jar ./src/zearch/controller/Crawler.java -d ./out
build-monolith:
	mkdir -p out
	javac -cp ./src:./lib/jsoup-1.15.3.jar:./lib/h2-2.1.214.jar ./src/zearch/controller/Monolith.java -d ./out
clean:
	rm -rf ./out/*