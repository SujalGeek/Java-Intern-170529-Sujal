<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Spring Search App</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
	<link href="<c:url value="/resources/css/style.css" />"  rel="stylesheet" />
<script src="<c:url value="/resources/js/script.js" />"></script>
</head>
<body class="bg-light">
<%-- <img alt="myimage" src="<c:url value="/resources/image/profile.png"/>" /> --%>
<div class="container">
    <div class="card mx-auto mt-5 shadow-lg" style="max-width: 600px;">
        <div class="card-body bg-secondary text-white rounded">
            <h3 class="text-center text-uppercase mb-4">My Search</h3>
            
            <!-- Show error message if present -->
            <c:if test="${not empty errorMsg}">
                <div class="alert alert-danger text-center">${errorMsg}</div>
            </c:if>
            
            <form class="mt-3" action="search">
                <div class="mb-3">
                    <input type="text" name="querybox" placeholder="Enter keyword to search"
                           class="form-control" />
                </div>
                <div class="d-grid">
                    <button type="submit" class="btn btn-outline-light">Search Google</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
