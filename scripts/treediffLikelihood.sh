#!/bin/bash

for i in $(seq 1 10)
do	
	for j in $(seq 5 10)
	do
		cat "../likelihood/results/prolazak$i/tree$j.txt" > intree
		printf "\n" | cat >> intree
			cat "../resources/random_data/prolazak$i/nwk/likelihood$j.nwk" >> intree

		printf "r\nd\ny\n" | ./treedist

		cp outfile "../likelihood/results/prolazak$i/diff$j"
	done
done

