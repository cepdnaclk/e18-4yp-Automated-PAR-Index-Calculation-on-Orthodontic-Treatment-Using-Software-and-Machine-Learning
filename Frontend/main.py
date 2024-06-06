import sys
from PyQt5.QtWidgets import QMainWindow, QApplication, QVBoxLayout, QHBoxLayout, QWidget, QPushButton, QComboBox, QLabel
from vtk.qt.QVTKRenderWindowInteractor import QVTKRenderWindowInteractor
import vtk
from button_functions import load_stl, save_to_json, undo_marker, reset_markers, save_data
from register_patient import RegisterWindow
from disclaimers import (UPPER_ANTERIOR_SEGMENT, LOWER_ANTERIOR_SEGMENT, BUCCAL_SEGMENT)

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
            ["Upper Arch Segment", 
             "Lower Arch Segment", 
             "Buccal Segment"
            ])
        self.fileType = "Upper Arch Segment"  # Default value

        self.fileTypeComboBox.currentIndexChanged.connect(self.update_file_type)

        self.measurement = "undefined"
        
        self.btn_save_json = QPushButton("Save to JSON")
        self.btn_reset = QPushButton("Reset Markers")
        self.btn_undo = QPushButton("Undo Marker")
        self.btnSave = QPushButton("Save")

        button_style = """
        QPushButton, QComboBox, QLabel {
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
            subcontrol-origin: padding;
            subcontrol-position: top right;
            width: 15px;
            border-left-width: 1px;
            border-left-color: darkgray;
            border-left-style: solid;
            border-top-right-radius: 10px;
            border-bottom-right-radius: 10px;
        }

        QComboBox QAbstractItemView {
            background-color: #ADD8E6; /* Light blue background */
            color: black; /* Ensuring text is visible against light background */
            selection-background-color: #5599FF; /* Different color for selection for better contrast */
        }
        """

        uniform_width = 25
        for btn in [self.btn_register,self.label_filetype,self.fileTypeComboBox,self.btn_load, self.btn_save_json, self.btn_reset, self.btn_undo, self.btnSave]:
            if (btn == self.label_filetype):
                btn.setFixedHeight(uniform_width)
                btn.setStyleSheet("color: white; font-size: 14px; margin: 5px; border: 2px;")
            else:
                btn.setStyleSheet(button_style)
            self.buttonPanel.addWidget(btn)

        self.buttonPanel.addSpacing(2)
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

    def update_disclaimer_text(self, new_text):
        if hasattr(self, 'text_actor'):
            disclaimer_text = {
            "Upper Arch Segment": UPPER_ANTERIOR_SEGMENT,
            "Lower Arch Segment": LOWER_ANTERIOR_SEGMENT,
            "Buccal Segment": BUCCAL_SEGMENT,
            }.get(new_text, "No disclaimer available for this type.")

            self.text_actor.SetInput(disclaimer_text)
            self.text_actor.GetTextProperty().SetFontSize(15) 
            self.vtkWidget.GetRenderWindow().Render()  # Rerender to update the display
    
    def update_file_type(self, index):
        # This method is called whenever the selected index in the combo box changes.
        self.fileType = self.fileTypeComboBox.currentText()
        self.update_disclaimer_text(self.fileType)
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
