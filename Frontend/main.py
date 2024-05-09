import sys
import base64
import numpy as np
import json
import requests
from PyQt5.QtWidgets import QMainWindow, QApplication, QVBoxLayout, QHBoxLayout, QWidget, QPushButton, QFileDialog, QMessageBox, QComboBox
from vtk.qt.QVTKRenderWindowInteractor import QVTKRenderWindowInteractor
from vtk.util.numpy_support import vtk_to_numpy
import vtk
from commonHelper import RenderHelper  # Import the custom style
from stl import mesh

class MainWindow(QMainWindow):
    def __init__(self, parent=None):
        super(MainWindow, self).__init__(parent)

        # Main layout and widget setup
        self.mainWidget = QWidget()
        self.setCentralWidget(self.mainWidget)
        self.mainLayout = QVBoxLayout()
        self.mainWidget.setLayout(self.mainLayout)

        self.comboPanel = QHBoxLayout()
        self.mainLayout.addLayout(self.comboPanel)

        # File type dropdown setup
        self.fileTypeCombo = QComboBox()
        self.fileTypeCombo.addItems(["Buccal", "Upper anterior", "Lower anterior"])  
        self.fileTypeCombo.setStyleSheet("background-color: #2196F3; color: white; border-radius: 10px; padding: 5px;")
        self.comboPanel.addWidget(self.fileTypeCombo)

        # Panel with buttons
        self.buttonPanel = QHBoxLayout()
        self.mainLayout.addLayout(self.buttonPanel)

        # Set button panel background color to black
        self.mainWidget.setStyleSheet("background-color: black;")

        # Add buttons
        self.btn_load = QPushButton("Load STL")
        self.btn_load.clicked.connect(self.load_stl)
        self.btn_save_json = QPushButton("Save to JSON")
        self.btn_save_json.clicked.connect(self.save_to_json)
        self.btn_reset = QPushButton("Reset Markers")
        self.btn_reset.clicked.connect(self.reset_markers)
        self.btn_undo = QPushButton("Undo Marker")
        self.btn_undo.clicked.connect(self.undo_marker)
        self.btnSave = QPushButton("Save")
        self.btnSave.clicked.connect(self.save_data)

        # Styling buttons
        button_style = """
        QPushButton {
            color: white;
            background-color: qlineargradient(x1:0, y1:0, x2:1, y2:0, stop:0 #2196F3, stop:1 #0D47A1);
            border: 1px solid #0D47A1;
            border-radius: 20px;
            padding: 10px;
            margin: 5px;
            font-size: 14px;
        }
        QPushButton:hover {
            background-color: qlineargradient(x1:0, y1:0, x2:1, y2:0, stop:0 #0D47A1, stop:1 #2196F3);
        }
        QPushButton:pressed {
            background-color: #0D47A1;
        }
        """
        self.btn_load.setStyleSheet(button_style)
        self.btn_save_json.setStyleSheet(button_style)
        self.btn_reset.setStyleSheet(button_style)
        self.btn_undo.setStyleSheet(button_style)
        self.btnSave.setStyleSheet(button_style)

        # Add buttons to panel
        self.buttonPanel.addWidget(self.btn_load)
        self.buttonPanel.addWidget(self.btn_save_json)
        self.buttonPanel.addWidget(self.btn_reset)
        self.buttonPanel.addWidget(self.btn_undo)
        self.buttonPanel.addWidget(self.btnSave)

        # VTK Widget setup
        self.vtkWidget = QVTKRenderWindowInteractor(self.mainWidget)
        self.mainLayout.addWidget(self.vtkWidget)
        self.renderer = vtk.vtkRenderer()
        self.vtkWidget.GetRenderWindow().AddRenderer(self.renderer)

        # Data structure to store marker positions and actors
        self.markers = []
        self.points = []

    def load_stl(self):
        file_path = QFileDialog.getOpenFileName(self, "Select STL file", "", "STL Files (*.stl)")[0]
        if file_path:
            with open(file_path, "rb") as file:
                # Encode the file content in base64
                self.files_data = base64.b64encode(file.read()).decode('utf-8')
            
            reader = vtk.vtkSTLReader()
            reader.SetFileName(file_path)
            reader.Update()

            your_mesh = mesh.Mesh.from_file(file_path)

            self.renderer.RemoveAllViewProps()

            mapper = vtk.vtkPolyDataMapper()
            mapper.SetInputConnection(reader.GetOutputPort())

            actor = vtk.vtkActor()
            actor.SetMapper(mapper)
            self.renderer.AddActor(actor)
            self.renderer.ResetCamera()

            self.center = np.mean(vtk_to_numpy(reader.GetOutput().GetPoints().GetData()), axis=0)

            # Extract the vertices
            points = np.vstack(np.array([your_mesh.v0, your_mesh.v1, your_mesh.v2]))

            # Calculate the mean of each column (dimension)
            means = np.mean(points, axis=0)

            # Subtract the mean from each dimension to center the data
            centered_points = points - means

            # Calculate the covariance matrix
            covariance_matrix = np.cov(centered_points, rowvar=False)

            # Perform PCA by finding eigenvectors and eigenvalues
            eigenvalues, eigenvectors = np.linalg.eig(covariance_matrix)

            #print("Eigenvalues:\n", eigenvalues)
            #print("Eigenvectors:\n", eigenvectors)

            # Indexes of the eigenvalues sorted from highest to lowest
            sorted_indexes = np.argsort(eigenvalues)[::-1]

            # Sorted eigenvectors corresponding to the sorted eigenvalues
            principal_eigenvectors = eigenvectors[:, sorted_indexes]

            # If you want just the top three or fewer principal components
            top_principal_eigenvectors = principal_eigenvectors[:, :3]

            # Eigenvectors and their corresponding eigenvalues
            eigenvectors = top_principal_eigenvectors  # Use your computed eigenvectors
            eigenvalues = eigenvalues[sorted_indexes[:3]]  # Sorted eigenvalues

            # Colors for the eigenvectors
            colors = [(1, 0, 0), (0, 1, 0), (0, 0, 1)]  # Red, Green, Blue

            # Plot each eigenvector as a line originating from the center of the mesh
            for i, vec in enumerate(eigenvectors.T):
                # Create a line from the center in the direction of the eigenvector
                lineSource = vtk.vtkLineSource()
                lineSource.SetPoint1(self.center)
                lineSource.SetPoint2(self.center + vec * 10)  # Adjusted scaling factor for visibility

                # Mapper for the line
                lineMapper = vtk.vtkPolyDataMapper()
                lineMapper.SetInputConnection(lineSource.GetOutputPort())

                # Actor for the line
                lineActor = vtk.vtkActor()
                lineActor.SetMapper(lineMapper)
                lineActor.GetProperty().SetColor(colors[i])  # Set color from the colors array
                lineActor.GetProperty().SetLineWidth(2)  # Thicker line for better visibility

                # Add the actor to the renderer
                self.renderer.AddActor(lineActor)

            self.interactor = self.vtkWidget.GetRenderWindow().GetInteractor()
            style = RenderHelper(self.renderer, self.center, self.vtkWidget.GetRenderWindow(), self.markers, self.points)
            self.interactor.SetInteractorStyle(style)
            self.interactor.Initialize()
            self.vtkWidget.GetRenderWindow().Render()
        
        # Save STL file in backend
        self.stl_id = self.save_file()

    def save_to_json(self):
        if not self.points:
            print("No points to save.")
            return

        json_data = {
            "points": [
                {
                    "point_name": point["name"], 
                    "coordinates": 
                        (point["x"], point["y"], point["z"])} for point in self.points
                    ]
        }
        file_path = QFileDialog.getSaveFileName(self, "Save File", "", "JSON Files (*.json)")[0]
        if file_path:
            with open(file_path, 'w') as outfile:
                json.dump(json_data, outfile, indent=4)
            print("Data saved to", file_path)


    def undo_marker(self):
        if self.markers:
            last_marker = self.markers.pop()  # Remove the last marker
            if 'actor' in last_marker:
                self.renderer.RemoveActor(last_marker['actor'])  # Remove the sphere actor
            if 'textActor' in last_marker:
                self.renderer.RemoveActor(last_marker['textActor'])  # Remove the text actor
            self.vtkWidget.GetRenderWindow().Render()
            print("Last marker has been undone.")

    def reset_markers(self):
        # Remove all marker actors and their text labels from the renderer
        while self.markers:
            marker = self.markers.pop()
            if 'actor' in marker:
                self.renderer.RemoveActor(marker['actor'])
            if 'textActor' in marker:
                self.renderer.RemoveActor(marker['textActor'])
        self.vtkWidget.GetRenderWindow().Render()
        print("All markers have been reset.")
    
    def save_file(self):
        try:
            url = 'http://localhost:8080/api/stlfile'
            data = {
                "file_type": self.fileTypeCombo.currentText(),
                "file": self.files_data       
            }
            
            # Send POST request with JSON data
            response = requests.post(url, json=data)

            if response.status_code == 201:
                return response.json.get('stl_id')        
            else:
                QMessageBox.warning(self, "Error", "Failed to save file.")

        except Exception as e:
            QMessageBox.critical(self, "Error", "An error occurred when saving the file: " + str(e))

    def save_data(self):
        try:
            url = 'http://localhost:8080/api/point/list'
            data = {
                "stl_id": self.stl_id,
                "points": [
                    {
                        "point_name": point["name"],
                        "coordinates": f"{point['x']},{point['y']},{point['z']}"
                    } for point in self.points
                ]
            }

            # Send POST request with JSON data
            response = requests.post(url, json=data)
            if response.status_code == 201:
                QMessageBox.information(self, "Success", "The data was saved successfully!")
            else:
                QMessageBox.warning(self, "Error", "Failed to save data.")
                print(response.text, "/n" , response)
        except Exception as e:
            QMessageBox.critical(self, "Error", "An error occurred: " + str(e))

if __name__ == "__main__":
    app = QApplication(sys.argv)
    window = MainWindow()
    window.show()
    sys.exit(app.exec_())
