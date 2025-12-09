package edu.neu.curriculumRecommendation.util;

import edu.neu.curriculumRecommendation.entity.Course;
import edu.neu.curriculumRecommendation.entity.Enrollment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Grade utility for GPA calculations (Northeastern University 4.0 scale)
 */
public final class GradeUtil {

    private GradeUtil() {
    }

    /**
     * Convert letter grade to GPA value (NEU official scale).
     * Non-GPA-impacting grades (S/U/P/W/I) return null.
     */
    public static Double gradeToGPA(String grade) {
        if (grade == null) {
            return null;
        }
        String g = grade.trim().toUpperCase();
        switch (g) {
            case "A":
                return 4.000;
            case "A-":
                return 3.667;
            case "B+":
                return 3.333;
            case "B":
                return 3.000;
            case "B-":
                return 2.667;
            case "C+":
                return 2.333;
            case "C":
                return 2.000;
            case "C-":
                return 1.667;
            case "D+":
                return 1.333;
            case "D":
                return 1.000;
            case "D-":
                return 0.667;
            case "F":
                return 0.000;
            // Non-GPA grades
            case "S":
            case "U":
            case "P":
            case "W":
            case "I":
            default:
                return null;
        }
    }

    /**
     * Calculate weighted GPA for completed enrollments.
     * Returns null if no GPA-impacting courses are present.
     */
    public static Double calculateWeightedGPA(List<Enrollment> enrollments) {
        if (enrollments == null || enrollments.isEmpty()) {
            return null;
        }

        double totalQualityPoints = 0.0;
        int totalCredits = 0;

        for (Enrollment enrollment : enrollments) {
            if (enrollment == null || enrollment.getStatus() == null) {
                continue;
            }
            if (!"COMPLETED".equalsIgnoreCase(enrollment.getStatus())) {
                continue;
            }

            String grade = enrollment.getGrade();
            Double gpaValue = gradeToGPA(grade);
            if (gpaValue == null) {
                // Non-GPA impacting grades are skipped
                continue;
            }

            Course course = enrollment.getCourse();
            if (course == null || course.getCredits() == null) {
                continue;
            }
            int credits = course.getCredits();
            totalQualityPoints += gpaValue * credits;
            totalCredits += credits;
        }

        if (totalCredits == 0) {
            return null;
        }

        double gpa = totalQualityPoints / totalCredits;
        return roundToThreeDecimals(gpa);
    }

    /**
     * Round GPA to three decimals (e.g., 3.667)
     */
    public static Double roundToThreeDecimals(Double value) {
        if (value == null) {
            return null;
        }
        return BigDecimal.valueOf(value)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();
    }
}

