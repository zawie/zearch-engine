java -cp ./out:./lib/jsoup-1.15.3.jar:./lib/h2-2.1.214.jar zearch.Controller $(cat ./database_filepath) 8 false $@