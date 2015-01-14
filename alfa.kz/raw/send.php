<?
	mysql_connect("adimur.noip.me", "adimur", "1234567890"); 
	mysql_select_db("adimur");
//	mysql_connect("mysql.hostinger.ru", "u605665531_test", "qwe2147564"); 
//	mysql_select_db("u605665531_test");
// http://vmchat.besaba.com/send.php?who=IamD2&what=hi%20to%20everybody&toh=AlL&udt=1111
	$table = "chat"; 
	if (isset($_GET['who'])){ 
		$who=$_GET['who'];
		$what=str_replace("'", "\"", $_GET['what']);
		$udt=$_GET['udt'];
		$to=$_GET['toh'];
		$ins_query = "  INSERT INTO chat ( who, what, toh, udt ) ";
		$ins_query .= "   VALUES ( '".$who."' ,'".$what."', '".$to."', '".$udt."' ); ";
		$res = mysql_query($ins_query) or trigger_error(mysql_error()." in ".$ins_query);
	}
	header("HTTP/1.1 200 OK");
?><?=$res?>
