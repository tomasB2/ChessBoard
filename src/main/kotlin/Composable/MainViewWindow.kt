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
import domane.Move
import domane.Pos
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

    val moveString = remember{ mutableStateOf("")}
    val listOfBoard = remember{mutableStateOf(LinkedList<String>())}

    val listIndex = remember { mutableStateOf(value = 0) }

    fun addToList(board: String) { listOfBoard.value.add(board) }
    LaunchedEffect(
        board.value.board.actionState!=Commands.PROMOTE
                && board.value.board.actionState!=Commands.INVALID
                && board.value.board.turn!=board.value.board.team
                && board.value.dbMode==DbMode.REMOTE
                && displayState.value == "play"
    ){
        println("Launching effect")
        println(board.value.board.turn)
        println(board.value.board.team)
        println(board.value.board.actionState)
        while (true) {
            println(board.value.board.currentGame_String)
            if (board.value.board.actionState!=Commands.PROMOTE
                && board.value.board.turn!=board.value.board.team
                && board.value.dbMode==DbMode.REMOTE
                && board.value.board.actionState != Commands.WIN
            ) {
                val newBoard = board.value.refresh()
                if(newBoard != board.value){
                    board.value = newBoard
                    addToList(board.value.board.toString())
                    println("refresh -> ${board.value.board.turn}")
                }
            }
            delay(1_000)
        }
    }



    fun makeMove(callFunc: callFunc) {
        if (moveString.value.length == 5) {
            val currentBoard = board.value
            println(currentBoard.board.toString() + " ${board.value.hashCode()}")
            val a= decodeMove(moveString.value.split(""),currentBoard.board)
            val newBoard = currentBoard.makeMove(a!!,callFunc)
            println("play -> ${board.value.board.turn}")
            println(newBoard.board.toString() + " ${board.value.hashCode()}")
            if(newBoard.board.actionState != Commands.INVALID){
                if(newBoard.board.actionState != Commands.INVALID) addToList(board.value.board.toString())
                if(newBoard.board.actionState != Commands.PROMOTE) moveString.value = ""
            }
            println("--------- makeMove Window ----------")
            println("== ${currentBoard == newBoard}")
            println("=== ${currentBoard === newBoard}")
            board.value = board.value.copy(board=newBoard.board)
            println(moveString)
            println(board.value.board.actionState)
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
                if(moveString.value[1] != it[1] || moveString.value[2] != it[2]){
                    moveString.value =moveString.value+ it[1] + it[2]
                    makeMove(callFunc.PLAY)
                }
            }
        }
    }

    fun onCancel():()->Unit={
        displayState.value = "welcome"
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
        if(board.value.board.currentgame_state == "currentgames"){
            displayState.value = "play"
            true
        }
        else false
    }

    fun forfeit(){
        println("aaaaa")
        board.value= board.value.addToGameString(
            Move('P', Pos(1,1),
                Pos(1,1)
            ),
            callFunc.PLAY,
            piece = 'f'
        )
    }

    MaterialTheme {
        Row {
            when(displayState.value){
                "welcome" -> welcomeDisplay(join(), open())
                "play" -> {
                    Row{
                        buildBoard(board.value.board.toString(), buildCoordinate())
                        buildButtons(board.value, ::forfeit)
                        if(board.value.board.actionState == Commands.PROMOTE){
                            promotePawn(
                                board = board.value,
                                onPieceChosen = {
                                    board.value= board.value.overidePiece(it, moveString.value[3], moveString.value[4], moveString.value).addToGameString(
                                        Move(moveString.value[0], Pos(moveString.value[1].code-97,moveString.value[2].code-48),
                                            Pos(moveString.value[3].code-97,moveString.value[4].code-48)
                                        ),callFunc.PLAY,it
                                    )
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
                "review" -> buildReview(listOfBoard.value, onCancel())
            }
        }
    }
}