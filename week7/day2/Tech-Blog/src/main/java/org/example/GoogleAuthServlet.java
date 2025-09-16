package org.example;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleAuthServlet extends HttpServlet {

    private static final String CLIENT_ID = "61271818010-uacdkgt9pef25bs1f5ff3u9kk00tm8lm.apps.googleusercontent.com";
    private static final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String credential = request.getParameter("credential");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if (credential == null) {
            out.println("No credential received!");
            return;
        }

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(), jsonFactory)
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(credential);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");

                // Store user in session
                HttpSession session = request.getSession();
                session.setAttribute("userEmail", email);
                session.setAttribute("userName", name);
                session.setAttribute("userPicture", pictureUrl);

                // Redirect to index.jsp or dashboard
                response.sendRedirect("index.jsp");

            } else {
                out.println("Invalid ID token.");
            }

        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
