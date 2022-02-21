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
            return copy(board = board.copy(actionState = Commands.INVALID))
        }
        val read = dbOperations.read(board.currentgame_state,board.currentGameid)!!.movement
        val string = read.split(" ")
        if (string[string.lastIndex-1].isEmpty()) return this

        val stringFromBoard = board.currentGame_String.split(" ")
        string.forEach {
            println("this is it ->"+it)
            if(it == "f")
                return copy(board = board.copy(actionState = Commands.WIN, turn = board.team))
        }

        val lessString = string[string.lastIndex-1]
        if(lessString.length == 6){
            if(lessString == stringFromBoard[stringFromBoard.size-2]) return this
        }
        val sanitized = sanitiseString(lessString,board) ?: return this
        println("this is the move1 -> $lessString")
        println("sanitizeeee -> $sanitized")
        val newboard= board.makeMove(sanitized,callFunc.REFRESH,dbMode)
        return copy(newboard)
    }

}
fun MongoDbBoard.addToGameString(move: Move, func: callFunc,piece: Char?):MongoDbBoard {
    var newboard = board
    if (piece != null && piece == 'f') {
        newboard = newboard.copy(
            currentGame_String = "${board.currentGame_String} f ",
            currentgame_state = "currentgames",
            actionState = Commands.WIN,
            turn = board.team.next()
        )
        dbOperations.put(
            newboard.currentgame_state,
            GameState(newboard.currentGameid, newboard.currentGame_String)
        )
        return this.copy(board = newboard)
    }
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
    }
    return this.copy(board = newboard)
}
fun MongoDbBoard.makeMove(move: Move, callFunc: callFunc) :MongoDbBoard{
    //shit incoming
    val read = dbOperations.read(board.currentgame_state,board.currentGameid)!!.movement
    val string = read.split(" ")
    string.forEach {
        if(it == "f"){
            return copy(board = board.copy(turn = board.team.next()))
        }
    }
    //shit end
    val newBoard = this.board.makeMove(move,callFunc,dbMode)

    if (newBoard != this.board){
        if(newBoard.actionState != Commands.PROMOTE && newBoard.actionState != Commands.INVALID) {
            return this.copy(board = newBoard).addToGameString(move, callFunc, null)
        }
        else return this.copy(board = newBoard)
    }
    else return this
}
fun MongoDbBoard.overidePiece(piece: Char, atX: Char, atY: Char, move:String? = null):MongoDbBoard{
    if(board.actionState == Commands.PROMOTE){
        println("this is the move -> $move")
        val newboard = board.copy(actionState = Commands.VALID, turn = board.turn.next(), currentGame_String = if(move != null) board.currentGame_String + "$move " else board.currentGame_String)
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