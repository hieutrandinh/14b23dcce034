<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.example.cinema.entity.Seat" %>
<%
    if (session.getAttribute("currentSeller") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    List<Seat> seatMap = (List<Seat>) request.getAttribute("seatMap");
    Set<Integer> selectedSeatIds = (Set<Integer>) request.getAttribute("seatIds");
    Integer selectedShowTimeId = (Integer) request.getAttribute("showTimeId");
    String selectedRoomName = (String) request.getAttribute("roomName");
    String selectedMovieName = (String) request.getAttribute("movieName");
    String paymentError = (String) request.getAttribute("paymentError");
    double seatPrice = (Double) request.getAttribute("seatPrice");
    String seatError = (String) request.getAttribute("seatError");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Select Seat</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/style.css">
</head>
<body>
<div class="box">
    <div class="module-header">
        <div class="module-function">Seller Sells Tickets at the Counter</div>
    </div>
    <h2>5. Select Seat</h2>
    <%@ include file="flow-error.jspf" %>
    <p>Show: SC<%= String.format("%02d", selectedShowTimeId) %> - Room: <%= selectedRoomName %> - Movie: <%= selectedMovieName %></p>

    <% if (seatError != null) { %>
    <div class="error"><%= seatError %></div>
    <% } %>
    <% if (paymentError != null) { %>
    <div class="error"><%= paymentError %></div>
    <% } %>

    <p style="text-align:center;background:#ccc;">SCREEN</p>

    <table class="seat-grid">
        <%
            String currentRow = null;
            for (Seat s : seatMap) {
                if (!s.getSeatRow().equals(currentRow)) {
                    if (currentRow != null) { out.print("</tr>"); }
                    out.print("<tr>");
                    currentRow = s.getSeatRow();
                }
                boolean picked = selectedSeatIds.contains(s.getId());
                boolean sold = s.isSold();
                String cssClass = sold ? "seat-sold" : (picked ? "seat-picked" : "seat-free");
        %>
        <td class="<%= cssClass %>">
            <% if (sold) { %>
                <%= s.getLabel() %>
            <% } else if (picked) { %>
                <a href="<%=ctx%>/sell?action=deleteSeat&seatId=<%= s.getId() %>"><%= s.getLabel() %></a>
            <% } else { %>
                <a href="<%=ctx%>/sell?action=selectSeat&seatId=<%= s.getId() %>"><%= s.getLabel() %></a>
            <% } %>
        </td>
        <% } %>
        </tr>
    </table>
    <p class="hint">green=free &nbsp; yellow=selected &nbsp; grey=sold</p>

    <table>
        <tr><th>Row</th><th>No.</th><th>Price</th><th></th></tr>
        <%
            for (Seat s : seatMap) {
                if (!selectedSeatIds.contains(s.getId())) { continue; }
        %>
        <tr>
            <td><%= s.getSeatRow() %></td>
            <td><%= s.getSeatNumber() %></td>
            <td><%= String.format("%,.0f", seatPrice) %></td>
            <td><a href="<%=ctx%>/sell?action=deleteSeat&seatId=<%= s.getId() %>">remove</a></td>
        </tr>
        <% } %>
    </table>
    <p>Total: <%= selectedSeatIds.size() %> seats - <%= String.format("%,.0f", seatPrice * selectedSeatIds.size()) %></p>

    <a href="<%=ctx%>/sell?action=backToRoom"><button type="button">Back</button></a>
    <a href="<%=ctx%>/sell?action=continueSeat"><button type="button" <%= selectedSeatIds.isEmpty() ? "disabled" : "" %>>Continue</button></a>
</div>
</body>
</html>
