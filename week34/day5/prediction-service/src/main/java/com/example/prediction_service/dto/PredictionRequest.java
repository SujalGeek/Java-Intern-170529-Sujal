package com.example.prediction_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionRequest {

	private Double attendancePercentage;
	private Double quizAverage;
	private Double assignmentAverage;
	private Double midTermScore;
	private Integer participationScore;
	private Double studyHoursPerWeek;
	private Double previousGpa;
	
	public double[] toFeatureArray() {
		return new double[]
				{
						attendancePercentage != null ? attendancePercentage : 0.0,
						quizAverage != null ? quizAverage : 0.0,
								assignmentAverage != null ? assignmentAverage : 0.0,
							    midTermScore != null ? midTermScore : 0.0,
							    participationScore != null ? participationScore.doubleValue() : 0.0,
							    studyHoursPerWeek != null ? studyHoursPerWeek : 0.0,
							    previousGpa != null ? previousGpa : 0.0				
				};
	}
	
	public boolean isValid() {
        return attendancePercentage != null && attendancePercentage >= 0 && attendancePercentage <= 100
            && quizAverage != null && quizAverage >= 0 && quizAverage <= 100
            && assignmentAverage != null && assignmentAverage >= 0 && assignmentAverage <= 100
            && midTermScore != null && midTermScore >= 0 && midTermScore <= 100
            && participationScore != null && participationScore >= 1 && participationScore <= 10
            && studyHoursPerWeek != null && studyHoursPerWeek >= 0 && studyHoursPerWeek <= 20
            && previousGpa != null && previousGpa >= 0.0 && previousGpa <= 4.0;
    }
}
	
