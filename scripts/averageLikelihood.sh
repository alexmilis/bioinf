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
		number=$(tail -n 1 "../likelihood/results/prolazak$i/diff$j" | grep -Eo '[0-9]+$')
		a=${array[$j]}
		array[$j]=$(($(($a))+$(($number))))
	done
done

for i in $(seq 5 10)
do
	echo $((${array[$i]}))
done

