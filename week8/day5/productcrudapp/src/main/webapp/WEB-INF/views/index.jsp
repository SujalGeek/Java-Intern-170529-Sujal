
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%> 
<%@include file="./base.jsp" %>
<%@ page isELIgnored="false" %>
<html>
<head>
    <meta charset="UTF-8">
    <%@include file="./base.jsp" %>
</head>
<body>
    <div class="container mt-4">
        <h2 class="text-center mb-4">Available Products</h2>
        
        <div class="mb-3 text-end">
            <a href="add-product" class="btn btn-success">
                + Add Product
            </a>
        </div>

        <table class="table table-striped table-hover shadow-sm">
            <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Product Name</th>
                    <th>Description</th>
                    <th>Price</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="p" items="${products}">
                    <tr>
                        <td>${p.id}</td>
                        <td>${p.name}</td>
                        <td>${p.description}</td>
                        <td>₹${p.price}</td>
                        <td>
                           <a href="<c:url value='/delete-product/${p.id}'/>" class="btn btn-danger btn-sm">Delete</a>
                            <a href="<c:url value='/update-product/${p.id}'/>" class="btn btn-primary btn-sm">Edit</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        
        <c:if test="${empty products}">
            <div class="alert alert-info text-center">
                No products available. Click 'Add Product' to begin!
            </div>
        </c:if>

    </div>
</body>
</html>