package neilgilbert.gallardo.burglarrunner

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Looper
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import kotlinx.coroutines.delay
import kotlin.random.Random


class GameLogic(g : Game): Thread(){
    private var gameLoop = true;
    private val g = g;
    private var screenWidth = 0;
    private var screenHeight = 0;

    private val GAME_SPEED = 50;
    private var gameCounter = 0;
    private val SPAWN_SPEED = 25;
    private var spawnCounter = 0;


    private var player = ImageView(g)
    private var PLAYER_JUMPHEIGHT = 200;
    private var playerScore = 0;
    private var playerloc_x = 250;
    private var playerloc_y = 500;
    private val PLAYER_SIZES = 400;
    private var playerJump = false;

    private var obstacle = arrayListOf<ImageView>();
    private var obstacleXLocs = arrayListOf<Int>();
    private var obstacleYLocs = arrayListOf<Int>();
    private val OBS_FAIL_RAD = 10;
    private val OBS_LIMIT = 1;
    private val OBS_SIZES = 250;

    private var money = arrayListOf<ImageView>();
    private var moneyXLocs = arrayListOf<Int>();
    private var moneyYLocs = arrayListOf<Int>();
    private val MON_COLLECT_RAD = 10;
    private val MON_LIMIT = 1;
    private val MON_SIZES = 250;

    override fun run() {
        addPlayer()

        while (gameLoop)
        {
            gameCounter++;
            println("Game Loop RUNNING")

            // Check if update speed is valid
            if(gameCounter>0 && gameCounter%GAME_SPEED == 0){
                gameCounter = 0;
                spawnCounter++;

                // Spawn check
                if(spawnCounter>0 && spawnCounter%SPAWN_SPEED == 0){
                    spawnCounter = 0;
                    if(obstacle.size > 0){
                        if(MON_LIMIT > money.size){
                            if(Random.nextInt(0, 100) > 90){
                                addMoney()
                            }
                        }
                    }
                    if(OBS_LIMIT > obstacle.size){
                        if(Random.nextInt(0, 100) > 90){
                            addObstacle()
                        }
                    }
                }

                // OBSTACLE Method Calls
                updateObstaclePosition();
                if(checkIfObsCollided()){
                    removeObstacle(getCollidedObstacle())
                    endGame()
                }

                // MONEY Method Calls
                updateMoneyPosition();
                if(checkIfMonCollided()){
                    removeMoney(getCollidedMoney())
                    playerScore++;
                    g.runOnUiThread {
                        g.scoreDisplay!!.text = playerScore.toString();
                    }
                }
                for(mon in money)
                {
                    val monIndex = money.indexOf(mon)
                    if(moneyXLocs.get(monIndex) <= 0){
                        removeMoney(mon)
                    }
                }

                // PLAYER Method Calls
                updatePlayerPosition();
            }
        }
        recordScore()
    }

    /////// OBSTACLE Functions
    private fun addObstacle(){
        var obs = ImageView(g)
        obs.setImageResource(R.drawable.obstacle)
        obstacle.add(obs)
        obstacleXLocs.add(screenWidth)
        obstacleYLocs.add(playerloc_y)
        g.runOnUiThread {
            g.display!!.addView(obs)
            obs.layoutParams.width = OBS_SIZES;
            obs.layoutParams.height = OBS_SIZES;
        }
    }
    private fun removeObstacle(obs : View?){
        var obsIndex = obstacle.indexOf(obs)
        obstacle.removeAt(obsIndex)
        obstacleXLocs.removeAt(obsIndex)
        obstacleYLocs.removeAt(obsIndex)
        g.runOnUiThread {
            g.display!!.removeView(obs)
        }
    }
    private fun updateObstaclePosition(){
        for(obs in obstacle)
        {
            val obsIndex = obstacle.indexOf(obs)
            if(obstacleXLocs.get(obsIndex) > 0) {
                obstacleXLocs.set(obsIndex, obstacleXLocs.get(obsIndex)-1)
            } else {
                removeObstacle(obs);
                break;
            }

            obs.x = obstacleXLocs.get(obsIndex).toFloat();
            obs.y = obstacleYLocs.get(obsIndex).toFloat();
        }
    }
    private fun checkIfObsCollided() : Boolean{
        for(obs in obstacle)
        {
            if( (Math.abs(player.x-obs.x) <= OBS_FAIL_RAD) &&
                (Math.abs(player.y-obs.y) <= OBS_FAIL_RAD) ){
                return true;
            }
        }
        return false;
    }
    private fun getCollidedObstacle() : View?{
        for(obs in obstacle)
        {
            if( (Math.abs(player.x-obs.x) <= OBS_FAIL_RAD) &&
                (Math.abs(player.y-obs.y) <= OBS_FAIL_RAD) ){
                return obs;
            }
        }
        return null;
    }

    /////// MONEY Functions
    private fun addMoney(){
        var mon = ImageView(g)
        g.runOnUiThread {
            Glide.with(g).load(R.drawable.money).into(mon);
        }
        money.add(mon)
        moneyXLocs.add(screenWidth)
        moneyYLocs.add(playerloc_y)
        g.runOnUiThread {
            g.display!!.addView(mon)
            mon.layoutParams.width = MON_SIZES;
            mon.layoutParams.height = MON_SIZES;
        }
    }
    private fun removeMoney(mon : View?){
        var monIndex = money.indexOf(mon)
        money.removeAt(monIndex)
        moneyXLocs.removeAt(monIndex)
        moneyYLocs.removeAt(monIndex)
        g.runOnUiThread {
            g.display!!.removeView(mon)
        }
    }
    private fun updateMoneyPosition(){
        for(mon in money)
        {
            val monIndex = money.indexOf(mon)
            if(moneyXLocs.get(monIndex) > 0) {
                moneyXLocs.set(monIndex, moneyXLocs.get(monIndex)-1)
            } else {
                removeMoney(mon);
                break;
            }

            mon.x = moneyXLocs.get(monIndex).toFloat();
            mon.y = moneyYLocs.get(monIndex).toFloat();
        }
    }
    private fun checkIfMonCollided() : Boolean{
        for(mon in money)
        {
            if( (Math.abs(player.x-mon.x) <= MON_COLLECT_RAD) &&
                (Math.abs(player.y-mon.y) <= MON_COLLECT_RAD) ){
                return true;
            }
        }
        return false;
    }
    private fun getCollidedMoney() : View?{
        for(mon in money)
        {
            if( (Math.abs(player.x-mon.x) <= MON_COLLECT_RAD) &&
                (Math.abs(player.y-mon.y) <= MON_COLLECT_RAD) ){
                return mon;
            }
        }
        return null;
    }
    /////// PLAYER Functions
    private fun addPlayer(){
        g.runOnUiThread {
            Glide.with(g).load(R.drawable.burglar).into(player);
        }
        g.display!!.addView(player)
        player.layoutParams.width = PLAYER_SIZES;
        player.layoutParams.height = PLAYER_SIZES;
        player.x = playerloc_x.toFloat();
        player.y = playerloc_y.toFloat();
    }
    private fun updatePlayerPosition(){
        if(playerJump){
            if (Math.abs(player.y-playerloc_y.toFloat()) >= PLAYER_JUMPHEIGHT){
                playerJump = false;
            } else {
                player.y--;
            }
        } else {
            if (player.y >= playerloc_y.toFloat()){
                player.y = playerloc_y.toFloat()
            } else {
                player.y++;
            }
        }
    }

    /////// INPUT Functions
    fun inputDown() {
        playerJump = true;
    }
    fun inputUp() {
        playerJump = false;
    }

    /////// GAME Functions
    fun endGame(){
        gameLoop = false;
        println("Game Loop ENDED.")
    }
    fun recordScore(){
        if(playerScore > 0){
            g.runOnUiThread {
                val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(g)
                builder.setTitle("Record Score?")

                val inPName = EditText(g)
                inPName.setHint("Enter Name")
                inPName.inputType = InputType.TYPE_CLASS_TEXT
                builder.setView(inPName)

                builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                    // Here you get get input text from the Edittext
                    g.recordScore(inPName.text.toString(), playerScore)
                    g.endSession()
                })
                builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                    g.endSession()
                })

                builder.show()
            }
        } else {
            g.endSession()
        }
    }
    fun setScreenDimensions(width : Int, height : Int){
        screenWidth = width;
        screenHeight = height;
    }
}