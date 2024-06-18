import vtk
import numpy as np
from PyQt5.QtWidgets import QInputDialog

class RenderHelper(vtk.vtkInteractorStyleTrackballCamera):
    def __init__(self, renderer, center, renderWindow, markers, points):
        super(RenderHelper, self).__init__()
        self.renderer = renderer
        self.center = center
        self.renderWindow = renderWindow
        self.markers = markers
        self.points = points
        self.AddObserver("LeftButtonPressEvent", self.left_button_press)
        self.input_active = False  # Flag to track when input is active

    def left_button_press(self, obj, event):
        if not self.input_active:  # Only process clicks if not currently handling input
            clickPos = self.GetInteractor().GetEventPosition()
            picker = vtk.vtkPropPicker()
            picker.Pick(clickPos[0], clickPos[1], 0, self.renderer)
            pickedPosition = picker.GetPickPosition()

            if picker.GetActor() is not None:
                self.input_active = True  # Set flag to indicate input handling
                self.add_marker(pickedPosition)
            else:
                self.OnLeftButtonDown()  # Continue camera manipulation if no actor was picked

    def add_marker(self, position):
        # Create and place a sphere (marker)
        sphereSource = vtk.vtkSphereSource()
        sphereSource.SetCenter(position)
        sphereSource.SetRadius(0.1)
        sphereSource.Update()

        mapper = vtk.vtkPolyDataMapper()
        mapper.SetInputConnection(sphereSource.GetOutputPort())

        sphereActor = vtk.vtkActor()
        sphereActor.SetMapper(mapper)
        sphereActor.GetProperty().SetColor(1, 0, 0)  # Red color for the marker

        self.renderer.AddActor(sphereActor)

        label, ok = QInputDialog.getText(None, "Input Marker Label", "Enter label for the marker:")
        if ok and label:
            # Create and display a label that moves with the marker
            textActor = vtk.vtkBillboardTextActor3D()
            textActor.SetInput(label)
            textProp = textActor.GetTextProperty()
            textProp.SetFontSize(18)
            textProp.SetColor(0, 1, 0)
            textProp.SetBold(True)
            textActor.SetPosition(position)
            self.renderer.AddActor(textActor)

            self.renderWindow.Render()

            # Save position, label, and actors to markers list
            self.markers.append({
                "name": label,
                "x": position[0], 
                "y": position[1], 
                "z": position[2], 
                "actor": sphereActor, 
                "textActor": textActor
            })

            # Save position, label, and actors to point list
            self.points.append({
                "name": label,
                "x": position[0], 
                "y": position[1], 
                "z": position[2]
            })

        else: 
            self.renderer.RemoveActor(sphereActor)

        self.input_active = False  # Reset flag after handling input
        
        

