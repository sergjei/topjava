<%--
  Created by IntelliJ IDEA.
  User: sergj
  Date: 07.02.2023
  Time: 14:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html lang="ru">
<head>
    <title>MealsCreateAndUpdate</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Edit meal</h2>
<form method=post action="meals">
    <input type="hidden" name="id" value="${meal.id}">
    <p>DateTime:<input type="datetime-local" name="dateTime"></p>
    <p>Description:<input type="text" name="description"></p>
    <p>Calories:<input type="text" name="calories"></p>
    <p><input type="submit" value="Save">
        <button onclick="window.history.back()" type="button">Cancel</button>
    </p>
</form>
</body>
</html>
