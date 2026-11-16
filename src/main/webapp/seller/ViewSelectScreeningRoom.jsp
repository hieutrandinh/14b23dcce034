<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="org.example.cinema.entity.ScreeningRoom" %>
<%@ page import="org.example.cinema.entity.ShowTime" %>
<%
    if (session.getAttribute("currentSeller") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    List<ScreeningRoom> rooms = (List<ScreeningRoom>) request.getAttribute("rooms");
    ShowTime showTime = (ShowTime) request.getAttribute("showTime");
    String selectedMovieName = (String) request.getAttribute("movieName");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Select Screening Room</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/style.css">
</head>
<body>
<div class="box">
    <div class="module-header">
        <div class="module-function">Seller Sells Tickets at the Counter</div>
    </div>
    <h2>4. Select Screening Room</h2>
    <%@ include file="flow-error.jspf" %>
    <p>Movie: <%= selectedMovieName %> | Show time: <%= showTime.getShowDate() %> <%= showTime.getStartTime() %></p>
    <table>
        <tr><th>Code</th><th>Room name</th><th>Total seats</th><th>Rate</th><th></th></tr>
        <% for (ScreeningRoom room : rooms) { %>
        <tr>
            <td>P<%= String.format("%02d", room.getId()) %></td>
            <td><%= room.getRoomName() %></td>
            <td><%= room.getTotalSeats() %></td>
            <td><%= room.getPriceMultiplier() %></td>
            <td><a href="<%=request.getContextPath()%>/sell?action=selectRoom&roomId=<%= room.getId() %>">select</a></td>
        </tr>
        <% } %>
        <% if (rooms.isEmpty()) { %>
        <tr><td colspan="5">No screening room is available for this show time.</td></tr>
        <% } %>
    </table>
    <a href="<%=request.getContextPath()%>/sell?action=backToShowTime"><button type="button">Back</button></a>
</div>
</body>
</html>
