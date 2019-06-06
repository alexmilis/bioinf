#!/bin/bash

array=()
for i in $(seq 5 10)
do
	array[$i]=0
done

for i in $(seq 1 10)
do	
	for j in $(seq 5 10)
	do	
		echo tail -n 1 "../results/prolazak$i/diff$j" | sed 's/[^0-9]*//g'
		$number=$(tail -n 1 "../results/prolazak$i/diff$j" | sed 's/[^0-9]*//g')
		a=${array[$j]}
		$array[$j]=$(($a+$number))
	done
done

for i in $(seq 5 10)
do
	echo ${array[$i]}
done
