#!/usr/bin/perl

$i = 0;
$limit = $ARGV[1];
$step = int(rand(100));

foreach(1..$step){
	$row = <>;
	$row = <>;
}

$row = <>;

while($i < $limit) {
	print "$row";
	$newrow = <>;
	$flag = 1;
	while($flag and defined($row)){
		$row = <>;
		if($row =~ m/\>.*/){
			$flag = 0;
		} else {
			chomp($row);
			$newrow = "$newrow$row";
		}
	}
	print "$newrow";
	$i += 1;
	
	foreach(1..$step){
		$row = <>;
		$row = <>;
	}	
}
