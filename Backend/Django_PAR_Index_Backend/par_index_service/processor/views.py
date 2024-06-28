from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
import json
import numpy as np

@csrf_exempt  # Disables CSRF validation for this view
def process_coordinates(request):
    if request.method == 'POST':
        data = json.loads(request.body)
        segments = data.get("segments", {})

        for segment_name, points in segments.items():
            for point in points:
                coordinates = np.array(point["coordinates"].split(','), dtype=float)
                # Perform your arithmetic operations
                new_coordinates = coordinates + np.array([10, 2, -5])  # Example operation
                point["coordinates"] = ','.join(map(str, new_coordinates))
        
        return JsonResponse(segments)
    return JsonResponse({"error": "Invalid request method"}, status=400)
