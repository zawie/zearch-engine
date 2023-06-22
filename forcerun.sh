cd ~/zearch-engine

# Update
git pull
make

# Stop all currently running java tasks
kill -9 `ps -C java -o pid=`

# Run the program
./scripts/monolith/main.sh https://en.wikipedia.org/wiki/List_of_most_visited_websites > output.log
