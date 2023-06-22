cd ~/zearch-engine

# Update
git pull
make

# Stop all currently running java tasks
killall -9 java

# Run the program
./scripts/monolith/main.sh https://en.wikipedia.org/wiki/List_of_most_visited_websites > output.log
