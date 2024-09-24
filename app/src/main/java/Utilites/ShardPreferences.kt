package Utilites
import Models.ScoreList
import android.content.Context
import android.content.SharedPreferences
import com.example.hw_1.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesManager private constructor(context: Context) {
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.KEY), Context.MODE_PRIVATE)

    companion object {
        private var instance: SharedPreferencesManager? = null

        fun init(context: Context): SharedPreferencesManager {
            return instance ?: synchronized(this) {
                instance ?: SharedPreferencesManager(context).also { instance = it }
            }
        }

        fun getInstance(): SharedPreferencesManager {
            return instance
                ?: throw IllegalStateException("SharedPreferencesManager must be initialized by calling init(context) before use.")
        }
    }
    fun putScoreList(key: String, scoreList: MutableList<ScoreList>) {
        val gson = Gson()
        val json = gson.toJson(scoreList)
        with(sharedPref.edit()) {
            putString(key, json)
            apply()
        }
    }
    fun getString(key: String, defaultValue: String?): String? {
        return sharedPref.getString(key, defaultValue)
    }
    fun getSharedPreferences(): SharedPreferences {
        return sharedPref
    }
}
