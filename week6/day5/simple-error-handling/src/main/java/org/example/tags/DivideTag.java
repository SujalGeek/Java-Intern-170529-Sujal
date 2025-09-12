package org.example.tags;

import java.io.IOException;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

public class DivideTag extends SimpleTagSupport {
    private double n1;
    private double n2;

    // setters for tag attributes
    public void setN1(double n1) { this.n1 = n1; }
    public void setN2(double n2) { this.n2 = n2; }

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        if (n2 == 0) {
            out.println("<div style='color:red; display:flex; justify-content:center;'>Error: Division by zero!</div>");
        } else {
            out.println(n1 + " / " + n2 + " = " + (n1 / n2));
        }
    }
}
