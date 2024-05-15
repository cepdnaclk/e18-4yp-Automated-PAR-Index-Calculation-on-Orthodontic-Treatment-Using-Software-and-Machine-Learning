import json
import base64
import tempfile
import numpy as np
import requests
from PyQt5.QtWidgets import QMessageBox, QFileDialog
from vtk.util.numpy_support import vtk_to_numpy
import vtk
from stl import mesh
from commonHelper import RenderHelper  # Ensure this is properly imported

def load_stl(self):
    # file_path = QFileDialog.getOpenFileName(self, "Select STL file", "", "STL Files (*.stl)")[0]
    # if file_path:
    #     with open(file_path, "rb") as file:
    #             # Encode the file content in base64
    #             self.files_data = base64.b64encode(file.read()).decode('utf-8')
    try:
        if self.fileType == "Upper Anterior Segment":
            base64_stl_data = self.file_data['prep_file']
        elif self.fileType == "Lower Anterior Segment":
            base64_stl_data = self.file_data['opposing_file']
        elif self.fileType == "Buccal Segment":
            base64_stl_data = self.file_data['buccal_file']
    except Exception as e:
        QMessageBox.warning(self, "Warning", "No file data to load. Register the patient!")
        return

    decoded_stl_data = base64.b64decode(base64_stl_data)

    with tempfile.NamedTemporaryFile(delete=False, suffix=".stl") as temp_file:
        temp_file.write(decoded_stl_data)
        temp_file_path = temp_file.name

        reader = vtk.vtkSTLReader()
        reader.SetFileName(temp_file_path)
        reader.Update()

        your_mesh = mesh.Mesh.from_file(temp_file_path)

        self.renderer.RemoveAllViewProps()

        mapper = vtk.vtkPolyDataMapper()
        mapper.SetInputConnection(reader.GetOutputPort())

        actor = vtk.vtkActor()
        actor.SetMapper(mapper)
        self.renderer.AddActor(actor)
        self.renderer.ResetCamera()

        self.center = np.mean(vtk_to_numpy(reader.GetOutput().GetPoints().GetData()), axis=0)

        points = np.vstack(np.array([your_mesh.v0, your_mesh.v1, your_mesh.v2]))
        means = np.mean(points, axis=0)
        centered_points = points - means
        covariance_matrix = np.cov(centered_points, rowvar=False)
        eigenvalues, eigenvectors = np.linalg.eig(covariance_matrix)

        sorted_indexes = np.argsort(eigenvalues)[::-1]
        principal_eigenvectors = eigenvectors[:, sorted_indexes]
        top_principal_eigenvectors = principal_eigenvectors[:, :3]
        eigenvectors = top_principal_eigenvectors
        eigenvalues = eigenvalues[sorted_indexes[:3]]

        colors = [(1, 0, 0), (0, 1, 0), (0, 0, 1)]

        for i, vec in enumerate(eigenvectors.T):
            lineSource = vtk.vtkLineSource()
            lineSource.SetPoint1(self.center)
            lineSource.SetPoint2(self.center + vec * 10)

            lineMapper = vtk.vtkPolyDataMapper()
            lineMapper.SetInputConnection(lineSource.GetOutputPort())

            lineActor = vtk.vtkActor()
            lineActor.SetMapper(lineMapper)
            lineActor.GetProperty().SetColor(colors[i])
            lineActor.GetProperty().SetLineWidth(2)

            self.renderer.AddActor(lineActor)
        
        # Initialize text actor here and store as a class attribute
        self.text_actor = vtk.vtkTextActor()
        self.text_actor.GetTextProperty().SetColor(0, 1, 0)  # Green color
        self.text_actor.GetTextProperty().SetFontSize(20)
        self.text_actor.SetPosition(20, 30)
        self.renderer.AddActor(self.text_actor)

        self.update_disclaimer_text(self.measurement)  # Initial text update

        self.interactor = self.vtkWidget.GetRenderWindow().GetInteractor()
        style = RenderHelper(self.renderer, self.center, self.vtkWidget.GetRenderWindow(), self.markers, self.points)
        self.interactor.SetInteractorStyle(style)
        self.interactor.Initialize()
        self.vtkWidget.GetRenderWindow().Render()

def save_to_json(self):
    if not self.points:
        QMessageBox.warning(self, "Warning", "No Data To Save.")
        print("No points to save.")
        return

    json_data = {
        "measurement_type": self.measurement,
        "points": [{"point_name": point["name"], "coordinates": (point["x"], point["y"], point["z"])} for point in self.points]
    }
    file_path = QFileDialog.getSaveFileName(self, "Save File", "", "JSON Files (*.json)")[0]
    if file_path:
        with open(file_path, 'w') as outfile:
            json.dump(json_data, outfile, indent=4)
        print("Data saved to", file_path)

def undo_marker(self):
    if self.markers:
        last_marker = self.markers.pop()
        self.points.pop()
        if 'actor' in last_marker:
            self.renderer.RemoveActor(last_marker['actor'])
        if 'textActor' in last_marker:
            self.renderer.RemoveActor(last_marker['textActor'])
        self.vtkWidget.GetRenderWindow().Render()
        print("Last marker has been undone.")

def reset_markers(self):
    while self.markers:
        marker = self.markers.pop()
        self.points.pop()
        if 'actor' in marker:
            self.renderer.RemoveActor(marker['actor'])
        if 'textActor' in marker:
            self.renderer.RemoveActor(marker['textActor'])
    self.vtkWidget.GetRenderWindow().Render()
    print("All markers have been reset.")

def save_data(self):
    try:
        url = 'http://3.6.62.207:8080/api/point/list'
        data = {
            "file_type" : self.fileType,
            "measurement_type": self.measurement,
            "points": [{"point_name": point["name"], "coordinates": f"{point['x']},{point['y']},{point['z']}"} for point in self.points]
        }
    
        response = requests.post(url, json=data)
        if response.status_code == 201:
            QMessageBox.information(self, "Success", "The data was saved successfully!")
        else:
            QMessageBox.warning(self, "Error", "Failed to save data.")
            print(response.text, "\n", response)
    except Exception as e:
        QMessageBox.critical(self, "Error", "An error occurred: " + str(e))


