<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    if (session.getAttribute("currentSeller") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    double subtotal = (Double) request.getAttribute("subtotal");
    double discountAmount = (Double) request.getAttribute("discountAmount");
    String paymentError = (String) request.getAttribute("paymentError");
    double finalAmount = subtotal - discountAmount;
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Payment</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/style.css">
</head>
<body>
<div class="box">
    <div class="module-header">
        <div class="module-function">Seller Sells Tickets at the Counter</div>
    </div>
    <h2>7. Payment</h2>
    <%@ include file="flow-error.jspf" %>

    <% if (paymentError != null) { %>
    <div class="error"><%= paymentError %></div>
    <% } %>

    <form action="<%=ctx%>/sell" method="post">
        <input type="hidden" name="action" value="confirmPayment">
        <label>Subtotal</label>
        <input type="text" value="<%= String.format("%,.0f", subtotal) %>" readonly>
        <label>Discount</label>
        <input type="text" value="<%= String.format("%,.0f", discountAmount) %>" readonly>
        <label>Total to pay</label>
        <input type="text" value="<%= String.format("%,.0f", finalAmount) %>" readonly>
        <label>Method</label>
        <select name="method">
            <option value="Cash">Cash</option>
            <option value="Bank Transfer">Bank Transfer</option>
        </select>
        <button type="submit">Confirm payment</button>
    </form>
    <% if (paymentError != null) { %>
    <p class="hint">Payment failed &#8594; select seats again.</p>
    <% } %>

    <a href="<%=ctx%>/sell?action=backToSeat"><button type="button">Back</button></a>
</div>
</body>
</html>
