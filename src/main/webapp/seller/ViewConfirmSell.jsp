<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="org.example.cinema.entity.MemberCard" %>
<%
    if (session.getAttribute("currentSeller") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    double subtotal = (Double) request.getAttribute("subtotal");
    String seatLabels = (String) request.getAttribute("seatLabels");
    String cardError = (String) request.getAttribute("cardError");
    MemberCard card = (MemberCard) request.getAttribute("memberCard");
    double discountAmount = (Double) request.getAttribute("discountAmount");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Confirm Sale</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/style.css">
</head>
<body>
<div class="box">
    <div class="module-header">
        <div class="module-function">Seller Sells Tickets at the Counter</div>
    </div>
    <h2>6. Confirm Sale</h2>
    <%@ include file="flow-error.jspf" %>
    <p>Seats: <%= seatLabels %> &nbsp;&nbsp; Subtotal: <%= String.format("%,.0f", subtotal) %></p>

    <% if (cardError != null) { %>
    <div class="error"><%= cardError %></div>
    <% } %>

    <form action="<%=ctx%>/sell" method="get">
        <input type="hidden" name="action" value="checkCard">
        <label>Card code</label>
        <input type="text" name="cardCode" value="<%= card != null ? card.getCardCode() : "" %>" placeholder="e.g. MC0001">
        <button type="submit">Check card</button>
    </form>

    <p>Discount: <%= String.format("%,.0f", discountAmount) %></p>
    <% if (card == null) { %>
    <p class="hint">Invalid card &#8594; discount = 0, skip this step.</p>
    <% } %>

    <a href="<%=ctx%>/sell?action=backToSeat"><button type="button">Back</button></a>
    <a href="<%=ctx%>/sell?action=confirmSell"><button type="button">Confirm sale</button></a>
</div>
</body>
</html>
