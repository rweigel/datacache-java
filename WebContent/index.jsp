<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="source.*" %>   
<%@ page import="java.util.*" %> 

<%
String url = request.getParameter("urls");

String maxThread = request.getParameter("maxThread");
if (maxThread == null || maxThread.isEmpty()) {
 	maxThread = "16";
}

String forceUpdate = request.getParameter("forceUpdate");
System.out.println("forceUpdate: " + forceUpdate);
if (forceUpdate == null || forceUpdate.isEmpty()) {
	forceUpdate = "true";
}

System.out.println("url: " + url);
System.out.println("forceUpdate: " + forceUpdate);
System.out.println("maxThread: " + maxThread);

if (url != null) {
	String[] urls = url.split("\n");
	syncsubmit downloader = new syncsubmit();
	downloader.getFiles(urls,maxThread,forceUpdate);
}
 %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<html>
		<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Data Cache</title>
	</head>	
	<body>	
		<form name="input" action="index.jsp" method="POST">
			<fieldset>
				<legend>URLs</legend>
				<input type="hidden" name="maxThread" id="maxThread" value="<%=maxThread%>" />
				<input type="hidden" name="forceUpdate" id="forceUpdate" value="<%=forceUpdate%>" />
				<br>
				<textarea name="urls" id="urls" rows="10" cols="50">http://datacache.org/dc/demo/file1.txt</textarea>
				<br>
				<input type="submit" value="Submit" />
			</fieldset>
		</form>
	</body>
</html>