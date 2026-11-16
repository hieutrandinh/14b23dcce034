<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="org.example.cinema.entity.Seller" %>
<%
    Seller seller = (Seller) session.getAttribute("currentSeller");
    if (seller == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Seller Home Page</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/style.css">
</head>
<body>
<div class="box">
    <div class="module-header">
        <div class="module-function">Seller Sells Tickets at the Counter</div>
    </div>
    <h2>1. Seller Home Page</h2>
    <%@ include file="flow-error.jspf" %>
    <p>Logged in as: <%= seller.getFullName() %> (<%= seller.getCounterPosition() %>)</p>
    <form action="<%=request.getContextPath()%>/sell" method="get">
        <input type="hidden" name="action" value="start">
        <button type="submit">Sell Ticket</button>
    </form>
    <p class="hint">(the seller clicks this menu to start)</p>
    <p><a href="<%=request.getContextPath()%>/logout">Logout</a></p>
</div>
</body>
</html>
