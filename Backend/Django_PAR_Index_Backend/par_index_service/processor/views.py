from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
import json
from .PARIndexHub import *
from .PARCalculator import *

@csrf_exempt
def process_coordinates(request):
    if request.method == 'POST':
        try:
            # PAR index variables
            int_LowerAnteriorSegment = 0

            # Load the JSON data
            data = json.loads(request.body)

            int_UpperAnteriorSegment = UpperLowerAnteriorSegments(data["Upper Arch Segment"])
            int_LowerAnteriorSegment = UpperLowerAnteriorSegments(data["Lower Arch Segment"])

            float_PAR_Index = PARCalculator(int_UpperAnteriorSegment, int_LowerAnteriorSegment)
            
            # return HttpResponse(f"{int_UpperAnteriorSegment}, {int_LowerAnteriorSegment}", status=200)
            return HttpResponse(float_PAR_Index, status=200)
        
        except json.JSONDecodeError as e:
            return HttpResponse(f"Invalid JSON: {e}", status=400)
    return HttpResponse("Invalid request method", status=400)
