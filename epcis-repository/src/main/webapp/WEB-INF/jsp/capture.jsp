<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<title>EPCIS Capture Service</title>
	<link rel="stylesheet" type="text/css" href="static/style.css" />
</head>
<body>
	<%@ include file="banner.jsp" %>
	<div id="content">
		<h1>EPCIS Capture Service</h1>
		<div>
			This service captures EPCIS events sent to it using HTTP POST requests.<br />
	        The payload of the HTTP POST request is expected to be an XML document conforming to the EPCISDocument schema.
		</div>
	    <br />
	    <br />
	    <div style="font-size:10px">
	    	<a href="capture?showCaptureForm=true">&raquo; show HTML capture form</a>
	    </div>
	</div>
</body>
</html>