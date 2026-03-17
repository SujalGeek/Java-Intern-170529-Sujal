package com.example.performance_service.util;

public class GradeUtil {

    public static String calculateGrade(double percentage) {

        if (percentage >= 85) return "A";
        if (percentage >= 70) return "B";
        if (percentage >= 55) return "C";
        if (percentage >= 40) return "D";
        return "F";
    }
}