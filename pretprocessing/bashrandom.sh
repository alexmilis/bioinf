#!/bin/bash

for j in $(seq 1 10)
do
	mkdir "prolazak$j"
	for i in $(seq 5 10)
	do
		./pretp3.pl formatted.fasta $i | cat >> "prolazak$j/random$i.fasta"
	done
done
