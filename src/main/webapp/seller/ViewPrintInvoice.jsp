<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="org.example.cinema.entity.Invoice" %>
<%@ page import="org.example.cinema.entity.Ticket" %>
<%
    if (session.getAttribute("currentSeller") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    Invoice invoice = (Invoice) request.getAttribute("invoice");
    List<Ticket> tickets = (List<Ticket>) request.getAttribute("tickets");
    String paymentMethod = (String) request.getAttribute("paymentMethod");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Print Invoice</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/style.css">
</head>
<body>
<div class="box">
    <div class="module-header">
        <div class="module-function">Seller Sells Tickets at the Counter</div>
    </div>
    <h2>8. Print Invoice</h2>
    <%@ include file="flow-error.jspf" %>
    <div class="success">Ticket sold successfully.</div>

    <h3>TICKET</h3>
    <table>
        <tr><th>Movie</th><th>Room</th><th>Show time</th><th>Seat</th><th>Price</th></tr>
        <% for (Ticket t : tickets) { %>
        <tr>
            <td><%= t.getMovieName() %></td>
            <td><%= t.getRoomName() %></td>
            <td><%= t.getShowDateTime() %></td>
            <td><%= t.getSeatLabel() %></td>
            <td><%= String.format("%,.0f", t.getPrice()) %></td>
        </tr>
        <% } %>
    </table>

    <h3>INVOICE</h3>
    <table>
        <tr><th>Code</th><td><%= invoice.getInvoiceCode() %></td></tr>
        <tr><th>Tickets</th><td><%= tickets.size() %></td></tr>
        <tr><th>Method</th><td><%= paymentMethod %></td></tr>
        <tr><th>Subtotal</th><td><%= String.format("%,.0f", invoice.getTotalAmount()) %></td></tr>
        <tr><th>Discount</th><td><%= String.format("%,.0f", invoice.getDiscountAmount()) %></td></tr>
        <tr><th>Total</th><td><%= String.format("%,.0f", invoice.getFinalAmount()) %></td></tr>
    </table>

    <a href="<%=request.getContextPath()%>/sell?action=finish"><button type="button">Finish</button></a>
</div>
</body>
</html>
