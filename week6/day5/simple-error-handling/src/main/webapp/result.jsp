<%@ taglib uri="http://example.com/tags/math" prefix="m" %>
<!DOCTYPE html>
<html>
<head>
  <title>Division with Custom Tag</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
  <div class="card">
    <div class="card-header bg-dark text-white text-center">Custom Tag Division</div>
    <div class="card-body">
      <%
        double n1 = Double.parseDouble(request.getParameter("n1"));
        double n2 = Double.parseDouble(request.getParameter("n2"));
      %>
      <!-- Using our custom tag -->
      <m:divide n1="<%= n1 %>" n2="<%= n2 %>" />
    </div>
    <div class="card-footer text-center">
      <a href="index.jsp" class="btn btn-dark">Back</a>
    </div>
  </div>
</div>
</body>
</html>
