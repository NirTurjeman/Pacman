package com.example.hw_1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    private lateinit var welcome_startButton: Button
    private lateinit var welcome_playerName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        findViews()
        startNewGame()
    }

    private fun findViews() {
        welcome_startButton = findViewById(R.id.welcome_StartGame_Button)
        welcome_playerName = findViewById(R.id.welcome_nameInput)
    }

    private fun startNewGame() {
        welcome_startButton.setOnClickListener {
            val playerName = welcome_playerName.text.toString().trim()

            if (playerName.isEmpty()) {
                Toast.makeText(this, "Please enter your name before starting the game", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("PlayerName", playerName)
            startActivity(intent)
        }
    }
}
