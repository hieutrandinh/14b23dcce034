<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="org.example.cinema.entity.MemberCard" %>
<%
    MemberCard card = (MemberCard) request.getAttribute("card");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Registration Successful</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/style.css">
</head>
<body>
<div class="box">
    <h2>Registration Successful</h2>
    <div class="success">Your registration has been completed successfully.</div>
    <table>
        <tr><th>Member card code</th><td><%= card.getCardCode() %></td></tr>
        <tr><th>Full name</th><td><%= request.getAttribute("fullName") %></td></tr>
        <tr><th>Registration date</th><td><%= card.getRegistrationDate() %></td></tr>
        <tr><th>Loyalty points</th><td><%= card.getLoyaltyPoint() %></td></tr>
    </table>
    <a href="<%=request.getContextPath()%>/ViewHome.jsp"><button type="button">Finish</button></a>
    <p><a href="<%=request.getContextPath()%>/ViewHome.jsp">&#8617; Return to the Cinema Home Page</a></p>
</div>
</body>
</html>
