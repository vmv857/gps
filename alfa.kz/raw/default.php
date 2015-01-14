<?
	mysql_connect("adimur.noip.me", "adimur", "1234567890"); 
	mysql_select_db("adimur");
//	mysql_connect("mysql.hostinger.ru", "u605665531_test", "qwe2147564"); 
//	mysql_select_db("u605665531_test");
	$table = "chat"; 
	$str = " ";
	$LIST=array(); 
	if (isset($_GET['startfrom']))
		$startfrom=$_GET['startfrom'];
	else
		$startfrom=0;
	$query="SELECT * FROM $table where id > $startfrom ORDER BY dtime DESC";  
	$res=mysql_query($query); 
	while($row=mysql_fetch_assoc($res)) 
		$LIST[]=$row;
	foreach ($LIST as $row):
		$id=$row['id'];
		$who=$row['who'];
		$what=$row['what'];
		$when=$row['dtime'];		
		$to=$row['toh'];
		$udt=$row['udt'];
		$str .= "\n <tr> <td>".$id."</td> <td>".$who."</td><td>".$what."</td><td>".$when."</td><td>".$to."</td><td>".$udt."</td></tr>";
	endforeach;
	header("HTTP/1.1 200 OK");
?><html>
  <head> <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  </head>
  <body >
	<table border=1>
	<?=$str?>
	</table>
  </body>
</html>
