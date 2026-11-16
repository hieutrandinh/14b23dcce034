<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Member Registration</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/style.css">
</head>
<body>
<div class="box">
    <h2>Member Registration</h2>
    <p>Please fill in your personal information. Fields marked (*) are required.</p>

    <% if (request.getAttribute("error") != null) { %>
    <div class="error"><%= request.getAttribute("error") %></div>
    <% } %>

    <form action="<%=request.getContextPath()%>/register" method="post">
        <label>Full name (*)</label>
        <input type="text" name="fullName" placeholder="e.g. Nguyen Van A"
               value="<%= request.getAttribute("fullName") != null ? request.getAttribute("fullName") : "" %>" required>

        <label>Date of birth (*)</label>
        <input type="text" name="dateOfBirth" placeholder="dd/mm/yyyy"
               value="<%= request.getAttribute("dateOfBirth") != null ? request.getAttribute("dateOfBirth") : "" %>" required>

        <label>Phone number (*)</label>
        <input type="text" name="phoneNumber" placeholder="10 digits, e.g. 0912345678"
               value="<%= request.getAttribute("phoneNumber") != null ? request.getAttribute("phoneNumber") : "" %>" required>

        <label>Email</label>
        <input type="text" name="email" placeholder="e.g. nguyenvana@gmail.com"
               value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>">

        <label>Address</label>
        <input type="text" name="address" placeholder="e.g. Ha Noi"
               value="<%= request.getAttribute("address") != null ? request.getAttribute("address") : "" %>">

        <label>Username (*)</label>
        <input type="text" name="username" placeholder="6 - 20 characters"
               value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>" required>

        <label>Password (*)</label>
        <input type="password" name="password" placeholder="at least 8 characters" required>

        <button type="submit">Register</button>
        <a href="<%=request.getContextPath()%>/ViewHome.jsp"><button type="button">Back to Home</button></a>
    </form>
</div>
</body>
</html>
