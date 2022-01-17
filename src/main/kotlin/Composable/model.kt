package Composables

data class Coordinate(val x:Char, val y:Int){
    override fun toString(): String {
        return ""+x+y
    }
}

fun pieceToImage(c:Char):String?{
    return when(c){
        'R' -> "tower_w.png"
        'r' -> "tower_b.png"
        'K' -> "king_w.png"
        'k' -> "king_b.png"
        'Q' -> "queen_w.png"
        'q' -> "queen_b.png"
        'N' -> "knight_w.png"
        'n' -> "knight_b.png"
        'B' -> "bishop_w.png"
        'b' -> "bishop_b.png"
        'P' -> "pawn_w.png"
        'p' -> "pawn_b.png"
        else -> null
    }
}
