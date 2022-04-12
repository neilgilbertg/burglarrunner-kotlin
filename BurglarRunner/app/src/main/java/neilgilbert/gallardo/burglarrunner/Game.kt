package neilgilbert.gallardo.burglarrunner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class Game : AppCompatActivity() {
    var display : ConstraintLayout? = null;
    var scoreDisplay : TextView? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val game = GameLogic(this);
        setContentView(R.layout.activity_game)

        display = findViewById(R.id.gameDisplay)
        scoreDisplay = findViewById(R.id.scr)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        game.setScreenDimensions(displayMetrics.widthPixels, displayMetrics.heightPixels)
        game.start()

        val gamePrompt = findViewById<Button>(R.id.gamePrompt)
        gamePrompt.setOnTouchListener { view, motionEvent ->
            // Task here
            when (motionEvent.action){
                MotionEvent.ACTION_DOWN -> {
                    println("input ON")
                    game.inputDown()
                }
                MotionEvent.ACTION_UP -> {
                    println("input OFF")
                    game.inputUp()
                }
            }
            true
        }

        val bg_scroll = AnimationUtils.loadAnimation(this, R.anim.bg_scroll)
        findViewById<ImageView>(R.id.bg).startAnimation(bg_scroll)
    }

    fun recordScore(pName : String, score : Int){
        DBHelper(this, null).addScore(pName, score)

        val intent = Intent(this, Scoreboard::class.java);
        startActivity(intent);
    }
    fun endSession(){
        finish()
    }
}