package com.example.hw_1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hw_1.models.GameType
import com.example.hw_1.models.SpeedMode


class WelcomeActivity : AppCompatActivity() {
    private lateinit var welcome_startButton: Button
    private var welcome_playingType: GameType = GameType.BUTTONS
    private lateinit var welcome_playingTypeList: RadioGroup
    private lateinit var welcome_scoreListButton : Button
    private var welcome_SpeedMode : SpeedMode = SpeedMode.FAST
    private lateinit var welcome_SpeedModeSwitch : Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        //FUNC CALL
        findViews()
        onPlayTypeSelected()
        onSpeedModeSelected()
        startGame()
        scoreListActivity()
    }

    private fun findViews() {
        welcome_startButton = findViewById(R.id.welcome_StartGame_Button)
        welcome_playingTypeList = findViewById(R.id.welcome_RadioButton_GameType)
        welcome_playingTypeList = findViewById(R.id.welcome_RadioButton_GameType)
        welcome_scoreListButton = findViewById(R.id.welcome_HistoryScore_Button)
        welcome_SpeedModeSwitch = findViewById(R.id.welcome_Speed_Switch)
    }
    private fun onSpeedModeSelected() {
        welcome_SpeedModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                welcome_SpeedMode = SpeedMode.SLOW
            } else {
                welcome_SpeedMode = SpeedMode.FAST
            }

            Toast.makeText(this, "Speed Mode: $welcome_SpeedMode", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun onPlayTypeSelected() {
        welcome_playingTypeList.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.welcome_GameType_MoveByButton -> {
                    welcome_playingType = GameType.BUTTONS
                    Toast.makeText(this, "Game Type: $welcome_playingType", Toast.LENGTH_SHORT)
                        .show()

                }

                R.id.welcome_GameType_MoveBySensor -> {
                    welcome_playingType = GameType.MOVE_SENSOR
                    Toast.makeText(this, "Game Type: $welcome_playingType", Toast.LENGTH_SHORT)
                        .show()

                }
            }
        }
    }
    private fun startGame(){
        welcome_startButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("playingType", this.welcome_playingType.name)
            intent.putExtra("SpeedMode", this.welcome_SpeedMode.name)
            startActivity(intent)
            Log.d("PlayingType:" , "${this.welcome_playingType} , ${this.welcome_SpeedMode}")
        }
    }
    private fun scoreListActivity(){
        welcome_scoreListButton.setOnClickListener {
            val intent = Intent(this, ScoreActivity::class.java)
            startActivity(intent)
        }
    }
}


