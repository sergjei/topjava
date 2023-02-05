<%--
  Created by IntelliJ IDEA.
  User: sergj
  Date: 03.02.2023
  Time: 21:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<html lang="ru">
<head>
    <title>Meals</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<table border="1" bordercolor="black">
    <tr>
        <th>Date</th>
        <th>Description</th>
        <th>Calories</th>
        <th></th>
        <th></th>
    </tr>
    <c:forEach items="${meals}" var="meal">
        <c:set var = "textColor" value = "${(meal.excess)? \"red\":\"green\"}"/>
        <tr style ="color:${textColor}">
            <td>
                <c:out value="${meal.dateTime.format( DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm\"))}"/>
            </td>
            <td>
                <c:out value="${meal.description}"/>
            </td>
            <td>
                <c:out value="${meal.calories}"/>
            </td>
            <td>
                <c:out value="update"/>
            </td>
            <td>
                <c:out value="delete"/>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
