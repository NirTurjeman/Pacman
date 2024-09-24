package Utilites
import Models.Location
import Models.ScoreList
import android.util.Log
import com.example.hw_1.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DataManager {

    fun setHighScoreList(newScoreList: MutableList<ScoreList>) {
        SharedPreferencesManager.getInstance().putScoreList(R.string.KEY.toString(), newScoreList)
    }

    fun getHighScoreList(): MutableList<ScoreList> {
        val gson = Gson()
        val json = SharedPreferencesManager.getInstance().getString(R.string.KEY.toString(), null)

        return if (json != null) {
            val type = object : TypeToken<MutableList<ScoreList>>() {}.type
            gson.fromJson<MutableList<ScoreList>>(json, type)
        } else
            mutableListOf()
    }

    fun generateHighScoreList(): MutableList<ScoreList> {
        val generateHighScoreList: MutableList<ScoreList> = mutableListOf()

        generateHighScoreList.add(
            ScoreList.Builder()
                .setScore(100)
                .setDate("24/01/2021")
                .setLocation(Location.Builder()
                    .address("Tel-Aviv, Israel")
                    .coordinates(51.4188, 40.0428)
                    .build())
                .build()
        )

        generateHighScoreList.add(
            ScoreList.Builder()
                .setScore(150)
                .setDate("20/01/2024")
                .setLocation(Location.Builder()
                    .address("Haifa, Israel")
                    .coordinates(32.7940, 34.9896)
                    .build())
                .build()
        )

        generateHighScoreList.add(
            ScoreList.Builder()
                .setScore(200)
                .setDate("20/04/2020")
                .setLocation(Location.Builder()
                    .address("Jerusalem, Israel")
                    .coordinates(31.7683, 35.2137)
                    .build())
                .build()
        )

        generateHighScoreList.add(
            ScoreList.Builder()
                .setScore(250)
                .setDate("10/03/2024")
                .setLocation(Location.Builder()
                    .address("Beersheba, Israel")
                    .coordinates(31.2529, 34.7915)
                    .build())
                .build()
        )

        generateHighScoreList.add(
            ScoreList.Builder()
                .setScore(300)
                .setDate("15/03/2024")
                .setLocation(Location.Builder()
                    .address("Eilat, Israel")
                    .coordinates(29.5581, 34.9482)
                    .build())
                .build()
        )

        generateHighScoreList.add(
            ScoreList.Builder()
                .setScore(350)
                .setDate("24/03/2024")
                .setLocation(Location.Builder()
                    .address("Ashdod, Israel")
                    .coordinates(31.8044, 34.6553)
                    .build())
                .build()
        )

        generateHighScoreList.add(
            ScoreList.Builder()
                .setScore(400)
                .setDate("20/03/2024")
                .setLocation(Location.Builder()
                    .address("Netanya, Israel")
                    .coordinates(32.3215, 34.8532)
                    .build())
                .build()
        )

        generateHighScoreList.add(
            ScoreList.Builder()
                .setScore(450)
                .setDate("20/03/2024")
                .setLocation(Location.Builder()
                    .address("Holon, Israel")
                    .coordinates(32.0158, 34.7871)
                    .build())
                .build()
        )

        generateHighScoreList.add(
            ScoreList.Builder()
                .setScore(500)
                .setDate("20/08/2023")
                .setLocation(Location.Builder()
                    .address("Rishon LeZion, Israel")
                    .coordinates(31.9710, 34.7925)
                    .build())
                .build()
        )

        generateHighScoreList.add(
            ScoreList.Builder()
                .setScore(550)
                .setDate("20/01/2022")
                .setLocation(Location.Builder()
                    .address("Petah Tikva, Israel")
                    .coordinates(32.0840, 34.8878)
                    .build())
                .build()
        )


        return generateHighScoreList
    }
}
