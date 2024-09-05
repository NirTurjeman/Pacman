package com.example.hw_1


import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOver : AppCompatActivity() {
    private lateinit var gameOverNameText: TextView
    private lateinit var playerName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)
        findViews()
        this.playerName = intent.getStringExtra("PlayerName") ?: "Unknown Player"
        gameOverNameText.text = getString(R.string.game_over_message, playerName)
    }

    private fun findViews() {
        gameOverNameText = findViewById(R.id.gameOver_Name_Text)
    }
}
