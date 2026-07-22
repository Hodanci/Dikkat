package com.example.dikkat

import android.graphics.Color

data class RoadType(
    val name: String,
    val speedLimit: Int,
    val color: Int
)

object SpeedLimitManager {
    
    enum class RoadCategory {
        CITY,           // Şehir içi: 50 km/h
        TWO_WAY,        // Çift yönlü yol: 80 km/h
        DIVIDED,        // Bölünmüş yol: 90 km/h
        TOLL_HIGHWAY    // Paralı otoyol: 100 km/h
    }

    fun getRoadTypes(): Map<RoadCategory, RoadType> {
        return mapOf(
            RoadCategory.CITY to RoadType("Şehir İçi", 50, Color.GREEN),
            RoadCategory.TWO_WAY to RoadType("Çift Yönlü Yol", 80, Color.YELLOW),
            RoadCategory.DIVIDED to RoadType("Bölünmüş Yol", 90, Color.parseColor("#FFA500")),
            RoadCategory.TOLL_HIGHWAY to RoadType("Paralı Otoyol", 100, Color.RED)
        )
    }

    fun getCurrentSpeedLimit(category: RoadCategory): Int {
        return getRoadTypes()[category]?.speedLimit ?: 50
    }

    fun getSpeedLimitColor(category: RoadCategory): Int {
        return getRoadTypes()[category]?.color ?: Color.GREEN
    }

    fun isSpeedExceeded(currentSpeed: Float, category: RoadCategory): Boolean {
        return currentSpeed > getCurrentSpeedLimit(category)
    }

    fun getExceededSpeedPercentage(currentSpeed: Float, category: RoadCategory): Int {
        val limit = getCurrentSpeedLimit(category)
        return if (currentSpeed > limit) {
            ((currentSpeed - limit) / limit * 100).toInt()
        } else {
            0
        }
    }
}