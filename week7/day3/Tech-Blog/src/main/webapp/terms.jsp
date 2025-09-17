<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Terms and Conditions</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" crossorigin="anonymous">
</head>
<body class="bg-light">

<!-- Navbar include (optional) -->
<%@ include file="normal_navbar.jsp" %>

<div class="container mt-5">
    <div class="card shadow-lg border-0 rounded-3">
        <div class="card-header bg-primary text-white">
            <h3 class="mb-0">Terms and Conditions</h3>
        </div>
        <div class="card-body p-4" style="max-height: 70vh; overflow-y: auto;">
            
            <h5>1. Introduction</h5>
            <p>
                Welcome to our website. By registering or using this service, you agree to the following terms and conditions. 
                Please read them carefully.
            </p>

            <h5>2. User Responsibilities</h5>
            <ul>
                <li>You must provide accurate information during registration.</li>
                <li>You are responsible for maintaining the confidentiality of your account.</li>
                <li>You agree not to misuse the platform for illegal or harmful purposes.</li>
            </ul>

            <h5>3. Privacy</h5>
            <p>
                We respect your privacy and protect your data according to our Privacy Policy. 
                We will not share your personal information without your consent.
            </p>

            <h5>4. Limitations</h5>
            <p>
                We are not liable for any damages or losses caused by the use of our platform. 
                The service is provided “as is” without any warranties.
            </p>

            <h5>5. Changes to Terms</h5>
            <p>
                We may update these terms at any time. Continued use of the platform means you agree to the updated terms.
            </p>

            <h5>6. Governing Law</h5>
            <p>
                These terms shall be governed by the laws of your country/state. Any disputes shall be resolved under its jurisdiction.
            </p>

        </div>
        <div class="card-footer text-end">
            <a href="register_page.jsp" class="btn btn-secondary">Back to Register</a>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
