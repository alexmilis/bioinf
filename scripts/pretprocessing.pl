#!/usr/bin/perl

$i = 0;

$row = <>;

while(defined($row)) {
	print "$row";
	$row = <>;	
	$newrow = "";
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
	print "$newrow\n";
	$i += 1;
}

print $i;
