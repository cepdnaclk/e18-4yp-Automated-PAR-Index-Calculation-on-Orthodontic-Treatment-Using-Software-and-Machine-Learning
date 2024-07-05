import math

def update_score(value):
    if 0 <= value <= 1:
        return 0
    elif 1.1 <= value <= 2:
        return 1
    elif 2.1 <= value <= 4:
        return 2
    elif 4.1 <= value <= 8:
        return 3
    elif value > 8:
        return 4
    else:
        raise ValueError("Invalid value: Value must be non-negative")

def calculate_distance(point1, point2):
    return round(math.sqrt((point2[0] - point1[0])**2 + (point2[1] - point1[1])**2 + (point2[2] - point1[2])**2), 1)

def UpperLowerAnteriorSegments(dict_points):
    if len(dict_points) == 0:
        return 0
    
    point_pairs = [
        ("R3M", "R2D"),
        ("R2M", "R1D"),
        ("R1M", "L1M"),
        ("L1D", "L2M"),
        ("L2D", "L3M")
    ]
    
    distances = []
    for point1, point2 in point_pairs:
        p1 = dict_points.get(point1)
        p2 = dict_points.get(point2)
        if p1 and p2:
            distance = calculate_distance(p1, p2)
        else:
            distance = 0
        distances.append(distance)
        # print(f"{point1}-{point2} distance: {distance}")

    score = sum(update_score(distance) for distance in distances)
    
    return score
