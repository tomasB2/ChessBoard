package Storage


import domane.*
import java.util.*


/**
 * enum class that holds the values of the called functions for future conditions
 */
enum class callFunc{
    REFRESH,
    PLAY
}
fun atIndex(x:Int,y:Int):Int{
    return x + numberOfCol * y
}
const val numberOfCol=8
/*class MongoDbBoard(private val db: DbOperations,private val dbInfo:DbMode): Board{
    private var mapDeMovesWhite:MutableSet<Move>  = HashSet()
    private var mapDeMovesBlack:MutableSet<Move> = HashSet()
    private val mapPositionsWhite: MutableSet<Pos> = HashSet()
    private val mapPositionsBlack: MutableSet<Pos> = HashSet()
    private val arrayzito = arrayOfNulls<Pos>(64)

    fun prepareArrayzito(a:Array<Pos?>){
        var i=0
        for (line in 0..7){
            for (colum in 0..7){
                a[i++]=Pos(colum,line)
            }
        }
    }

    /**
     * Val that creates the matrix used in the Board
     */
    private val linkedlistOfPieces:LinkedList<Piece?> = LinkedList()

    private var whiteKingPos=Pos(4,7)
    private var blackKingPos=Pos(4,0)
    /**
     * Val that contains the pieces to place
     */
    private val pieceChar: Array<Char> = arrayOf('r','n','b','q','k','b','n','r')

    /**
     * holds the current player turn
     */
    override var turn:Team = Team.WHITE
    /**
     * holds the current game id
     */
    var currentGameid=""

    /**
     * list of moves
     */
    var currentGame_String=""

    /**
     * Team associated to opening or joining game
     */
    override lateinit var myTeam:Team;

    /**
     * indicates the first move of the game
     */
    var firstmove=true

    /**
     * variable that holds the game_state
     * Possible values [open], [currentgames], [closedgames]
     */
    var currentgame_state=""

    override var actionState = Commands.INVALID


    /**
     * Function that initializes the Board
     */
    init {
        prepareArrayzito(arrayzito)
        placePieces()
        updatePieceMoves(mapDeMovesWhite,mapDeMovesBlack, mapPositionsWhite,mapPositionsBlack)
        linkedlistOfPieces.forEach{if(it!=null)println(it.mapDosMoves)}
        println(mapDeMovesBlack)
        println(mapDeMovesWhite)
        println(mapPositionsBlack)
        println(mapPositionsWhite)
    }

override fun CheckMapDeMoves(pos:Pos,team:Team)=if(team== Team.WHITE)(pos in mapPositionsBlack) else (pos in mapPositionsWhite)

fun updatePieceMoves(white:MutableSet<Move>,black:MutableSet<Move>,moves:MutableSet<Pos>,movesBlack:MutableSet<Pos>){
    var i =0
    var j=63
    while (j>=i){

        if (linkedlistOfPieces[i]!=null) {
            linkedlistOfPieces[i]!!.mapDosMoves=
                checkValidMoves(linkedlistOfPieces[i]!!.piece, linkedlistOfPieces[i]!!.team, arrayzito[i]!!, this,white, black,moves,movesBlack)
        }
        if (linkedlistOfPieces[j]!=null) {
            linkedlistOfPieces[j]!!.mapDosMoves=
                checkValidMoves(linkedlistOfPieces[j]!!.piece, linkedlistOfPieces[j]!!.team, arrayzito[j]!!, this,white, black,moves,movesBlack)
        }
        i++
        j--
        actualKingMoves(whiteKingPos,Team.WHITE,linkedlistOfPieces[atIndex(getWhiteKing().x,getWhiteKing().y)]!!.mapDosMoves,white,black,moves, movesBlack)
        actualKingMoves(blackKingPos,Team.BLACK,linkedlistOfPieces[atIndex(getBlackKing().x,getBlackKing().y)]!!.mapDosMoves,white,black,moves, movesBlack)
        actualKingMoves(whiteKingPos,Team.WHITE,linkedlistOfPieces[atIndex(getWhiteKing().x,getWhiteKing().y)]!!.mapDosMoves,white,black,moves, movesBlack)
    }



    /*if(i>= sizeOfObject) return true
        if (linkedlistOfPieces[i]!=null){
           linkedlistOfPieces[i]!!.mapDosMoves=checkValidMoves(linkedlistOfPieces[i]!!.piece,linkedlistOfPieces[i]!!.team, arrayzito[i]!!,this)
        }  return (updatePieceMoves(2*i+1) && updatePieceMoves(2*i+2))*/
    }
    /**
     * Function that places the pieces on the board
     */
    private fun placePieces(){
        for(i in 0..7){
            val curr = linkedlistOfPieces
            curr.add(Piece(pieceChar[i].toLowerCase(),Team.BLACK))
        }
        for(i in 8..15){
            val curr = linkedlistOfPieces
            curr.add(Piece('p',Team.BLACK, true))
        }
        for (i in 16..47){
            val curr = linkedlistOfPieces
            curr.add(null)
        }
        for (i in 48..55 ){
            val curr = linkedlistOfPieces
            curr.add(Piece('P',Team.WHITE, true))
        }
        for (i in 56.. 63){
            val curr = linkedlistOfPieces
            curr.add(Piece(pieceChar[i-56].toUpperCase(),Team.WHITE))
        }

    }
    override fun getWhiteKing()= whiteKingPos
    override fun getBlackKing()=blackKingPos
    override fun alterKingPos(pos:Pos, team: Team)= if (team==Team.WHITE)whiteKingPos=pos else blackKingPos=pos
    /**
     * Function that adds move to the global list of moves
     * @param move, the move to be added
     */
    private fun addToGameString(move:Move,func: callFunc){
        if(actionState != Commands.WIN) turn= turn.next()
        if(firstmove==true){
            currentGame_String += "${move.piece}${'a'.plus(move.from.x)}${8-move.from.y}${'a'.plus(move.to.x)}${8-move.to.y} "
            firstmove=false
            currentgame_state="currentgames"
            if(func==callFunc.PLAY){
                db.post(currentgame_state,GameState(currentGameid,currentGame_String))
            }

        }
        else {currentGame_String += "${move.piece}${'a'.plus(move.from.x)}${8-move.from.y}${'a'.plus(move.to.x)}${8-move.to.y} "
            db.put(currentgame_state,GameState(currentGameid,currentGame_String))}
    }

    /**
     * Override of the function toString
     * @return The current state of the Board in a String format
     */

    override fun toString(): String {
        var string = ""
        for(i in 0 .. 7){
            for(j in 0 .. 7){
                string+=linkedlistOfPieces[atIndex(j,i)]?.piece ?: ' '
            }
        }
        return string
    }

    override fun makeMove(move: Move,func: callFunc): Board {
        val toMove:Piece? = linkedlistOfPieces[atIndex(move.from.x,move.from.y)]
        if(toMove == null) {
            actionState = Commands.INVALID
            return this
        }
        val ret = checkConditionValidate(move,toMove,func)
        actionState = ret
        if(ret == Commands.INVALID) return this
        if(toMove.fristmove && (toMove.piece=='P' || toMove.piece=='p'))toMove.fristmove=false
        if(func==callFunc.REFRESH && turn==myTeam) return this
        if(this.linkedlistOfPieces[atIndex(move.to.x,move.to.y)]?.piece == 'K' ||
            this.linkedlistOfPieces[atIndex(move.to.x,move.to.y)]?.piece == 'k'){
            try{
                actionState = Commands.WIN
                addToGameString(move,func)
                return this
            }catch (e:BoardAccessException){
                throw BoardAccessException(e)
            }
        }

        this.linkedlistOfPieces[atIndex(move.from.x,move.from.y)] = null
        this.linkedlistOfPieces[atIndex(move.to.x,move.to.y)] = toMove
        if (this.linkedlistOfPieces[atIndex(move.to.x,move.to.y)]!!.piece in "Kk")
            alterKingPos(Pos(move.to.x,move.to.y),this.linkedlistOfPieces[atIndex(move.to.x,move.to.y)]!!.team)
        if(toMove.piece.toUpperCase() == 'P' && (move.to.y == 0 || move.to.y == 7)) actionState = Commands.PROMOTE//mudar
        try {
            addToGameString(move,func)//put
        }catch (e:BoardAccessException){
            throw BoardAccessException(e)
        }
        return this
    }

    /**
     * Function that validates conditions for the command to be valid
     * @param move the move
     * @param toMove the piece to move
     * @return if the [Result] is valid or not
     */
    private fun checkConditionValidate(move: Move, toMove:Piece,func: callFunc): Commands {

        if (func!=callFunc.REFRESH) {
            if (move.from == move.to)
                return Commands.INVALID
            if (turn != getPieceAt(atIndex(move.from.x, move.from.y))!!.team)
                return Commands.INVALID
            if (getPieceAt(atIndex(move.to.x, move.to.y))!=null && getPieceAt(atIndex(move.to.x, move.to.y))?.team == getPieceAt(atIndex(move.to.x, move.to.y))!!.team)
                return Commands.INVALID
            if (pieceMoves(move.piece, toMove.team, move.from, move.to, this)== Commands.INVALID)
                return Commands.INVALID

        }
        return Commands.VALID
    }

    override fun getPieceAt(i:Int): Piece? = this.linkedlistOfPieces[i]

    override fun open(id:String?):Commands{
         try {
             return if(id == null || db.read("open",id)!=null) return Commands.INVALID
             else {
                 db.post("open", GameState(id,""))
                 currentGameid = id
                 myTeam=Team.WHITE
                 currentgame_state = "open"
                 Commands.VALID
             }
         }catch (e:BoardAccessException){
             throw  BoardAccessException(e)
         }
    }

    override fun join(id:String?):Commands{
        try{
            return if(id != null && db.read("open",id)!=null) {
                currentGameid = id
                myTeam=if(dbInfo==DbMode.LOCAL)Team.WHITE else Team.BLACK
                currentgame_state = "currentgames"
                Commands.VALID
            }
            else Commands.INVALID
        }catch (e:BoardAccessException){
            throw BoardAccessException(e)
        }
    }

    override fun refresh():Board{
        if (dbInfo==DbMode.LOCAL) {
            this.actionState = Commands.INVALID
            return this
        }
        if (currentGameid.isEmpty()){
            this.actionState = Commands.INVALID
            return this
        }

        val a = db.read(currentgame_state,currentGameid)!!.movement
        val string = a.split(" ")
        val b = sanitiseString(string[string.size-2],this)
        if(b == null){
            this.actionState = Commands.INVALID
            return this
        }
        this.makeMove(b,callFunc.REFRESH)
        return this
    }

    override fun hasJoined():Boolean{
        return currentGameid.isNotEmpty()
    }

    override fun moves() = currentGame_String

}
*/
fun prepareArrayzito(): Array<Pos?> {
    val a= arrayOfNulls<Pos>(64)
    var i=0
    for (line in 0..7){
        for (colum in 0..7){
            a[i++]=Pos(colum,line)
        }
    }
    return a
}
fun placePieces():LinkedList<Piece?>{
    val pieceChar: Array<Char> = arrayOf('r','n','b','q','k','b','n','r')
    val linkedlistOfPieces= LinkedList<Piece?>()
    for(i in 0..7){
        val curr = linkedlistOfPieces
        curr.add(Piece(pieceChar[i].toLowerCase(),Team.BLACK))
    }
    for(i in 8..15){
        val curr = linkedlistOfPieces
        curr.add(Piece('p',Team.BLACK, true))
    }
    for (i in 16..47){
        val curr = linkedlistOfPieces
        curr.add(null)
    }
    for (i in 48..55 ){
        val curr = linkedlistOfPieces
        curr.add(Piece('P',Team.WHITE, true))
    }
    for (i in 56.. 63){
        val curr = linkedlistOfPieces
        curr.add(Piece(pieceChar[i-56].toUpperCase(),Team.WHITE))
    }
    return linkedlistOfPieces
}
data class BoardClass internal constructor(
    private val linkedlistOfPieces: LinkedList<Piece?> = placePieces(),
    val turn: Team= Team.WHITE,
    val team: Team= Team.WHITE,
    val mapPositionsWhite: MutableSet<Pos> = HashSet(),
    val mapPositionsBlack: MutableSet<Pos> = HashSet(),
    val arrayzito:Array<Pos?> = prepareArrayzito(),
    var whiteKingPos: Pos =Pos(4,7),
    var blackKingPos: Pos =Pos(4,0),
    val currentgame_state:String="",
    val currentGameid:String="",
    var currentGame_String:String="",
    val firstMove: Boolean= true,
    val actionState:Commands = Commands.INVALID


){
    companion object {
        operator fun invoke() = BoardClass()
    }
    private var mapDeMovesWhite:MutableSet<Move>  = HashSet()
    private var mapDeMovesBlack:MutableSet<Move> = HashSet()

    override fun toString(): String {
        var string = ""
        for(i in 0 .. 7){
            for(j in 0 .. 7){
                string+=linkedlistOfPieces[atIndex(j,i)]?.piece ?: ' '
            }
        }
        return string
    }

    /**
     * Val that contains the pieces to place
     */


    /**
     * holds the current game id
     */


    /**
     * list of moves
     */

    /**
     * Team associated to opening or joining game
     */

    /**
     * indicates the first move of the game
     */


    /**
     * variable that holds the game_state
     * Possible values [open], [currentgames], [closedgames]
     */


    fun getWhiteKing()= whiteKingPos
    fun getBlackKing()=blackKingPos
    fun alterKingPos(pos:Pos, team: Team)= if (team==Team.WHITE)whiteKingPos=pos else blackKingPos=pos
    fun CheckMapDeMoves(pos:Pos,team:Team)=if(team== Team.WHITE)(pos in mapPositionsBlack) else (pos in mapPositionsWhite)
    fun getPieceAt(i:Int): Piece? = this.linkedlistOfPieces[i]
    fun moves() = currentGame_String

  /*  fun updatePieceMoves(white:MutableSet<Move>,black:MutableSet<Move>,moves:MutableSet<Pos>,movesBlack:MutableSet<Pos>) {
        var i = 0
        var j = 63
        while (j >= i) {

            if (linkedlistOfPieces[i] != null) {
                linkedlistOfPieces[i]!!.mapDosMoves =
                    checkValidMoves(
                        linkedlistOfPieces[i]!!.piece,
                        linkedlistOfPieces[i]!!.team,
                        arrayzito[i]!!,
                        this,
                        white,
                        black,
                        moves,
                        movesBlack
                    )
            }
            if (linkedlistOfPieces[j] != null) {
                linkedlistOfPieces[j]!!.mapDosMoves =
                    checkValidMoves(
                        linkedlistOfPieces[j]!!.piece,
                        linkedlistOfPieces[j]!!.team,
                        arrayzito[j]!!,
                        this,
                        white,
                        black,
                        moves,
                        movesBlack
                    )
            }
            i++
            j--
            actualKingMoves(
                whiteKingPos,
                Team.WHITE,
                linkedlistOfPieces[atIndex(getWhiteKing().x, getWhiteKing().y)]!!.mapDosMoves,
                white,
                black,
                moves,
                movesBlack
            )
            actualKingMoves(
                blackKingPos,
                Team.BLACK,
                linkedlistOfPieces[atIndex(getBlackKing().x, getBlackKing().y)]!!.mapDosMoves,
                white,
                black,
                moves,
                movesBlack
            )
            actualKingMoves(
                whiteKingPos,
                Team.WHITE,
                linkedlistOfPieces[atIndex(getWhiteKing().x, getWhiteKing().y)]!!.mapDosMoves,
                white,
                black,
                moves,
                movesBlack
            )
        }
    }

*/

    fun checkConditionValidate(move: Move, toMove:Piece,func: callFunc): Commands {

        if (func!=callFunc.REFRESH) {
            if (move.from == move.to)
                return Commands.INVALID
            if (turn != getPieceAt(atIndex(move.from.x, move.from.y))!!.team)
                return Commands.INVALID
            if (getPieceAt(atIndex(move.to.x, move.to.y))!=null && getPieceAt(atIndex(move.from.x, move.from.y))?.team == getPieceAt(atIndex(move.to.x, move.to.y))!!.team)
                return Commands.INVALID
            if (pieceMoves(move.piece, toMove.team, move.from, move.to, this)== Commands.INVALID)
                return Commands.INVALID

        }
        return Commands.VALID
    }


    fun hasJoined():Boolean{
        return currentGameid.isNotEmpty()
    }
    fun alterpieceat(i: Int,value:Piece?) {
        linkedlistOfPieces[i]=value
    }

}
// i think i found the secret sauce.
// Pode ser que assim tinhamos acesso a um objeto igual ao inicial e a ele ligado
//so tinhamos de tornar o make move numa função externa para tentar
fun BoardClass.makeMove(move: Move, func: callFunc): BoardClass {
    val newboard= this
    val toMove:Piece? = newboard.getPieceAt(atIndex(move.from.x,move.from.y))
    if(toMove == null) {
        return copy(actionState = Commands.INVALID)
    }
    println("estou na boardclass"+ newboard.turn+ " "+ toMove)
    if (toMove.team!=newboard.turn) return this
    val ret = checkConditionValidate(move,toMove,func)
    if(ret == Commands.INVALID) return newboard.copy(actionState=Commands.INVALID)
    if(func==callFunc.REFRESH && turn==newboard.team) return this
    if(toMove.fristmove && (toMove.piece=='P' || toMove.piece=='p'))toMove.fristmove=false
    if(newboard.getPieceAt(atIndex(move.to.x,move.to.y))?.piece == 'K' ||
        newboard.getPieceAt(atIndex(move.to.x,move.to.y))?.piece == 'k'){
        //try{
            //fazer addToGameString se for valido no handler de eventos
            // TODO: 10/01/2022
            return newboard.copy(actionState = Commands.WIN)
       /* }catch (e:BoardAccessException){
            throw BoardAccessException(e)
        }*/
    }
    newboard.alterpieceat(atIndex(move.from.x,move.from.y),null)
    newboard.alterpieceat(atIndex(move.to.x,move.to.y),toMove)
    if (newboard.getPieceAt(atIndex(move.to.x,move.to.y))!!.piece in "Kk")
        alterKingPos(Pos(move.to.x,move.to.y),newboard.getPieceAt(atIndex(move.to.x,move.to.y))!!.team)
    if(toMove.piece.toUpperCase() == 'P' && (move.to.y == 0 || move.to.y == 7)) return newboard.copy(actionState = Commands.PROMOTE)
   // try {
        //addToGameString(move,func)//alterar do mesmo modo que o outro
  /*  }catch (e:BoardAccessException){
        throw BoardAccessException(e)
    }*/
    return copy(turn = turn.next(), actionState = Commands.VALID)//tá mal Arão
}
