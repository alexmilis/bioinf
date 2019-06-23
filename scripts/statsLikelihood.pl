#!/usr/bin/perl

@times = ();

$row = <>;

while(defined($row)) {
	if($row =~ m/resources\/random_data\/prolazak(\d+)\/random(\d+).fasta/){
		$i = $2;
		$row = <>;
		$row = <>;
		$row = <>;
		$row =~ m/(\d+)/;
		$times[$i] += $1;
	}
	$row = <>;
}

foreach(5..10){
	$time = $times[$_] / 10.0;
	print "$_ " . " $time " . " $change \n";
}	
