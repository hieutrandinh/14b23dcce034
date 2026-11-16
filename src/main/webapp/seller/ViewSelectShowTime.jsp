<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="org.example.cinema.entity.ShowTime" %>
<%
    if (session.getAttribute("currentSeller") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    List<ShowTime> showTimes = (List<ShowTime>) request.getAttribute("showTimes");
    String selectedMovieName = (String) request.getAttribute("movieName");
    Object showTimeSearchDate = request.getAttribute("showTimeSearchDate");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Select Show Time</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/style.css">
</head>
<body>
<div class="box">
    <div class="module-header">
        <div class="module-function">Seller Sells Tickets at the Counter</div>
    </div>
    <h2>3. Select Show Time</h2>
    <%@ include file="flow-error.jspf" %>
    <p>Movie: <%= selectedMovieName %></p>
    <form class="date-search" method="get" action="<%=request.getContextPath()%>/sell">
        <input type="hidden" name="action" value="searchShowTime">
        <label for="showDate">Search by date</label>
        <div class="date-search-controls">
            <input id="showDate" type="date" name="showDate"
                   value="<%= showTimeSearchDate == null ? "" : showTimeSearchDate %>">
            <button type="submit">Search</button>
            <% if (showTimeSearchDate != null) { %>
            <a class="button-link" href="<%=request.getContextPath()%>/sell?action=searchShowTime">Clear</a>
            <% } %>
        </div>
    </form>
    <table>
        <tr><th>Code</th><th>Date</th><th>Start</th><th>Rate</th><th></th></tr>
        <% for (ShowTime st : showTimes) { %>
        <tr>
            <td>SC<%= String.format("%02d", st.getId()) %></td>
            <td><%= st.getShowDate() %></td>
            <td><%= st.getStartTime() %></td>
            <td><%= st.getPriceMultiplier() %></td>
            <td><a href="<%=request.getContextPath()%>/sell?action=selectShowTime&showTimeId=<%= st.getId() %>">select</a></td>
        </tr>
        <% } %>
        <% if (showTimes.isEmpty()) { %>
        <tr><td colspan="5">
            <%= showTimeSearchDate == null
                ? "No show time is available for this movie."
                : "No show time is available on the selected date." %>
        </td></tr>
        <% } %>
    </table>
    <a href="<%=request.getContextPath()%>/sell?action=backToSearch"><button type="button">Back</button></a>
</div>
</body>
</html>
