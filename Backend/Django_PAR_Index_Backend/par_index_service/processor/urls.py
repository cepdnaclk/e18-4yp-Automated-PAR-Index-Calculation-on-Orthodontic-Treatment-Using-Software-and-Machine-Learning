from django.urls import path
from .views import process_coordinates

urlpatterns = [
    path('process_coordinates/', process_coordinates, name='process_coordinates'),
]
