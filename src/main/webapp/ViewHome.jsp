<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Cinema Home Page</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
<div class="box">
    <h2>Cinema Home Page</h2>
    <p>Welcome to the Cinema Ticketing System</p>
    <p><a href="<%=request.getContextPath()%>/register">Register as a member</a></p>
    <p><a href="<%=request.getContextPath()%>/login">Seller login</a></p>
</div>
</body>
</html>
