package com.example.hw_1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView

class MainActivity : AppCompatActivity() {
    private var screenWidth: Int = 0
    private lateinit var main_Button_Left: ImageButton
    private lateinit var main_Button_Right: ImageButton
    private lateinit var main_Player: AppCompatImageView
    private var currentLane = 1
    private lateinit var playerName : String
    private lateinit var main_health_parameters: Array<AppCompatImageView>
    private var lifeCounter: Int = 2
    private lateinit var gameLayout: ViewGroup
    private var ghostMatrix: Array<Array<Int>> = Array(15) { Array(3) { 0 } }
    private var lanes: Array<Int> = Array(3) { 0 }
    private val ghostHandler = Handler()
    private val addGhostHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Variables
        this.screenWidth = resources.displayMetrics.widthPixels
        this.playerName = intent.getStringExtra("PlayerName").toString()
        // Find Views
        findViews()
        setupLanes()
        updatePlayerPosition()
        setupButtons()

        // Start the game
        addGhostHandler.post(addGhostRunnable)
        ghostHandler.post(ghostRunnable)
    }

    private fun findViews() {
        main_Button_Left = findViewById(R.id.main_Botton_Left)
        main_Button_Right = findViewById(R.id.main_Botton_Right)
        main_Player = findViewById(R.id.main_IMG_Player)
        main_health_parameters = arrayOf(
            findViewById(R.id.main_IMG_health0),
            findViewById(R.id.main_IMG_health1),
            findViewById(R.id.main_IMG_health2)
        )
        gameLayout = findViewById(R.id.main)
    }
    fun vibrateDevice() {
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(effect)
            } else {
                vibrator.vibrate(500)
            }
        }
    }
    // Ghost Setup
    private fun addGhost() {
        val randomColumn = (0 until lanes.size).random()

        val ghost = ImageView(this)
        ghost.setImageResource(R.drawable.pacman_logo)

        val ghostWidth = resources.getDimensionPixelSize(R.dimen.ghostSize)
        val ghostHeight = resources.getDimensionPixelSize(R.dimen.ghostSize)

        ghost.layoutParams = ViewGroup.MarginLayoutParams(
            ghostWidth,
            ghostHeight
        ).apply {
            leftMargin = lanes[randomColumn]
            topMargin = resources.getDimension(R.dimen.startPosition).toInt()
        }
        gameLayout.addView(ghost)
        ghostMatrix[0][randomColumn] = 1
        moveGhostDownward(ghost, randomColumn)
    }

    private fun moveGhostDownward(ghost: ImageView, column: Int) {
        val moveStep = resources.getDimensionPixelSize(R.dimen.moveEnemy_dp)
        ghostHandler.post(object : Runnable {
            var row = 0
            override fun run() {
                if (row >= ghostMatrix.size - 1) {
                    if(currentLane == column){
                        loseHealth()
                    }
                    ghostMatrix[row][column] = 0
                    gameLayout.removeView(ghost)
                    return
                }
                ghostMatrix[row][column] = 0
                row++
                ghostMatrix[row][column] = 1
                val params = ghost.layoutParams as ViewGroup.MarginLayoutParams
                params.topMargin += moveStep
                ghost.layoutParams = params
                ghost.requestLayout()
                ghostHandler.postDelayed(this, 200)
            }
        })
    }

    private val addGhostRunnable = object : Runnable {
        override fun run() {
            addGhost()
            addGhostHandler.postDelayed(this, 1000)
        }
    }

    private val ghostRunnable = object : Runnable {
        override fun run() {
            ghostHandler.postDelayed(this, 1000)
        }
    }

    private fun loseHealth() {
        if (lifeCounter < 0) {
            val intent = Intent(this, GameOver::class.java)
            intent.putExtra("PlayerName", playerName)
            startActivity(intent)
            finish()
            return
        }
        Toast.makeText(this, "Oops! You hit a ghost!", Toast.LENGTH_SHORT).show()
        vibrateDevice()
        main_health_parameters[lifeCounter].visibility = View.INVISIBLE
        lifeCounter--
        return
    }
    // Buttons Functions
    private fun setupButtons() {
        main_Button_Left.setOnClickListener {
            movePlayerLeft()
        }
        main_Button_Right.setOnClickListener {
            movePlayerRight()
        }
    }

    // Player Functions
    private fun setupLanes() {
        lanes[0] = (screenWidth * 0.1).toInt()
        lanes[1] = (screenWidth * 0.45).toInt() - main_Player.width / 2
        lanes[2] = (screenWidth * 0.80).toInt() - main_Player.width
    }

    private fun updatePlayerPosition() {
        main_Player.post {
            val params = main_Player.layoutParams as ViewGroup.MarginLayoutParams
            params.leftMargin = lanes[currentLane]
            main_Player.layoutParams = params
            main_Player.requestLayout()
        }
    }

    private fun movePlayerLeft() {
        if (currentLane > 0) {
            currentLane--
            updatePlayerPosition()
        }
    }

    private fun movePlayerRight() {
        if (currentLane < 2) {
            currentLane++
            updatePlayerPosition()
        }
    }
}
