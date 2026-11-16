<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="org.example.cinema.entity.Movie" %>
<%
    if (session.getAttribute("currentSeller") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    List<Movie> movies = (List<Movie>) request.getAttribute("movies");
    String movieName = (String) request.getAttribute("movieName");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Search Movie</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/style.css">
</head>
<body>
<div class="box">
    <div class="module-header">
        <div class="module-function">Seller Sells Tickets at the Counter</div>
    </div>
    <h2>2. Search Movie</h2>
    <%@ include file="flow-error.jspf" %>
    <form action="<%=request.getContextPath()%>/sell" method="get">
        <input type="hidden" name="action" value="search">
        <label>Movie name</label>
        <input type="text" name="movieName" value="<%= movieName != null ? movieName : "" %>" placeholder="Avengers">
        <button type="submit">Search</button>
    </form>

    <% if (movies != null) { %>
    <table>
        <tr><th>Code</th><th>Title</th><th>Genre</th><th>Base price</th><th></th></tr>
        <% for (Movie m : movies) { %>
        <tr>
            <td>MV<%= String.format("%02d", m.getId()) %></td>
            <td><%= m.getName() %></td>
            <td><%= m.getGenre() %></td>
            <td><%= String.format("%,.0f", m.getBasePrice()) %></td>
            <td><a href="<%=request.getContextPath()%>/sell?action=selectMovie&movieId=<%= m.getId() %>">select</a></td>
        </tr>
        <% } %>
        <% if (movies.isEmpty()) { %>
        <tr><td colspan="5">No movie found.</td></tr>
        <% } %>
    </table>
    <% } %>
</div>
</body>
</html>
