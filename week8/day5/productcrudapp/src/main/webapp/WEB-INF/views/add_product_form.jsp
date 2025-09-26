<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    
    <%-- INCLUDE BASE.JSP HERE --%>
    <%@include file="./base.jsp" %>
    
    <style>
        .card {
            border-radius: 1rem;
        }
    </style>
</head>
<body class="bg-light">

    <div class="container mt-5">
        <div class="row">
            <div class="col-md-8 offset-md-2">
                <div class="card shadow">
                    <div class="card-header bg-secondary text-white text-center">
                        <h4 class="mb-0">Add New Product</h4>
                    </div>
                    <div class="card-body">
                        
                        <form:form action="handle-product" method="post" modelAttribute="product">

                            <div class="mb-3">
                                <label for="name" class="form-label">Product Name</label>
                                <form:input path="name" 
                                            cssClass="form-control" 
                                            placeholder="Enter product name" 
                                            id="name" required="true" />
                                <form:errors path="name" cssClass="text-danger small" />
                            </div>

                            <%-- Product Description --%>
                            <div class="mb-3">
                                <label for="description" class="form-label">Description</label>
                                <form:textarea path="description" 
                                               cssClass="form-control" 
                                               placeholder="Enter product description" 
                                               id="description" 
                                               rows="5" required="true" />
                                <form:errors path="description" cssClass="text-danger small" />
                            </div>

                            <%-- Product Price --%>
                            <div class="mb-3">
                                <label for="price" class="form-label">Product Price (₹)</label>
                                <form:input path="price" 
                                            type="number" 
                                            cssClass="form-control" 
                                            placeholder="Enter price" 
                                            id="price" required="true" />
                                <form:errors path="price" cssClass="text-danger small" />
                            </div>

                            <%-- Buttons: Back and Submit --%>
                            <div class="container text-center mt-4">
                                
                                <a href="<c:url value='/' />" class="btn btn-outline-secondary me-2">
                                    <i class="fas fa-arrow-left"></i> Back to Home
                                </a>
                                
                                <button type="submit" class="btn btn-secondary">
                                    Add Product
                                </button>
                            </div>
                        </form:form>

                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <%-- Icon dependency for the back arrow --%>
    <script src="https://kit.fontawesome.com/a076d05399.js" crossorigin="anonymous"></script>
</body>
</html>