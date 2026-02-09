"""
Train Machine Learning Models for Student Performance Prediction
Creates 3 models: Grade Classification, Score Prediction, Risk Assessment
Saves models in portable JSON format
"""

import pandas as pd
import numpy as np
import json
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.linear_model import LogisticRegression, LinearRegression
from sklearn.tree import DecisionTreeClassifier
from sklearn.metrics import accuracy_score, mean_absolute_error, classification_report
from sklearn.preprocessing import StandardScaler
import pickle
import os

class StudentPerformanceModels:
    def __init__(self, data_file='student_data.csv'):
        """Initialize and load data"""
        print("Loading dataset...")
        self.df = pd.read_csv(data_file)
        print(f"✅ Loaded {len(self.df)} student records")
        
        # Features for prediction
        self.feature_columns = [
            'attendance_percentage',
            'quiz_average',
            'assignment_average',
            'midterm_score',
            'participation_score',
            'study_hours_per_week',
            'previous_gpa'
        ]
        
        self.models = {}
        self.scalers = {}
        
    def prepare_data(self):
        """Split data into training and testing sets"""
        print("\nPreparing data...")
        
        X = self.df[self.feature_columns]
        
        # Grade classification
        y_grade = self.df['final_grade']
        self.X_train_grade, self.X_test_grade, self.y_train_grade, self.y_test_grade = \
            train_test_split(X, y_grade, test_size=0.2, random_state=42, stratify=y_grade)
        
        # Score prediction
        y_score = self.df['final_score']
        self.X_train_score, self.X_test_score, self.y_train_score, self.y_test_score = \
            train_test_split(X, y_score, test_size=0.2, random_state=42)
        
        # Risk assessment (Pass/Fail)
        y_risk = self.df['pass_fail']
        self.X_train_risk, self.X_test_risk, self.y_train_risk, self.y_test_risk = \
            train_test_split(X, y_risk, test_size=0.2, random_state=42, stratify=y_risk)
        
        print(f"✅ Training set: {len(self.X_train_grade)} samples")
        print(f"✅ Testing set: {len(self.X_test_grade)} samples")
    
    def train_grade_classifier(self):
        """Train grade classification model"""
        print("\n" + "="*60)
        print("TRAINING: Grade Classification Model")
        print("="*60)
        
        # Try Logistic Regression
        lr_model = LogisticRegression(max_iter=1000, random_state=42, multi_class='ovr')
        lr_model.fit(self.X_train_grade, self.y_train_grade)
        lr_pred = lr_model.predict(self.X_test_grade)
        lr_accuracy = accuracy_score(self.y_test_grade, lr_pred)
        
        # Try Decision Tree
        dt_model = DecisionTreeClassifier(max_depth=10, random_state=42)
        dt_model.fit(self.X_train_grade, self.y_train_grade)
        dt_pred = dt_model.predict(self.X_test_grade)
        dt_accuracy = accuracy_score(self.y_test_grade, dt_pred)
        
        print(f"Logistic Regression Accuracy: {lr_accuracy:.4f}")
        print(f"Decision Tree Accuracy: {dt_accuracy:.4f}")
        
        # Choose best model
        if lr_accuracy >= dt_accuracy:
            self.models['grade_classifier'] = lr_model
            chosen = "Logistic Regression"
            accuracy = lr_accuracy
            predictions = lr_pred
        else:
            self.models['grade_classifier'] = dt_model
            chosen = "Decision Tree"
            accuracy = dt_accuracy
            predictions = dt_pred
        
        print(f"\n✅ Selected: {chosen} (Accuracy: {accuracy:.4f})")
        
        # Classification report
        print("\nClassification Report:")
        print(classification_report(self.y_test_grade, predictions))
        
        return accuracy
    
    def train_score_predictor(self):
        """Train score prediction model"""
        print("\n" + "="*60)
        print("TRAINING: Score Prediction Model")
        print("="*60)
        
        # Linear Regression
        model = LinearRegression()
        model.fit(self.X_train_score, self.y_train_score)
        
        # Predictions
        predictions = model.predict(self.X_test_score)
        
        # Calculate metrics
        mae = mean_absolute_error(self.y_test_score, predictions)
        rmse = np.sqrt(np.mean((self.y_test_score - predictions)**2))
        r2 = model.score(self.X_test_score, self.y_test_score)
        
        print(f"Mean Absolute Error (MAE): {mae:.4f}")
        print(f"Root Mean Square Error (RMSE): {rmse:.4f}")
        print(f"R² Score: {r2:.4f}")
        
        self.models['score_predictor'] = model
        
        print(f"\n✅ Model trained successfully")
        
        return mae
    
    def train_risk_assessor(self):
        """Train risk assessment model (Pass/Fail)"""
        print("\n" + "="*60)
        print("TRAINING: Risk Assessment Model")
        print("="*60)
        
        # FIXED: Use multi_class='ovr' to force 2 sets of coefficients
        model = LogisticRegression(max_iter=1000, random_state=42, multi_class='ovr')
        model.fit(self.X_train_risk, self.y_train_risk)
        
        # Predictions
        predictions = model.predict(self.X_test_risk)
        
        # Calculate metrics
        accuracy = accuracy_score(self.y_test_risk, predictions)
        
        print(f"Accuracy: {accuracy:.4f}")
        
        print("\nClassification Report:")
        print(classification_report(self.y_test_risk, predictions))
        
        # Debug: Show coefficient shape
        print(f"\nCoefficient shape: {model.coef_.shape}")
        print(f"Classes: {model.classes_}")
        
        self.models['risk_assessor'] = model
        
        print(f"\n✅ Model trained successfully")
        
        return accuracy
    
    def save_models_json(self, output_dir='models'):
        """Save models in JSON format for Java Spring Boot"""
        print("\n" + "="*60)
        print("SAVING MODELS TO JSON")
        print("="*60)
        
        # Create output directory
        os.makedirs(output_dir, exist_ok=True)
        
        # Save Grade Classifier
        grade_model = self.models['grade_classifier']
        if hasattr(grade_model, 'coef_'):  # Logistic Regression
            grade_json = {
                'model_type': 'LogisticRegression',
                'features': self.feature_columns,
                'classes': grade_model.classes_.tolist(),
                'coefficients': grade_model.coef_.tolist(),
                'intercept': grade_model.intercept_.tolist()
            }
        else:  # Decision Tree
            grade_json = {
                'model_type': 'DecisionTree',
                'features': self.feature_columns,
                'classes': grade_model.classes_.tolist(),
                'feature_importances': grade_model.feature_importances_.tolist(),
                'max_depth': grade_model.max_depth
            }
        
        grade_file = os.path.join(output_dir, 'grade_classifier.json')
        with open(grade_file, 'w') as f:
            json.dump(grade_json, f, indent=2)
        print(f"✅ Saved: {grade_file}")
        
        # Save Score Predictor
        score_model = self.models['score_predictor']
        score_json = {
            'model_type': 'LinearRegression',
            'features': self.feature_columns,
            'coefficients': score_model.coef_.tolist(),
            'intercept': float(score_model.intercept_)
        }
        
        score_file = os.path.join(output_dir, 'score_predictor.json')
        with open(score_file, 'w') as f:
            json.dump(score_json, f, indent=2)
        print(f"✅ Saved: {score_file}")
        
        # Save Risk Assessor - FIXED
        risk_model = self.models['risk_assessor']
        
        # Ensure we have 2D coefficients array
        if risk_model.coef_.shape[0] == 1:
            # Only one set of coefficients, need to create the complement
            print("⚠️  Warning: Single coefficient set detected, creating complement...")
            coef_array = [
                (-risk_model.coef_[0]).tolist(),  # Negative for first class
                risk_model.coef_[0].tolist()       # Positive for second class
            ]
            intercept_array = [
                -risk_model.intercept_[0],
                risk_model.intercept_[0]
            ]
        else:
            # Already has 2 sets
            coef_array = risk_model.coef_.tolist()
            intercept_array = risk_model.intercept_.tolist()
        
        risk_json = {
            'model_type': 'LogisticRegression',
            'features': self.feature_columns,
            'classes': risk_model.classes_.tolist(),
            'coefficients': coef_array,
            'intercept': intercept_array
        }
        
        risk_file = os.path.join(output_dir, 'risk_assessor.json')
        with open(risk_file, 'w') as f:
            json.dump(risk_json, f, indent=2)
        print(f"✅ Saved: {risk_file}")
        print(f"   Coefficient sets: {len(coef_array)}")
        
        # Save metadata
        metadata = {
            'created_date': pd.Timestamp.now().strftime('%Y-%m-%d %H:%M:%S'),
            'num_training_samples': len(self.X_train_grade),
            'num_test_samples': len(self.X_test_grade),
            'feature_columns': self.feature_columns,
            'models': {
                'grade_classifier': grade_json['model_type'],
                'score_predictor': score_json['model_type'],
                'risk_assessor': risk_json['model_type']
            }
        }
        
        metadata_file = os.path.join(output_dir, 'model_metadata.json')
        with open(metadata_file, 'w') as f:
            json.dump(metadata, f, indent=2)
        print(f"✅ Saved: {metadata_file}")
        
        print(f"\n✅ All models saved to: {output_dir}/")
    
    def save_models_pickle(self, output_dir='models'):
        """Save models as pickle files (alternative format)"""
        os.makedirs(output_dir, exist_ok=True)
        
        for name, model in self.models.items():
            filename = os.path.join(output_dir, f'{name}.pkl')
            with open(filename, 'wb') as f:
                pickle.dump(model, f)
            print(f"✅ Saved pickle: {filename}")
    
    def test_predictions(self):
        """Test predictions with sample data"""
        print("\n" + "="*60)
        print("TESTING PREDICTIONS")
        print("="*60)
        
        # Get a random test sample
        sample_idx = np.random.randint(0, len(self.X_test_grade))
        sample = self.X_test_grade.iloc[sample_idx:sample_idx+1]
        
        print("\nSample Student Data:")
        for feature, value in zip(self.feature_columns, sample.values[0]):
            print(f"  {feature:25s}: {value:.2f}")
        
        # Predictions
        grade_pred = self.models['grade_classifier'].predict(sample)[0]
        score_pred = self.models['score_predictor'].predict(sample)[0]
        risk_pred = self.models['risk_assessor'].predict(sample)[0]
        
        # Actual values
        actual_grade = self.y_test_grade.iloc[sample_idx]
        actual_score = self.y_test_score.iloc[sample_idx]
        actual_risk = self.y_test_risk.iloc[sample_idx]
        
        print("\nPredictions vs Actual:")
        print(f"  Grade:      Predicted: {grade_pred}  |  Actual: {actual_grade}")
        print(f"  Score:      Predicted: {score_pred:.2f}  |  Actual: {actual_score:.2f}")
        print(f"  Pass/Fail:  Predicted: {risk_pred}  |  Actual: {actual_risk}")

def main():
    print("="*60)
    print("STUDENT PERFORMANCE ML MODEL TRAINING")
    print("="*60)
    
    # Initialize
    trainer = StudentPerformanceModels('student_data.csv')
    
    # Prepare data
    trainer.prepare_data()
    
    # Train models
    grade_acc = trainer.train_grade_classifier()
    score_mae = trainer.train_score_predictor()
    risk_acc = trainer.train_risk_assessor()
    
    # Save models
    trainer.save_models_json(output_dir='models')
    trainer.save_models_pickle(output_dir='models')
    
    # Test predictions
    trainer.test_predictions()
    
    # Summary
    print("\n" + "="*60)
    print("TRAINING SUMMARY")
    print("="*60)
    print(f"✅ Grade Classifier Accuracy:  {grade_acc:.4f} ({grade_acc*100:.2f}%)")
    print(f"✅ Score Predictor MAE:        {score_mae:.4f} points")
    print(f"✅ Risk Assessor Accuracy:     {risk_acc:.4f} ({risk_acc*100:.2f}%)")
    print("\n✅ All models trained and saved successfully!")
    print("   - JSON files: Ready for Spring Boot")
    print("   - Pickle files: Ready for Python")
    print("\nNext steps:")
    print("  1. Copy JSON files to Spring Boot project resources")
    print("  2. Build prediction service")
    print("  3. Test with REST API")

if __name__ == "__main__":
    main()