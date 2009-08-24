<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>
<c:if test="${not empty errors}">
	<ul style="color: red;">
		<c:forEach var="error" items="${errors}">
			<li>${error.message}</li>
		</c:forEach>
	</ul>
</c:if>
<h2>${client.id }. ${client.name }</h2>
<ul>
	<c:forEach var="address" items="${client.addresses }">
		<li>${address.street }, ${address.number }</li>
	</c:forEach>
</ul>
<br />
<ul>
	<c:forEach var="email" items="${client.emails }">
		<li>${email }</li>
	</c:forEach>
</ul>
<br />
<br />
<a href="<c:url value="/clients/${client.id }"/>">view</a>
|
<a href="<c:url value="/clients/download/${client.id }"/>">download file</a>
|
<a href="<c:url value="/clients/${client.id }"/>?_method=DELETE">delete</a>
|
<a href="<c:url value="/clients"/>">list</a>


</body>
</html>