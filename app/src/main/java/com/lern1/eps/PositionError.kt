package com.lern1.eps
import kotlin.math.*
class PositionError {

    fun calculatePositionError(groundtruth: List<Waypoint>, recorded: List<Waypoint>) {
        // Iteriere über die Wegpunkte und berechne den Fehler für jeden Zeitpunkt
        for (gtPoint in groundtruth) {
            val closestRecordedPoint = findClosestRecordedPoint(gtPoint, recorded)
            val distanceError = calculateDistance(gtPoint, closestRecordedPoint)
            // Speichere oder analysiere den Fehler
            println("Groundtruth: $gtPoint, Closest Recorded: $closestRecordedPoint, Error: $distanceError km")
        }
    }

    fun findClosestRecordedPoint(gtPoint: Waypoint, recorded: List<Waypoint>): Waypoint {
        var closestPoint = recorded.firstOrNull()
        var closestDistance = calculateDistance(gtPoint, closestPoint ?: return gtPoint)

        for (recordedPoint in recorded) {
            val distance = calculateDistance(gtPoint, recordedPoint)
            if (distance < closestDistance) {
                closestDistance = distance
                closestPoint = recordedPoint
            }
        }

        return closestPoint ?: gtPoint
    }

    fun calculateDistance(point1: Waypoint, point2: Waypoint): Double {
        val R = 6371 // Radius der Erde in Kilometern

        val lat1 = Math.toRadians(point1.latitude)
        val lon1 = Math.toRadians(point1.longitude)
        val lat2 = Math.toRadians(point2.latitude)
        val lon2 = Math.toRadians(point2.longitude)

        val dlat = lat2 - lat1
        val dlon = lon2 - lon1

        val a = sin(dlat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dlon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c
    }

}