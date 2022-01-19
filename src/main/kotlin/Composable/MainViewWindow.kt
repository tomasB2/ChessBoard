package Composables

import Composable.buildButtons
import Composable.buildReview
import Composable.welcomeDisplay

import Composables.board.buildBoard
import Storage.BoardClass
import Storage.MongoDbBoard
import Storage.callFunc
import Storage.makeMove
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import domane.Commands
import domane.decodeMove
import Storage.*
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import java.util.*


@Composable //where refresh?
fun mainWindow(dbPair: Pair<DbMode,DbOperations>, onCloseRequested: () -> Unit) = Window(
    onCloseRequest = onCloseRequested,
    title = "Ultimate Chess",
    state = WindowState(size = DpSize(1920.dp, 1080.dp)),
    resizable = false
) {
    println("Composint main window")
    val displayState = remember{ mutableStateOf(value = "welcome") }
    val board = remember{ mutableStateOf(value = MongoDbBoard(BoardClass(), dbPair.second,dbPair.first)) }
    println(board.value.board.toString())

    val moveString = remember{ mutableStateOf("")}
    val listOfBoard = remember{mutableStateOf(LinkedList<String>())}

    val listIndex = remember { mutableStateOf(value = 0) }
    LaunchedEffect(board.value.board.actionState!=Commands.INVALID &&board.value.board.turn!=board.value.board.team && board.value.dbMode==DbMode.REMOTE) {
        println("Launching effect")
        while (true) {
            println("${board.value.board.currentGame_String}")
            if (board.value.board.turn!=board.value.board.team && board.value.dbMode==DbMode.REMOTE) {
                board.value = board.value.refresh()
            }
            delay(1_000)
        }
    }
    fun buildCoordinate():(String) -> Unit = {
        if(moveString.value.length == 5){
            val move = moveString
            moveString.value = ""
            println(move)
        }
        else {
            if(moveString.value.isEmpty()){
                if(it[0] != ' ') moveString.value += it
            }
            else{
                if(moveString.value[1] != it[1] || moveString.value[2] != it[2])
                    moveString.value =moveString.value+ it[1] + it[2]
            }
        }
    }

    fun addToList(board: String) { listOfBoard.value.add(board)  }

    fun onCancel():()->Unit={
        when(displayState.value){
            "review" -> displayState.value = "welcome"
            "play" -> if(board.value.board.actionState != Commands.WIN) displayState.value = "forfeit"
            "forfeit" -> displayState.value = "play"
            else -> onCloseRequested()
        }
    }

    fun makeMove():(callFunc: callFunc)->Unit= { //incorporar no buildCoordinate
        if (moveString.value.length == 5) {
            val currentBoard = board.value
            println(currentBoard.board.toString() + " ${board.value.hashCode()}")
            val a= decodeMove(moveString.value.split(""),currentBoard.board)
            val newBoard = currentBoard.makeMove(a!!,it)
            println(newBoard.board.toString() + " ${board.value.hashCode()}")
            if(newBoard.board.actionState != Commands.INVALID){
                if(newBoard.board.actionState != Commands.INVALID) addToList(board.value.board.toString())
                if(newBoard.board.actionState != Commands.PROMOTE) moveString.value = ""
            }
            println("--------- makeMove Window ----------")
            println("== ${currentBoard == newBoard}")
            println("=== ${currentBoard === newBoard}")
            board.value = board.value.copy(board=newBoard.board)
            println(moveString);println(board.value.board.actionState)
        }
    }

    fun newGame(){
        board.value = MongoDbBoard(BoardClass(),dbPair.second,dbPair.first);listOfBoard.value.add(board.value.board.toString())
    }

    fun open(): (String) -> Boolean = {
        newGame()
        board.value=board.value.open(it)
        if(board.value.board.currentgame_state == "open"){
            displayState.value = "play"
            true
        }
        else false
    }

    fun join(): (String) -> Boolean = {
        newGame()
        board.value=board.value.join(it)
        if(board.value.board.currentgame_state == "open"){
            displayState.value = "play"
            true
        }
        else false
    }

    MaterialTheme {
        Row {
            when(displayState.value){
                "welcome" -> welcomeDisplay(join(), open())
                "play" -> {
                    Row{
                        buildBoard(board.value.board.toString(), buildCoordinate())
                        buildButtons(board.value, makeMove())
                        if(board.value.board.actionState == Commands.PROMOTE){
                            promotePawn(
                                board = board.value,
                                onPieceChosen = {
                                    println("i am here")
                                    println(it)
                                    println(moveString)
                                    board.value= board.value.overidePiece(it, moveString.value[3], moveString.value[4])
                                    moveString.value = ""
                                },
                                onCancel = onCancel()
                            )
                        }
                        if(board.value.board.actionState == Commands.WIN){
                            winDisplay(board.value, onCancel()) { displayState.value = "review" }
                        }
                    }
                }
                "forfeit" -> {
                    Row{
                        buildBoard(board.value.board.toString(), buildCoordinate())
                        buildButtons(board.value, makeMove())
                        if(board.value.board.actionState == Commands.PROMOTE){
                            promotePawn(
                                board = board.value,
                                onPieceChosen = {
                                    /*board.value.overidePiece(it, moveString.value[3], moveString.value[4])*/
                                    moveString.value = "" },
                                onCancel = onCancel()
                            )
                        }
                        if(board.value.board.actionState == Commands.WIN){
                            winDisplay(board.value, onCancel()) { displayState.value = "review" }
                        }
                        exitPanel("Do you want to forfeit the game",onCancel()){board.value = board.value.forfeit()}

                    }
                }
                "review" -> buildReview(listOfBoard.value, onCancel())
            }
        }
    }
}
