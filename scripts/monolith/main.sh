java -Xmx3g -cp ./out:./lib/jsoup-1.15.3.jar:./lib/h2-2.1.214.jar zearch.controller.Monolith $(cat ./database_filepath) 2 true $@