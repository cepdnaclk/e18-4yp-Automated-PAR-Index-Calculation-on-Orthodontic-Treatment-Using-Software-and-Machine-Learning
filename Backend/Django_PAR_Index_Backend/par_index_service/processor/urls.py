from django.urls import path
from .views import process_coordinates, process_lower_file, process_upper_file, process_buccal_file

urlpatterns = [
    path('process_coordinates', process_coordinates, name='process_coordinates'),
    path('predict/upper', process_upper_file, name='process_upper_file'),
    path('predict/lower', process_lower_file, name='process_lower_file'),
    path('predict/buccal', process_buccal_file, name='process_buccal_file')
]
