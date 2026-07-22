package com.example.dikkat.database

import android.content.Context
import android.content.SharedPreferences
import com.example.dikkat.models.SavedRoute
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RouteDatabase(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("dikkat_routes", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val routesKey = "saved_routes"
    
    fun saveRoute(route: SavedRoute): Boolean {
        return try {
            val routes = getAllRoutes().toMutableList()
            routes.add(route)
            val json = gson.toJson(routes)
            sharedPreferences.edit().putString(routesKey, json).apply()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun getAllRoutes(): List<SavedRoute> {
        return try {
            val json = sharedPreferences.getString(routesKey, "[]")
            val type = object : TypeToken<List<SavedRoute>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    fun getRouteById(id: String): SavedRoute? {
        return getAllRoutes().find { it.id == id }
    }
    
    fun deleteRoute(id: String): Boolean {
        return try {
            val routes = getAllRoutes().toMutableList()
            routes.removeAll { it.id == id }
            val json = gson.toJson(routes)
            sharedPreferences.edit().putString(routesKey, json).apply()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun updateRoute(route: SavedRoute): Boolean {
        return try {
            deleteRoute(route.id) && saveRoute(route)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun searchRoutes(query: String): List<SavedRoute> {
        return getAllRoutes().filter {
            it.name.contains(query, ignoreCase = true) ||
            it.startAddress.contains(query, ignoreCase = true) ||
            it.endAddress.contains(query, ignoreCase = true) ||
            it.notes.contains(query, ignoreCase = true)
        }
    }
    
    fun clearAllRoutes(): Boolean {
        return try {
            sharedPreferences.edit().remove(routesKey).apply()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}