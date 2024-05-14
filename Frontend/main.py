import sys
from PyQt5.QtWidgets import QMainWindow, QApplication, QVBoxLayout, QHBoxLayout, QWidget, QPushButton, QComboBox, QLabel
from vtk.qt.QVTKRenderWindowInteractor import QVTKRenderWindowInteractor
import vtk
from button_functions import load_stl, save_to_json, undo_marker, reset_markers, save_data
from register_patient import RegisterWindow


class MainWindow(QMainWindow):
    def __init__(self, parent=None):
        super(MainWindow, self).__init__(parent)
        self.mainWidget = QWidget()
        self.setCentralWidget(self.mainWidget)
        self.mainLayout = QHBoxLayout()
        self.mainWidget.setLayout(self.mainLayout)

        self.buttonPanel = QVBoxLayout()
        self.mainLayout.addLayout(self.buttonPanel)
        self.mainWidget.setStyleSheet("background-color: black;")

        self.btn_register = QPushButton("Register Patient")

        self.btn_load = QPushButton("Load STL")

        self.label_filetype = QLabel('Select the file type:')
        self.fileTypeComboBox = QComboBox()
        self.fileTypeComboBox.addItems(
            ["Upper Anterior Segment", 
             "Lower Anterior Segment", 
             "Buccal Segment"
            ])
        self.fileType = "Upper Anterior Segment"  # Default value

        self.fileTypeComboBox.currentIndexChanged.connect(self.update_file_type)

        self.label_measurementtype = QLabel('Select the measurement type:')
        self.measurementTypeComboBox = QComboBox()
        self.measurementTypeComboBox.addItems(
            ["Upper Anterior Segment Alignment", 
             "Lower Anterior Segment Alignment", 
             "Overjet",
             "Overbite",
             "Open Bite",
             "Centre Line Displacement"])
        self.measurement = "Upper Anterior Segment Alignment"  # Default value

        self.measurementTypeComboBox.currentIndexChanged.connect(self.update_measurement_type)
        
        self.btn_save_json = QPushButton("Save to JSON")
        self.btn_reset = QPushButton("Reset Markers")
        self.btn_undo = QPushButton("Undo Marker")
        self.btnSave = QPushButton("Save")

        button_style = """
        QPushButton, QComboBox {
            background-color: qlineargradient(x1:0, y1:0, x2:1, y2:0, stop:0 #2196F3, stop:1 #0D47A1);
            border: 2px solid #0D47A1;
            border-radius: 20px;
            padding: 10px;
            margin: 5px;
            font-size: 14px;
            color: white;
        }

        QPushButton:hover, QComboBox:hover {
            background-color: qlineargradient(x1:0, y1:0, x2:1, y2:0, stop:0 #0D47A1, stop:1 #2196F3);
        }

        QPushButton:pressed, QComboBox:pressed {
            background-color: qlineargradient(x1:0, y1:0, x2:1, y2:0, stop:0 #2196F3, stop:1 #0D47A1);
        }

        QComboBox {
            color: white;
            background-color: qlineargradient(x1:0, y1:0, x2:1, y2:0, stop:0 #2196F3, stop:1 #0D47A1);
        }

        QComboBox::drop-down {
            background-color: qlineargradient(x1:0, y1:0, x2:1, y2:0, stop:0 #2196F3, stop:1 #0D47A1);
            subcontrol-origin: padding;
            subcontrol-position: top right;
            width: 15px;
            border-left-width: 1px;
            border-left-color: darkgray;
            border-left-style: solid;
            border-top-right-radius: 10px;
            border-bottom-right-radius: 10px;
        }
        """

        for btn in [self.btn_register,self.label_filetype,self.fileTypeComboBox,self.btn_load,self.label_measurementtype,self.measurementTypeComboBox, self.btn_save_json, self.btn_reset, self.btn_undo, self.btnSave]:
            if (btn == self.label_filetype or btn == self.label_measurementtype):
                btn.setStyleSheet("color: white; font-size: 14px; margin: 5px;")
            else:
                btn.setStyleSheet(button_style)
            self.buttonPanel.addWidget(btn)

        self.buttonPanel.addSpacing(10)
        self.btn_register.clicked.connect(self.open_register_window)
        self.btn_load.clicked.connect(lambda: load_stl(self))
        self.btn_save_json.clicked.connect(lambda: save_to_json(self))
        self.btn_reset.clicked.connect(lambda: reset_markers(self))
        self.btn_undo.clicked.connect(lambda: undo_marker(self))
        self.btnSave.clicked.connect(lambda: save_data(self))

        self.vtkWidget = QVTKRenderWindowInteractor(self.mainWidget)
        self.mainLayout.addWidget(self.vtkWidget)
        self.renderer = vtk.vtkRenderer()
        self.vtkWidget.GetRenderWindow().AddRenderer(self.renderer)
        self.markers = []
        self.points = []

    def update_measurement_type(self, index):
        # This method is called whenever the selected index in the combo box changes.
        self.measurement = self.measurementTypeComboBox.currentText()
        print("Selected Measurement Type:", self.measurement)
    
    def update_file_type(self, index):
        # This method is called whenever the selected index in the combo box changes.
        self.fileType = self.fileTypeComboBox.currentText()
        print("Selected File Type:", self.fileType)
    
    def open_register_window(self):
        # This function will be called when btn_register is clicked
        self.register_window = RegisterWindow()  # Create an instance of RegisterWindow
        self.register_window.data_ready.connect(self.handle_data_from_register)  # Connect the data_ready signal to handle_data_from_register
        self.register_window.show()  # Show the RegisterWindow
    
    def handle_data_from_register(self, data):
        self.file_data = data

if __name__ == "__main__":
    app = QApplication(sys.argv)
    window = MainWindow()
    window.show()
    sys.exit(app.exec_())
