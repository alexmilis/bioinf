#!/usr/bin/perl

@times = ();
@changes = ();

$row = <>;

while(defined($row)) {
	if($row =~ m/resources\/random_data\/prolazak(\d+)\/random(\d+).fasta/){
		$i = $2;
		$row = <>;
		
		$row =~ m/(\d+)/;
		$changes[$i] += $1;

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
	$change = $changes[$_] / 10.0;
	print "$_ " . " $time " . " $change \n";
}	
