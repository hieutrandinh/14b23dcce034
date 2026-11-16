<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/style.css">
</head>
<body>
<div class="box">
    <h2>Something went wrong</h2>
    <div class="error"><%= exception != null ? exception.getMessage() : "Unknown error" %></div>
    <button type="button" onclick="history.back()">&#8617; Back</button>
</div>
</body>
</html>
