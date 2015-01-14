<?
	mysql_connect("adimur.noip.me", "adimur", "1234567890"); 
	mysql_select_db("adimur");
//	mysql_connect("mysql.hostinger.ru", "u605665531_test", "qwe2147564"); 
//	mysql_select_db("u605665531_test");
	$table = "loc_log"; 
	$str = " ";
	$LIST=array(); 
	if (isset($_GET['startfrom']))
		$startfrom=$_GET['startfrom'];
	else
		$startfrom=0;
	$query="SELECT * FROM $table where _id > $startfrom ORDER BY _id ASC LIMIT 100 ";  
	$res=mysql_query($query); 
	while($row=mysql_fetch_assoc($res)) 
		$LIST[]=$row;
	foreach ($LIST as $row):
		$id=$row['_id'];
		$idwho=$row['idwho'];
		$lat=$row['Latitude'];
		$lon=$row['Longitude'];		
		$alt=$row['Altitude'];
		$sp=$row['Speed'];
		$br=$row['Bearing'];
		$acc=$row['Accuracy'];
		$pr=$row['Provider'];
		$dt=$row['DTime'];
		$d_t=$row['date_time'];
		$str .= "\n <tr> <td>".$id."</td> <td>".$idwho."</td><td>".$lat."</td><td>".$lon."</td><td>".$alt
		."</td><td>".$sp."</td><td>".$br."</td><td>".$acc."</td><td>".$pr."</td><td>".$dt."</td><td>".$d_t
		."</td></tr>";
	endforeach;
	header("HTTP/1.1 200 OK");
?><html>
  <head>
  </head>
  <body >
	<table border=1>
	<?=$str?>
	</table>
  </body>
</html>
