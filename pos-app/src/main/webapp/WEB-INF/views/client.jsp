<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>Clients</title></head>
<body>
<h2>Clients</h2>
<form action="client" method="post">
    Name: <input type="text" name="name" required/>
    Email: <input type="email" name="email" required/>
    <button type="submit">Add</button>
</form>
<table border="1">
    <tr><th>Name</th><th>Email</th></tr>
    <c:forEach var="c" items="${data}">
        <tr>
            <td>${c.name}</td>
            <td>${c.email}</td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
