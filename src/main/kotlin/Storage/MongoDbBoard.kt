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
                /* board.currentGameid = id
                 board.myTeam= Team.WHITE
                 board.currentgame_state = "open"*/
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
        val b = sanitiseString(string[string.size-2],board)
        if(b == null){
            copy(board=board.copy(actionState = Commands.INVALID))
            return this
        }
        val newboard= board.makeMove(b,callFunc.REFRESH)
        return copy(board=newboard)
    }

}
fun MongoDbBoard.addToGameString(move: Move, func: callFunc):MongoDbBoard{
    var alface=board
    if(board.actionState == Commands.WIN)  alface= board.copy(turn = board.turn.next())
    if(alface.firstMove==true){
        /*board.currentGame_String += "${move.piece}${'a'.plus(move.from.x)}${8-move.from.y}${'a'.plus(move.to.x)}${8-move.to.y} "
        board.firstmove=false
        board.currentgame_state="currentgames"*/
        alface=alface.copy(currentGame_String= alface.currentGame_String+"${move.piece}${'a'.plus(move.from.x)}${8-move.from.y}" +
                "${'a'.plus(move.to.x)}${8-move.to.y}",currentgame_state = "currentgames",firstMove = false)

        if(func==callFunc.PLAY){
            dbOperations.post(alface.currentgame_state,GameState(alface.currentGameid,alface.currentGame_String))
        }

    }
    else {alface= alface.copy(
        currentgame_state = alface.currentGame_String +
                "${move.piece}${'a'.plus(move.from.x)}${8-move.from.y}${'a'.plus(move.to.x)}${8-move.to.y} "
        ,turn = alface.turn)
        dbOperations.put(alface.currentgame_state,GameState(alface.currentGameid,alface.currentGame_String))}
    println(alface.currentGame_String+" "+alface.turn + " "+ alface.currentgame_state)
    return this.copy(board=alface)
}
fun MongoDbBoard.makeMove(move: Move, callFunc: callFunc) :MongoDbBoard{
    val newBoard=this.board.makeMove(move,callFunc)
    println(newBoard.turn)
    if (newBoard!=this.board){
        return   this.copy(board=newBoard).addToGameString(move,callFunc)


    }
    else return this
}