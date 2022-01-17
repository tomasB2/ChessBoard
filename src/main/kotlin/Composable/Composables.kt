package Composable

import Storage.MongoDbBoard
import Composables.board.buildBoard
import Composables.getGameName
import Storage.callFunc
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun welcomeDisplay(Join: (String) -> Boolean, Open: (String) -> Boolean) {
    val dialogs = remember { mutableStateOf(value = "" ) }

    fun openNameEntered(): (String) -> Unit = {
        if(Open(it)) dialogs.value = ""
    }
    fun joinNameEntered(): (String) -> Unit = {
        if(Join(it))dialogs.value = ""
    }
    fun onCancelDialogs(): () -> Unit = {
        dialogs.value = ""
    }

    Column(modifier = Modifier.padding(15.dp)){
        Text(text = "Welcome to Ultimate Chess", modifier = Modifier.padding(16.dp))
        Row{
            Button(
                onClick = {
                    dialogs.value = "open"
                },
                modifier = Modifier.padding(16.dp)
            ){
                Text("Open a Game")
            }
            Button(
                onClick = {
                    dialogs.value = "join"
                },
                modifier = Modifier.padding(16.dp)
            ){
                Text("Join a Game")
            }
            if(dialogs.value != ""){
                if(dialogs.value == "open")getGameName(openNameEntered(), onCancelDialogs())
                else getGameName(joinNameEntered(), onCancelDialogs())
            }
        }
    }
}

@Composable
fun buildReview(boards: List<String>, onCancel: () -> Unit) {
    val currentIndex= remember{
        mutableStateOf(0)
    }
    val currentBoard = remember { mutableStateOf(value = boards[currentIndex.value]) }
    fun nothing():(String) -> Unit = {
        onCancel()
    }

    Column(modifier = Modifier.padding(16.dp)){
        Text(text = "Game Review", modifier = Modifier.padding(16.dp))
        Row{
            buildBoard(currentBoard.value, nothing())
            Spacer(modifier = Modifier.width(32.dp))

            Button(onClick = {
              if (currentIndex.value>0) {
                currentIndex.value = currentIndex.value - 1
            }
             if((currentIndex.value>=0) && currentBoard.value != boards.first())currentBoard.value = boards[currentIndex.value]
    }) {
     Text("Before")
    }
        Button(onClick = {
            if (currentIndex.value<currentBoard.value.indices.last) {
                currentIndex.value = currentIndex.value +1
            }
            if((currentIndex.value<=currentBoard.value.indices.last) && currentBoard.value != boards.last())currentBoard.value = boards[currentIndex.value]
        println(currentBoard.value.indices.last)
        }) {
         Text("Next")
        }
    }
}
}

@Composable
fun buildButtons(board: MongoDbBoard, boardAction:(callFunc)->Unit) {
Column {
Button(onClick = {
board.refresh()
}) {
Text("Refresh")
}
Button(onClick = {
boardAction(callFunc.PLAY)
}) {
Text("Play")
}
}
}