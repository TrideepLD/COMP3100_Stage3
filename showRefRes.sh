#!/bin/bash
# to kill multiple runaway processes, use 'pkill runaway_process_name'
# For the Java implementation, use the following format: ./tests1.sh your_client.class [-n]
logDir="./logs"
algos="ff bf wf fs"

if [ ! -d $logDir ]; then
	mkdir $logDir
else
	rm $logDir/* &> /dev/null
fi


if [ $# -lt 1 ]; then
	echo "Usage: $0 configuration_file"
	exit
fi

if [ ! -f $1 ]; then
	echo "No $1 found!"
	echo "Usage: $0 configuration_file"
	exit
fi


trap "kill 0" EXIT
echo "$logDir/$(sed 's/.*\///' <<< $1)-$algo.log"

for algo in $algos; do
	logFile=$logDir/$(sed 's/.*\///' <<< $1)-$algo.log
	echo "running $algo...$logFile"
	./ds-server -c $1 -v brief > $logFile&
	sleep 1
	echo "client running..."
	java Client -a $algo
	sleep 2
done

echo hello!!!

n=0

for log in $logDir/*.log; do
    if [[ -f ${log} ]]; then
        echo "$(basename ${log}):"
        if (( ${n} == 0 )); then
        	# https://stackoverflow.com/a/24017666/8031185
            sed '/^t:/h;//!H;$!d;x' ${log}
        else
            tail -${n} ${log}
        fi
        echo ""
    fi
done
