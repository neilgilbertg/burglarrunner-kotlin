package neilgilbert.gallardo.burglarrunner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toScoreboard = findViewById<Button>(R.id.toScoreboard);
        toScoreboard.setOnClickListener {
            val intent = Intent(this, Scoreboard::class.java);
            startActivity(intent);
        }

        val toStartGame = findViewById<Button>(R.id.toStartGame);
        toStartGame.setOnClickListener {
            val intent = Intent(this, Game::class.java);
            startActivity(intent);
        }
    }
}