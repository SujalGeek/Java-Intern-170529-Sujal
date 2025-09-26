<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Upload Status</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" 
      rel="stylesheet"
      integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" 
      crossorigin="anonymous">
</head>
<body class="bg-light">

    <div class="container mt-5">
        <div class="card shadow-sm">
            <div class="card-body text-center">
                
                <h1 class="text-success mb-3">✅ Success!</h1>
                
                <p class="lead">${msg}</p>
                
                <p class="text-muted">File Name: ${fileName}</p>

                <hr>
                <a href="fileform" class="btn btn-primary">Upload Another File</a>
                
            </div>
        </div>
    </div>

</body>
</html>