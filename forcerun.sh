cd ~/zearch-engine

# Update
git pull
make

# Stop all currently running java tasks
kill -9 `ps -C java -o pid=`

SITES=$(shuf -n 50 data/top500sites.txt | sed ':a;N;$!ba;s/\n/ /g')
# Run the program
nohup ./scripts/monolith/main.sh $SITES > engine.log 2> engine.err < /dev/null &
