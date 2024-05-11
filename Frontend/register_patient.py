import sys
from PyQt5.QtWidgets import (QApplication, QWidget, QVBoxLayout, QHBoxLayout,
                             QPushButton, QLineEdit, QLabel, QRadioButton,
                             QGroupBox,  QMessageBox, QMainWindow, QFileDialog)
from PyQt5.QtCore import Qt
from PyQt5.QtGui import QPixmap, QIcon
import requests
import base64
import os

class FileDisplayWidget(QWidget):
    def __init__(self):
        super().__init__()
        layout = QHBoxLayout()
        
        # self.icon_label = QLabel()
        # self.icon_label.setFixedSize(50, 50)  # Set fixed size for the icon
        
        self.file_name_label = QLabel("No file selected")
        self.file_path = None

        #layout.addWidget(self.icon_label)
        layout.addWidget(self.file_name_label)
        layout.addStretch()
        
        self.setLayout(layout)
    
    def set_file(self, file_path):
        file_name = os.path.basename(file_path)
        file_extension = os.path.splitext(file_path)[1]
        if(file_extension != '.stl'):
            QMessageBox.critical(self, "Error", "Incorrect File Type")
        else:
            self.file_name_label.setText(file_name)
            self.file_path = file_path
            # Set the file icon (this assumes you have a file icon image in the working directory)
            #icon_pixmap = QPixmap('file_icon.png').scaled(self.icon_label.size(), Qt.KeepAspectRatio, Qt.SmoothTransformation)
            #self.icon_label.setPixmap(icon_pixmap)
            #self.titleLabel.setText(file_path)

class RegisterWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        
        self.setWindowTitle("Register Patient")
        self.setGeometry(100, 100, 800, 600)
        
        # Create the main widget
        self.main_widget = QWidget()
        self.main_layout = QVBoxLayout()
        self.setCentralWidget(self.main_widget)
        
        self.patient_layout = QHBoxLayout()
        self.patient_input = QLineEdit()
        self.patient_input.setStyleSheet("padding: 10px; font-size: 16px; border-radius: 20px;")
        self.patient_input.setPlaceholderText("Patient name")
        self.patient_layout.addWidget(self.patient_input)
        
        self.treatment_type_group = QGroupBox("Treatment Type")
        self.treatment_type_layout = QHBoxLayout()
        self.pre_treatment_radio = QRadioButton('Pre Treatment')
        self.post_treatment_radio = QRadioButton('Post Treatment')
        self.treatment_type_layout.addWidget(self.pre_treatment_radio)
        self.treatment_type_layout.addWidget(self.post_treatment_radio)
        self.treatment_type_group.setLayout(self.treatment_type_layout)
        
        files_layout = QHBoxLayout()
        # File Upload Groups
        opposing_group = QGroupBox('Opposing')
        buccal_group = QGroupBox('Buccal')
        prep_group = QGroupBox('Prep')

        self.opposing_file_display = FileDisplayWidget()
        self.buccal_file_display = FileDisplayWidget()
        self.prep_file_display = FileDisplayWidget()

        button_style = """
        QPushButton{
            background-color: qlineargradient(x1:0, y1:0, x2:1, y2:0, stop:0 #2196F3, stop:1 #0D47A1);
            border: 2px solid #0D47A1;
            border-radius: 20px;
            padding: 10px;
            margin: 5px;
            font-size: 14px;
            color: white;
        }

        QPushButton:hover{
            background-color: qlineargradient(x1:0, y1:0, x2:1, y2:0, stop:0 #0D47A1, stop:1 #2196F3);
        }

        QPushButton:pressed{
            background-color: qlineargradient(x1:0, y1:0, x2:1, y2:0, stop:0 #2196F3, stop:1 #0D47A1);
        }
        """

        for group, display_widget in zip([opposing_group, buccal_group, prep_group],
                                         [self.opposing_file_display, self.buccal_file_display, self.prep_file_display]):
            button = QPushButton('Browse')
            button.setStyleSheet(button_style)
            button.clicked.connect(lambda _, b=button, d=display_widget: self.browse_file(b, d))

            layout = QVBoxLayout()
            layout.addWidget(display_widget)
            layout.addWidget(button)
            group.setLayout(layout)
            files_layout.addWidget(group)
        
        self.save_button = QPushButton("Save")
        self.save_button.setStyleSheet(button_style)     
        self.save_button.clicked.connect(self.register_patient)

        self.main_layout.addLayout(self.patient_layout)
        self.main_layout.addWidget(self.treatment_type_group)
        self.main_layout.addLayout(files_layout)
        self.main_layout.addWidget(self.save_button)
        self.main_widget.setLayout(self.main_layout)
        
    def register_patient(self):
        # Collect data
        patient_data = {
            'patient_name': self.patient_input.text(),
            'treatment_status': 'Pre' if self.pre_treatment_radio.isChecked() else 'Post',
        }

        self.files_data = {}
        for label, widget in zip(['opposing_file', 'buccal_file', 'prep_file'],
                             [self.opposing_file_display, self.buccal_file_display, self.prep_file_display]):
            if widget.file_path:
                with open(widget.file_path, "rb") as file:
                    # Encode the file content in base64
                    self.files_data[label] = base64.b64encode(file.read()).decode('utf-8')
        
        patient_data.update(self.files_data)

        if not all(patient_data.values()):
            QMessageBox.critical(self, "Error", "All fields must be filled.")
        else:
            # Here you would typically send this data to your backend server for storage
            url = 'http://localhost:8080/api/patient/register'
            try:
                response = requests.post(url, data=patient_data)
                if response.status_code == 200:
                    QMessageBox.information(self, "Success", "Data submitted successfully.")
                else:
                    QMessageBox.critical(self, "Error", "Failed to submit data. Server responded with error.")
            except Exception as e:
                QMessageBox.critical(self, "Error", f"An error occurred: {str(e)}")
    
    def browse_file(self,button, display_widget):
        file_path, _ = QFileDialog.getOpenFileName(self, "Select File")
        if file_path:  # if a file was selected
            display_widget.set_file(file_path)
    