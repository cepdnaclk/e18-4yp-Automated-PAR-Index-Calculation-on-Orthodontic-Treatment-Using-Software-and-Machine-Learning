from django.http import HttpResponse, JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_POST
import os
import json
from .PARIndexHub import *
from .PARCalculator import *
from .stlData import LOWER_SEGMENT_DATASET
from .stlData import UPPER_SEGMENT_DATASET
from .stlData import BUCCAL_DATASET


@csrf_exempt
def process_coordinates(request):
    if request.method == 'POST':
        try:
            # PAR index variables
            int_LowerAnteriorSegment = 0

            # Load the JSON data
            data = json.loads(request.body)

            int_UpperAnteriorSegment = UpperLowerAnteriorSegments(json.loads(data["Upper Arch Segment"]))
            int_LowerAnteriorSegment = UpperLowerAnteriorSegments(json.loads(data["Lower Arch Segment"]))

            float_PAR_Index = PARCalculator(int_UpperAnteriorSegment, int_LowerAnteriorSegment)
            
            return HttpResponse(float_PAR_Index, status=200)
        
        except json.JSONDecodeError as e:
            return HttpResponse(f"Invalid JSON: {e}", status=400)
    return HttpResponse("Invalid request method", status=400)





@csrf_exempt
@require_POST
def process_lower_file(request):
    try:
        data = json.loads(request.body)
    except json.JSONDecodeError:
        return HttpResponse("Invalid JSON", status=400)

    if 'lower_stl' not in data:
        return HttpResponse("No file path provided", status=400)

    file_path = data['lower_stl']
    
    if not os.path.exists(file_path):
        return HttpResponse("File not found", status=400)

    print("File exists: ", file_path)
   
    return JsonResponse(LOWER_SEGMENT_DATASET)

@csrf_exempt
@require_POST
def process_upper_file(request):
    try:
        data = json.loads(request.body)
    except json.JSONDecodeError:
        return HttpResponse("Invalid JSON", status=400)

    if 'upper_stl' not in data:
        return HttpResponse("No file path provided", status=400)

    file_path = data['upper_stl']
    
    if not os.path.exists(file_path):
        return HttpResponse("File not found", status=400)

    print("File exists: ", file_path)
    
    return JsonResponse(UPPER_SEGMENT_DATASET)

@csrf_exempt
@require_POST
def process_buccal_file(request):
    try:
        data = json.loads(request.body)
    except json.JSONDecodeError:
        return HttpResponse("Invalid JSON", status=400)

    if 'buccal_stl' not in data:
        return HttpResponse("No file path provided", status=400)

    file_path = data['buccal_stl']
    
    if not os.path.exists(file_path):
        return HttpResponse("File not found", status=400)

    print("File exists: ", file_path)
    
    return JsonResponse(BUCCAL_DATASET)
