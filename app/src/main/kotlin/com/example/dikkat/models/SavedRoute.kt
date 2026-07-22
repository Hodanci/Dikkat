package com.example.dikkat.models

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

data class SavedRoute(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val startAddress: String,
    val endAddress: String,
    val distance: Double,
    val duration: Long,
    val polylineString: String,
    val roadType: String = "Genel",
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String = ""
) : Serializable {
    
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("tr", "TR"))
        return sdf.format(Date(createdAt))
    }
    
    fun getFormattedDistance(): String = String.format("%.1f km", distance)
    
    fun getFormattedDuration(): String {
        val hours = duration / 3600
        val minutes = (duration % 3600) / 60
        return when {
            hours > 0 -> "$hours sa $minutes dk"
            else -> "$minutes dk"
        }
    }
    
    fun toShareText(): String {
        return """Dikkat Uygulaması ile Kaydedilen Rota
        
Rota Adı: $name
Başlangıç: $startAddress
Hedef: $endAddress
Mesafe: ${getFormattedDistance()}
Süre: ${getFormattedDuration()}
Yol Tipi: $roadType
Kayıt Tarihi: ${getFormattedDate()}
Notlar: $notes

Dikkat Uygulaması: https://github.com/Hodanci/Dikkat"""
    }
    
    fun toJsonString(): String {
        return """{
  "name": "$name",
  "startAddress": "$startAddress",
  "endAddress": "$endAddress",
  "distance": $distance,
  "duration": $duration,
  "polylineString": "$polylineString",
  "roadType": "$roadType",
  "createdAt": $createdAt,
  "notes": "$notes"
}"""
    }
}