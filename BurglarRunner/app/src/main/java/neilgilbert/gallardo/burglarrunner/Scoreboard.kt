package neilgilbert.gallardo.burglarrunner

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class Scoreboard : AppCompatActivity() {
    val db = DBHelper(this, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)
        val scoreBoard = findViewById<TableLayout>(R.id.scoreBoard);
        refreshScoreboard(scoreBoard)

        findViewById<Button>(R.id.btnRetr).setOnClickListener {
            val intent = Intent(this, Game::class.java);
            startActivity(intent);
            finish()
        }
        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    var scoreRows = arrayListOf<TableRow>();
    @SuppressLint("Range")
    fun refreshScoreboard(scoreboard : TableLayout){
        for(r in scoreRows)
        {
            scoreboard.removeView(r)
        }

        val cursor = db.getScores()
        cursor!!.moveToFirst()

        var tableRow = TableRow(this)
        var rowCols = LinearLayout(this)
        var nameCol = TextView(this)
        var scoreCol = TextView(this)
        rowCols.orientation = LinearLayout.HORIZONTAL;

        if(cursor.count>0){
            nameCol.text = cursor.getString(cursor.getColumnIndex(DBHelper.NAME_COl))
            scoreCol.text = cursor.getString(cursor.getColumnIndex(DBHelper.SCORE_COL))
        }

        rowCols.addView(nameCol)
        rowCols.addView(scoreCol)
        tableRow.addView(rowCols)

        scoreRows.add(tableRow)
        scoreboard.addView(tableRow)

        while(cursor.moveToNext()){
            tableRow = TableRow(this)
            rowCols = LinearLayout(this)
            nameCol = TextView(this)
            scoreCol = TextView(this)
            rowCols.orientation = LinearLayout.HORIZONTAL;

            nameCol.text = cursor.getString(cursor.getColumnIndex(DBHelper.NAME_COl))
            scoreCol.text = cursor.getString(cursor.getColumnIndex(DBHelper.SCORE_COL))
            rowCols.addView(nameCol)
            rowCols.addView(scoreCol)
            tableRow.addView(rowCols)

            scoreRows.add(tableRow)
            scoreboard.addView(tableRow)
        }
        cursor.close()
    }
}