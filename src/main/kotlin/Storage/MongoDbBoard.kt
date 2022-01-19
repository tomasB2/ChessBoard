package Storage

import domane.*

data class MongoDbBoard(var board:BoardClass,
                        val dbOperations: DbOperations,
                        val dbMode: DbMode){

    fun open(id:String?):MongoDbBoard{
      //  try {
            return if(id == null || dbOperations.read("open",id)!=null) return this
            else {
                dbOperations.post("open", GameState(id,""))
                dbOperations.post("currentgames", GameState(id,"  "))
                        copy(board=board.copy(currentGameid = id,team = Team.WHITE,currentgame_state = "open"))
            }
      /*  }catch (e:BoardAccessException){
            throw  BoardAccessException(e)
        }*/
    }
    fun join(id:String?):MongoDbBoard{
        //try{
            return if(id != null && dbOperations.read("open",id)!=null) {
                /*board.currentGameid = id
                board.myTeam=if(dbMode==DbMode.LOCAL)Team.WHITE else Team.BLACK
                board.currentgame_state = "currentgames"*/
                copy(board.copy(team=if(dbMode==DbMode.LOCAL)Team.WHITE else Team.BLACK,currentGameid = id,
                    currentgame_state = "currentgames"))

            }
            else this
      /*  }catch (e:BoardAccessException){
            throw BoardAccessException(e)
        }*/
    }
    fun refresh():MongoDbBoard{
        if (dbMode==DbMode.LOCAL) {
            return copy(board=board.copy(actionState = Commands.INVALID))
        }
        if (board.currentGameid.isEmpty()){
            return copy(board.copy(actionState = Commands.INVALID))
        }
        val a = dbOperations.read(board.currentgame_state,board.currentGameid)!!.movement
        val string = a.split(" ")
        if (string[string.lastIndex-1].isEmpty())return this
        println(string)
        var newpiece=string[string.lastIndex-1][0]
        val lessString= if (string[string.lastIndex-1].length==6){
            newpiece= string[string.lastIndex-1][5]
                string[string.lastIndex-1].dropLast(1)
            }
        else string[string.lastIndex-1]
        val b = sanitiseString(lessString,board) ?: return copy(board=board.copy(actionState = Commands.INVALID))
        val newboard= board.makeMove(b,callFunc.REFRESH)
        println("piece"+ newpiece )
        return copy(board=newboard).overidePiece(newpiece,lessString[1],lessString[2]).addToGameString(b,callFunc.REFRESH,if (newpiece==lessString[0])null else newpiece)

    }

}
fun MongoDbBoard.addToGameString(move: Move, func: callFunc,piece: Char?):MongoDbBoard {//override toString Move
    var newboard = board
    if (board.actionState == Commands.WIN) newboard = board.copy(turn = board.turn.next())
    if (func == callFunc.PLAY) {
        if (piece == null) {
            newboard = newboard.copy(
                currentGame_String = "${board.currentGame_String} ${move.piece}${'a'.plus(move.from.x)}${8 - move.from.y}" +
                        "${'a'.plus(move.to.x)}${8 - move.to.y} ",
                currentgame_state = "currentgames",
                firstMove = false
            )
            dbOperations.put(
                newboard.currentgame_state,
                GameState(newboard.currentGameid, newboard.currentGame_String)
            )
        } else {
            newboard = newboard.copy(
                currentGame_String = "${board.currentGame_String} ${move.piece}${'a'.plus(move.from.x)}${8 - move.from.y}" +
                        "${'a'.plus(move.to.x)}${8 - move.to.y}$piece ",
                currentgame_state = "currentgames",
                firstMove = false
            )
            dbOperations.put(
                newboard.currentgame_state,
                GameState(newboard.currentGameid, newboard.currentGame_String)
            )
        }
    }
     else {
        if (piece == null) {
            newboard = newboard.copy(
                currentGame_String = newboard.currentGame_String +
                        "${move.piece}${'a'.plus(move.from.x)}${8 - move.from.y}${'a'.plus(move.to.x)}${8 - move.to.y} "
            )
        }
       else{
            newboard = newboard.copy(
                currentGame_String = newboard.currentGame_String +
                        "${move.piece}${'a'.plus(move.from.x)}${8 - move.from.y}${'a'.plus(move.to.x)}${8 - move.to.y}$piece "
            )
        }
        println(newboard.currentGame_String + " na addTogameString")
    }
    return this.copy(board = newboard)
}
fun MongoDbBoard.makeMove(move: Move, callFunc: callFunc) :MongoDbBoard{
    val newBoard=this.board.makeMove(move,callFunc)
    println(newBoard.turn)
    if (newBoard!=this.board){
        return this.copy(board=newBoard).addToGameString(move,callFunc,null)
    }
    else return this
}
fun MongoDbBoard.overidePiece(piece: Char, atX: Char, atY: Char):MongoDbBoard{
    if(board.actionState == Commands.PROMOTE){
        println(atX.code- 97)
        println(8 - (atY.code-48))
        val newboard = board.copy(actionState = Commands.VALID)
        newboard.alterpieceat(atIndex(atX.code - 97, 8 - (atY.code-48)),
            Piece(piece,board.turn,false, mutableSetOf()))
        //board.updatemoves
        return copy(board = newboard)
    }
    return this
}
fun MongoDbBoard.forfeit():MongoDbBoard{
    val newBoard = board.copy(actionState = Commands.WIN)
    dbOperations.put(board.currentGame_String,GameState(board.currentGameid,"f "));
    return copy(board = newBoard)
}