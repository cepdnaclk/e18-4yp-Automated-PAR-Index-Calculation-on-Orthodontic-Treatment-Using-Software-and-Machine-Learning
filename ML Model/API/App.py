from flask import Flask, request, jsonify
import base64
import os
import numpy as np
import tensorflow as tf
import trimesh

app = Flask(__name__)

# Define the list of names
names_lower = ['L1D', 'L1M', 'L1Mid', 'L2D', 'L2M', 'L2Mid', 'L3M', 'L3Mid', 'L4BT', 'L4PT',
         'L5BT', 'L5PT', 'L6BD', 'L6BM', 'L6PD', 'L6PM', 'L7BD', 'L7BM', 'L7PD', 'L7PM',
         'R1D', 'R1Lower', 'R1M', 'R1Mid', 'R2D', 'R2M', 'R2Mid', 'R3M', 'R3Mid', 'R4BT',
         'R4PT', 'R5BT', 'R5PT', 'R6BD', 'R6BM', 'R6PD', 'R6PM', 'R7BD', 'R7BM', 'R7PD', 'R7PM']

names_upper = ['L1D', 'L1M', 'L1Mid', 'L2D', 'L2M', 'L2Mid', 'L3M', 'L3Mid', 'L4BT', 'L4PT', 
               'L5BT', 'L5PT', 'L6BD', 'L6BM', 'L6PD', 'L6PM', 'L7BD', 'L7BM', 'L7PD', 'L7PM', 
               'R1D', 'R1M', 'R1Mid', 'R2D', 'R2M', 'R2Mid', 'R3M', 'R3Mid', 'R4BT', 'R4PT', 
               'R5BT', 'R5PT', 'R6BD', 'R6BM', 'R6PD', 'R6PM', 'R7BD', 'R7BM', 'R7PD', 'R7PM']

names_buccal = ['LCover', 'OJ_LCP', 'OJ_UCP']

@app.route("/predict/lower" , methods = ["POST"])
def predict_lower():
    # Load the trained model
    model = tf.keras.models.load_model('lower_landmark_prediction_model.h5')

    #assume lower_file is the file name in the frontend, change it necessarily
    if 'lower_stl' not in request.files:
        return "No file part", 400

    file = request.files['lower_stl']

    if file.filename == '':
        return "No selected file",400
    
    if file:
        try:
            features = process_stl(file)
        
            # Predict using the trained ML model
            prediction = model.predict(features)  # Adjust as needed for your model
        
            # Convert the prediction to the desired JSON format
            formatted_prediction = format_prediction(prediction[0], names_lower)
            
            return jsonify(formatted_prediction), 200
    
        except Exception as e:
            return jsonify({"error": str(e)}), 500

## NOT COMPLETE
@app.route("/predict/upper" , methods = ["POST"])
def predict_upper():
    # Load the trained model
    model = tf.keras.models.load_model('upper_landmark_prediction_model.h5')
    
    #assume lower_file is the file name in the frontend, change it necessarily
    if 'upper_stl' not in request.files:
        return "No file part", 400

    file = request.files['upper_stl']

    if file.filename == '':
        return "No selected file",400
    
    if file:
        try:
            features = process_stl(file)
        
            # Predict using the trained ML model
            prediction = model.predict(features)  # Adjust as needed for your model
        
            # Convert the prediction to the desired JSON format
            formatted_prediction = format_prediction(prediction[0], names_upper)
            
            return jsonify(formatted_prediction), 200
    
        except Exception as e:
            return jsonify({"error": str(e)}), 500

## NOT COMPLETE
@app.route("/predict/buccal" , methods = ["POST"])
def predict_buccal():
    # Load the trained model
    model = tf.keras.models.load_model('buccal_landmark_prediction_model.h5')
    
    #assume lower_file is the file name in the frontend, change it necessarily
    if 'buccal_stl' not in request.files:
        return "No file part", 400

    file = request.files['buccal_stl']

    if file.filename == '':
        return "No selected file",400
    
    if file:
        try:
            features = process_stl(file)
        
            # Predict using the trained ML model
            prediction = model.predict(features)  # Adjust as needed for your model
        
            # Convert the prediction to the desired JSON format
            formatted_prediction = format_prediction(prediction[0], names_buccal)
            
            return jsonify(formatted_prediction), 200
    
        except Exception as e:
            return jsonify({"error": str(e)}), 500

def process_stl(file):
    mesh = trimesh.load(file, file_type='stl')
    features = mesh.sample(10000)
    features = features.reshape(1, 10000, 3)
    return features

def format_prediction(prediction,names):
    formatted = {}
    for i, name in enumerate(names):
        formatted[name] = prediction[i*3:(i+1)*3].tolist()
    return formatted

if __name__ == '__main__':
    app.run(debug=True)