build:
	mkdir -p out
#	cp -R -p ./lib/org ./out/
	javac -cp ./src:./lib/jsoup-1.15.3.jar:./lib/h2-2.1.214.jar ./src/zearch/controller/Monolith.java -d ./out
	javac -cp ./src:./lib/jsoup-1.15.3.jar:./lib/aws-java-sdk-1.12.376.jar ./src/zearch/controller/Crawler.java -d ./out
clean:
	rm -rf ./out/*