package Composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import domane.Team
import Storage.MongoDbBoard
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp


@Composable
fun winDisplay(board: MongoDbBoard, onCancel: () -> Unit, ReviewGame: () -> Unit) {
    Dialog(
        onCloseRequest = onCancel,
        title = "Game Ended",
        resizable = false,
        state = rememberDialogState(size = DpSize(Dp.Unspecified, Dp.Unspecified))
    ){
        val name = remember { mutableStateOf(value = "") }
        Column(modifier = Modifier.padding(16.dp)){
            Text(text = "${board.board.turn} wins the game!", modifier = Modifier.padding(16.dp))
            Row{
                Button(onClick = ReviewGame, modifier = Modifier.padding(16.dp)){
                    Text("Review")
                }
                Button(onClick = onCancel, modifier = Modifier.padding(16.dp)){
                    Text("Exit")
                }
            }
        }
    }
}

@Composable
fun promotePawn(board: MongoDbBoard, onPieceChosen: (Char)->Unit, onCancel: () -> Unit){
    Dialog(
        onCloseRequest = onCancel,
        title = "Promotion",
        resizable = false,
        state = rememberDialogState(size = DpSize(Dp.Unspecified, Dp.Unspecified))
    ){
        val pieceChosen = remember { mutableStateOf(value = ' ') }
        val colorRGB = Color(0xFFe5b635)
        fun pieceTeamMatch(s:Char):Char{
            return if(board.board.team== Team.BLACK) s
            else s.toUpperCase()
        }
        fun maybeOnNameEntered(){
            onPieceChosen(if(board.board.team== Team.BLACK)pieceChosen.value else pieceChosen.value.toUpperCase())

        }
        Column(modifier = Modifier.padding(16.dp)){
            Text(text = "Chose the piece", modifier = Modifier.padding(16.dp))
            Row{
                Box(
                    modifier = Modifier.background(colorRGB)
                        .size(100.dp)
                        .clickable {
                            pieceChosen.value = 'q'
                            maybeOnNameEntered()
                        }
                ){
                    Image(
                        painter = painterResource(pieceToImage(pieceTeamMatch('q'))!!),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                }
                Box(
                    modifier = Modifier.background(colorRGB)
                        .size(100.dp)
                        .clickable {
                            pieceChosen.value = 'r'
                            maybeOnNameEntered()
                        }
                ){
                    Image(
                        painter = painterResource(pieceToImage(pieceTeamMatch('r'))!!),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                }
                Box(
                    modifier = Modifier.background(colorRGB)
                        .size(100.dp)
                        .clickable {
                            pieceChosen.value = 'n'
                            maybeOnNameEntered()
                        }
                ){
                    Image(
                        painter = painterResource(pieceToImage(pieceTeamMatch('n'))!!),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                }
                Box(
                    modifier = Modifier.background(colorRGB)
                        .size(100.dp)
                        .clickable {
                            pieceChosen.value = 'b'
                            maybeOnNameEntered()
                        }
                ){
                    Image(
                        painter = painterResource(pieceToImage(pieceTeamMatch('b'))!!),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun getGameName(onNameEntered: (String)->Unit, onCancel: () -> Unit){
    Dialog(
        onCloseRequest = onCancel,
        title = "Game Name",
        resizable = false,
        state = rememberDialogState(size = DpSize(Dp.Unspecified, Dp.Unspecified))
    ){
        val name = remember { mutableStateOf(value = "") }
        fun maybeOnNameEntered(){
            onNameEntered(name.value)
        }
        Column(modifier = Modifier.padding(16.dp)){
            Text(text = "Enter game name", modifier = Modifier.padding(16.dp))
            Row{
                TextField(
                    singleLine = true,
                    value = name.value,
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Gray),
                    onValueChange = { name.value = it }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = ::maybeOnNameEntered){
                    Text("OK")
                }
            }
        }
    }
}