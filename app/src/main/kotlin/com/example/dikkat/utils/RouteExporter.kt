package com.example.dikkat.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.FileProvider
import com.example.dikkat.models.SavedRoute
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RouteExporter(private val context: Context) {
    
    fun exportRouteToGPX(route: SavedRoute): File? {
        return try {
            val fileName = "${route.name.replace(" ", "_")}_${System.currentTimeMillis()}.gpx"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            val gpxContent = generateGPXContent(route)
            file.writeText(gpxContent)
            file
        } catch (e: Exception) {
            Log.e("RouteExporter", "GPX export error: ${e.message}", e)
            null
        }
    }
    
    fun exportRouteToJSON(route: SavedRoute): File? {
        return try {
            val fileName = "${route.name.replace(" ", "_")}_${System.currentTimeMillis()}.json"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            val jsonContent = route.toJsonString()
            file.writeText(jsonContent)
            file
        } catch (e: Exception) {
            Log.e("RouteExporter", "JSON export error: ${e.message}", e)
            null
        }
    }
    
    fun exportRouteToCSV(route: SavedRoute): File? {
        return try {
            val fileName = "${route.name.replace(" ", "_")}_${System.currentTimeMillis()}.csv"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            val csvContent = """Rota Adı,Başlangıç,Hedef,Mesafe (km),Süre (dakika),Yol Tipi,Tarih,Notlar
${route.name},"${route.startAddress}","${route.endAddress}",${route.distance},${route.duration / 60},${route.roadType},${route.getFormattedDate()},"${route.notes}"""
            
            file.writeText(csvContent)
            file
        } catch (e: Exception) {
            Log.e("RouteExporter", "CSV export error: ${e.message}", e)
            null
        }
    }
    
    fun shareRoute(route: SavedRoute, format: ShareFormat = ShareFormat.TEXT): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = when (format) {
                    ShareFormat.TEXT -> "text/plain"
                    ShareFormat.GPX -> "application/gpx+xml"
                    ShareFormat.JSON -> "application/json"
                    ShareFormat.CSV -> "text/csv"
                }
            }
            
            when (format) {
                ShareFormat.TEXT -> {
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Dikkat: ${route.name}")
                    intent.putExtra(Intent.EXTRA_TEXT, route.toShareText())
                }
                ShareFormat.GPX -> {
                    val file = exportRouteToGPX(route)
                    if (file != null) {
                        val uri = getFileUri(file)
                        intent.putExtra(Intent.EXTRA_STREAM, uri)
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                }
                ShareFormat.JSON -> {
                    val file = exportRouteToJSON(route)
                    if (file != null) {
                        val uri = getFileUri(file)
                        intent.putExtra(Intent.EXTRA_STREAM, uri)
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                }
                ShareFormat.CSV -> {
                    val file = exportRouteToCSV(route)
                    if (file != null) {
                        val uri = getFileUri(file)
                        intent.putExtra(Intent.EXTRA_STREAM, uri)
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                }
            }
            
            context.startActivity(Intent.createChooser(intent, "Rotayı Paylaş"))
            true
        } catch (e: Exception) {
            Log.e("RouteExporter", "Share error: ${e.message}", e)
            false
        }
    }
    
    private fun getFileUri(file: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } else {
            @Suppress("DEPRECATION")
            Uri.fromFile(file)
        }
    }
    
    private fun generateGPXContent(route: SavedRoute): String {
        val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())
        
        return """<?xml version="1.0" encoding="UTF-8"?>
<gpx version="1.1" creator="Dikkat" xmlns="http://www.topografix.com/GPX/1/1">
  <metadata>
    <name>${route.name}</name>
    <time>$timestamp</time>
    <bounds />
  </metadata>
  <trk>
    <name>${route.name}</name>
    <desc>Başlangıç: ${route.startAddress} | Hedef: ${route.endAddress}</desc>
    <trkpt lat="0" lon="0">
      <name>Başlangıç</name>
    </trkpt>
    <trkpt lat="0" lon="0">
      <name>Hedef</name>
    </trkpt>
  </trk>
</gpx>"""
    }
    
    enum class ShareFormat {
        TEXT, GPX, JSON, CSV
    }
}