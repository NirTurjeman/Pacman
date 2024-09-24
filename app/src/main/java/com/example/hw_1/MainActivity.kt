package com.example.hw_1

import Models.ScoreList
import Utilites.DataManager
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.hw_1.models.GameType
import com.example.hw_1.models.SpeedMode
import com.example.hw_1.services.SensorServices
import com.example.hw_1.services.VibrationServices
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textview.MaterialTextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() , SensorServices.SensorCallback {
    private var screenWidth: Int = 0
    private var laneWide: Int = 0
    private var laneHigh: Int = 0
    private lateinit var main_Score_LBL: MaterialTextView
    private lateinit var main_Distance_LBL : MaterialTextView
    private lateinit var main_Button_Left: ImageButton
    private lateinit var main_Button_Right: ImageButton
    private lateinit var main_Player: AppCompatImageView
    private var currentLane = 3
    private lateinit var main_health_parameters: Array<AppCompatImageView>
    private var lifeCounter: Int = 2
    private lateinit var gameLayout: ViewGroup
    private lateinit var gameItemsMatrix: Array<Array<Int>>
    private lateinit var lanes: Array<Int>
    private val itemHandler = Handler()
    private val addItemHandler = Handler()
    private lateinit var vibrationService: VibrationServices
    private lateinit var sensorServices: SensorServices
    private lateinit var playingType: GameType
    private lateinit var speedMode: SpeedMode
    private var currentGhostCount = 0
    private var currentCoinCount = 0
    private var scoreCounter = 0
    private var distanceCounter = 0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    private lateinit var crashSound : MediaPlayer
    private var speedDelay : Long = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Variables
        this.screenWidth = resources.displayMetrics.widthPixels
        this.laneWide = resources.getInteger(R.integer.laneWide)
        this.laneHigh = resources.getInteger(R.integer.laneHigh)
        this.gameItemsMatrix = Array(laneHigh) { Array(laneWide) { 0 } }
        this.lanes = Array(laneWide) { 0 }
        this.sensorServices = SensorServices(this, this)
        this.playingType =
            intent.getStringExtra("playingType")?.let { GameType.valueOf(it) } ?: GameType.BUTTONS
        this.speedMode = intent.getStringExtra("SpeedMode")?.let { SpeedMode.valueOf(it) } ?: SpeedMode.SLOW
        Log.d("PlayingType:" , "${this.speedMode.name} , ${this.playingType.name}")
        this.vibrationService = VibrationServices(this)
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        this.crashSound = MediaPlayer.create(this, R.raw.crash_sound)

        // Find Views
        findViews()
        setupLanes()
        updatePlayerPosition()
        setupGame()


        // Start the game
        addItemHandler.post(addGhostRunnable)
        addItemHandler.post(addCoinRunnable)
    }
    override fun onPause() {
        super.onPause()
        sensorServices.unregisterSensor()
        itemHandler.removeCallbacksAndMessages(null)
        addItemHandler.removeCallbacksAndMessages(null)
    }
    private fun findViews() {
        main_Score_LBL = findViewById(R.id.main_Score_LBL)
        main_Distance_LBL = findViewById(R.id.main_Distance_LBL)
        main_Button_Left = findViewById(R.id.main_Botton_Left)
        main_Button_Left.visibility = View.GONE
        main_Button_Right = findViewById(R.id.main_Botton_Right)
        main_Button_Right.visibility = View.GONE
        main_Player = findViewById(R.id.main_IMG_Player)
        main_health_parameters = arrayOf(
            findViewById(R.id.main_IMG_health0),
            findViewById(R.id.main_IMG_health1),
            findViewById(R.id.main_IMG_health2)
        )
        gameLayout = findViewById(R.id.main)
    }

    // Coin setup
    private fun addCoin() {
        val randomColumn = (0 until lanes.size).random()
        this.currentCoinCount++
        val coin = ImageView(this)
        coin.setImageResource(R.drawable.coin)
        coin.setTag("Coin")
        val coinWidth = resources.getDimensionPixelSize(R.dimen.coinSize)
        val coinHeight = resources.getDimensionPixelSize(R.dimen.coinSize)

        coin.layoutParams = ViewGroup.MarginLayoutParams(
            coinWidth,
            coinHeight
        ).apply {
            leftMargin = lanes[randomColumn]
            topMargin = resources.getDimension(R.dimen.startPosition).toInt()
        }
        gameLayout.addView(coin)
        gameItemsMatrix[0][randomColumn] = 1
        moveItemsDownward(coin, randomColumn)
    }

    // Ghost Setup
    private fun addGhost() {
        val randomColumn = (0 until lanes.size).random()
        this.currentGhostCount++
        val ghost = ImageView(this)
        ghost.setImageResource(R.drawable.pacman_logo)
        ghost.setTag("Ghost")
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
        gameItemsMatrix[0][randomColumn] = 1
        moveItemsDownward(ghost, randomColumn)
    }

    private fun moveItemsDownward(item: ImageView, column: Int) {
        val moveStep = resources.getDimensionPixelSize(R.dimen.moveItem_dp)
        val itemTag = item.getTag()
        main_Distance_LBL.text = ("Distance: " + distanceCounter++.toString())
        itemHandler.post(object : Runnable {
            var row = 0
            override fun run() {
                if (row >= gameItemsMatrix.size - 1) {
                    if (currentLane == column) {
                        when (itemTag) {
                            "Ghost" -> loseHealth()
                            "Coin" -> scorePlusPlus()
                        }
                    }
                    when (itemTag) {
                        "Ghost" -> currentGhostCount--
                        "Coin" -> currentCoinCount--
                    }
                    gameItemsMatrix[row][column] = 0
                    gameLayout.removeView(item)
                    return
                }
                gameItemsMatrix[row][column] = 0
                row++
                gameItemsMatrix[row][column] = 1
                val params = item.layoutParams as ViewGroup.MarginLayoutParams
                params.topMargin += moveStep
                item.layoutParams = params
                item.requestLayout()
                itemHandler.postDelayed(this, speedDelay)
            }
        })
    }

    private val addGhostRunnable = object : Runnable {
        override fun run() {
            if (currentGhostCount < resources.getInteger(R.integer.MAX_GHOSTS)) {
                addGhost()
            }
            addItemHandler.postDelayed(this, 2000)
        }
    }

    private val addCoinRunnable = object : Runnable {
        override fun run() {
            if (currentCoinCount < resources.getInteger(R.integer.MAX_COINS)) {
                addCoin()
            }
            addItemHandler.postDelayed(this, 3000)
        }
    }

    private fun scorePlusPlus() {
        this.scoreCounter+= 10
        main_Score_LBL.text = ("Score: " + scoreCounter.toString())

    }

    private fun loseHealth() {
        if (lifeCounter <= 0) {
            val intent = Intent(this, ScoreActivity::class.java)
            createScore()
            startActivity(intent)
            finish()
        }
        crashSound.start();
        Toast.makeText(this, "Oops! You hit a ghost!", Toast.LENGTH_SHORT).show()
        vibrationService.vibrateDevice(500)
        main_health_parameters[lifeCounter].visibility = View.INVISIBLE
        lifeCounter--
        return
    }

    private fun setupGame() {
        when (playingType) {
            GameType.BUTTONS -> {
                setupButtons()
                main_Button_Left.visibility = View.VISIBLE
                main_Button_Right.visibility = View.VISIBLE
            }
            GameType.MOVE_SENSOR -> setupSensor()
        }
        this.speedDelay = when (speedMode) {
            SpeedMode.SLOW -> 500
            SpeedMode.FAST -> 200
        }
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

    private fun setupSensor() {
        sensorServices.registerSensor()
    }

    // Player Functions
    private fun setupLanes() {
        lanes[0] = (screenWidth * 0.05).toInt()
        lanes[1] = (screenWidth * 0.25).toInt() - main_Player.width / 2
        lanes[2] = (screenWidth * 0.45).toInt() - main_Player.width / 2
        lanes[3] = (screenWidth * 0.65).toInt() - main_Player.width / 2
        lanes[4] = (screenWidth * 0.80).toInt() - main_Player.width
    }

    private fun updatePlayerPosition() {
        main_Player.post {
            val params = main_Player.layoutParams as ViewGroup.MarginLayoutParams
            params.leftMargin = lanes[currentLane]
            main_Player.layoutParams = params
            main_Player.requestLayout()
        }
    }

    override fun movePlayerLeft() {
        if (currentLane > 0) {
            currentLane--
            updatePlayerPosition()
        }
    }

    override fun movePlayerRight() {
        if (currentLane < laneWide - 1) {
            currentLane++
            updatePlayerPosition()
        }
    }
    private fun getCurrentLocation(callback: (Models.Location?) -> Unit) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)

                    if (addresses != null && addresses.isNotEmpty()) {
                        val address = addresses[0]
                        val locationData = Models.Location(
                            "${address.locality}, ${address.countryName}",
                            latitude,
                            longitude
                        )
                        callback(locationData)
                    } else {
                        callback(null)
                    }
                } else {
                    callback(null)
                }
            }.addOnFailureListener { exception ->
                Log.e("Location", "Error getting location: ${exception.message}")
                callback(null)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            callback(null)
        }
    }
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }
    private fun createScore() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        }
        getCurrentLocation { locationData ->
            Log.d("LocationTest", "Attempting to get location")
            if (locationData != null) {
                Log.d("LocationTest", "Location received: $locationData")
                val newScore = ScoreList.Builder()
                    .setScore(this.scoreCounter)
                    .setLocation(locationData)
                    .setDate(getCurrentDate())
                    .build()
                saveHighTenScores(newScore)
            } else {
                Log.e("createScore", "Failed to get location data.")
            }
        }
    }
    private fun saveHighTenScores(newScore: ScoreList) {
        var scoreList = DataManager.getHighScoreList()
        scoreList.add(newScore)
        scoreList.sortByDescending { it.getScore() }
        if (scoreList.size > 10) {
            scoreList = scoreList.take(10).toMutableList()
        }
        DataManager.setHighScoreList(scoreList)
    }
}


