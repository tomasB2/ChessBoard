package domane


import Storage.BoardClass
import Storage.atIndex

val piecesSet:HashSet<Char> = hashSetOf('P','R','N','Q','K','B')
val columns:Array<Char> = arrayOf('a','b','c','d','e','f','g','h')


/**
 * Function that sanitises the command given
 * @return null or the sanitized command
 */
fun sanitiseString(string: String?,board: BoardClass): Move? {
    if(string != null) println("decoding -> $string and length -> ${string.length}")
    return if (string == null || string.length in 7..8) null
    else decodeMove(string.split(""), board)
}

/**
 * Function that decodes the command given
 * @param s(sanitized command)
 * @return The command properly separated
 */
fun decodeMove(s: List<String>, board: BoardClass): Move? {
    println("decode -> $s")
    return when (s.lastIndex) {
        7 -> {//commando promote
            println(8 - s[3].toInt() - 6)
            println(8 - s[3].toInt())
            Move(
                piece = s[1][0].toUpperCase(),
                from = Pos(s[2][0] - 'a', if(board.turn == Team.WHITE) 1 else 6),
                to = Pos(s[4][0] - 'a', if(board.turn == Team.WHITE)0 else 7),
                newPiece = s[6][0]
            )
        }
        6 -> {//commando completo
            if (((s[1][0].toUpperCase() !in piecesSet ||
                        s[4][0] !in columns) ||
                        (s[3] !in "0123456789" || s[5] !in "0123456789") ||
                        !isValid(s[1][0].toUpperCase(), s[2][0] - 'a', 8 - s[3].toInt(), board))
            ) null
            else Move(
                piece = s[1][0].toUpperCase(),
                from = Pos(s[2][0] - 'a', 8 - s[3].toInt()),
                to = Pos(s[4][0] - 'a', 8 - s[5].toInt())
            )
        }
        5 -> {//commando onde peça é omitida
            if (s[2] !in "0123456789" || s[4] !in "0123456789" ||
                !isValid('P', s[1][0] - 'a', 8 - s[2].toInt(), board)
            ) null
            else Move(
                piece = 'P',
                from = Pos(s[1][0] - 'a', 8 - s[2].toInt()),
                to = Pos(s[3][0] - 'a', 8 - s[4].toInt())
            )
        }
        else -> null
    }
}

/**
 * Function that validates the move command passed
 * @param piece the piece in question
 * @param columnPos the x coordinate of the piece to move
 * @param linePos the y coordinate of the piece to move
 * @return If the Action is valid or not
 */
private fun isValid(piece: Char, columnPos: Int, linePos: Int, board: BoardClass): Boolean {
    return (columnPos in 0..7 && linePos in 0..7 &&
            board.getPieceAt(atIndex(columnPos, linePos)) != null
            && piecesSet.contains(piece.toUpperCase())
            && (board.getPieceAt(atIndex(columnPos, linePos))!!.piece == piece ||
            board.getPieceAt(atIndex(columnPos, linePos))!!.piece.toUpperCase() == piece))
}