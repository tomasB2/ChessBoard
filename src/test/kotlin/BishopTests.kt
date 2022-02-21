/*
import Storage.BoardClass
import Storage.makeMove
import domane.Move
import domane.*
import Storage.*
import Composable.*
import junit.framework.Assert.assertEquals
import org.junit.Test



class BishopTests {
    /* @Test
    fun `Bishop moves front right front left back right back left`() {
        val sut = BoardClass().makeMove("Pb2b4").makeMove("Pg2g4").makeMove("Pb7b5").makeMove("Pg7g5")
            .makeMove("Bc8a6")
            .makeMove("Bf8h6")
            .makeMove("Bc1a3")
            .makeMove("Bf1h3")
        assertEquals(
            "rn qk nr" +
                    "p pppp p" +
                    "b      b" +
                    " p    p " +
                    " P    P " +
                    "B      B" +
                    "P PPPP P" +
                    "RN QK NR", sut.toString()
        )
    }*/
    @Test
    fun `Bishop eats front right front left`() {
        val sut = BoardClass().makeMove(
            Move(
                'P',
                Pos('b'.code - 'a'.code, '2'.toInt()),
                Pos('b'.code - 'a'.code, '3'.toInt())
            ), callFunc.PLAY, dbMode = DbMode.LOCAL
        ).makeMove(
            Move('P', Pos('g'.code - 'a'.code, '2'.toInt()), Pos('g'.code - 'a'.code, '3'.toInt())),
            callFunc.PLAY,
            dbMode = DbMode.LOCAL
        )
        assertEquals(
            "rnbqkbnr" +
                    "pppppppp" +
                    "        " +
                    "        " +
                    "        " +
                    " P      " +
                    "P PPPPPP" +
                    "RNBQKBNR", sut.toString()
        )
    }
}
    @Test
    fun `Bishop eats back right back left`() {
        val sut = Board().makeMove("Pb7b6").makeMove("Pg7g6")
            .makeMove("Bc8a6").makeMove("Ba6e2")
            .makeMove("Bf8h6").makeMove("Bh6d2")
        assertEquals(
            "rn qk nr" +
                    "p pppp p" +
                    " p    p " +
                    "        " +
                    "        " +
                    "        " +
                    "PPPbbPPP" +
                    "RNBQKBNR", sut.toString()
        )
    }
    @Test
    fun `Bishop cant moves past teammates`() {
        val sut = Board()
            .makeMove("Bc8a6")
        assertEquals(
            "rnbqkbnr" +
                    "pppppppp" +
                    "        " +
                    "        " +
                    "        " +
                    "        " +
                    "PPPPPPPP" +
                    "RNBQKBNR", sut.toString()
        )
    }
    @Test
    fun `Bishop cant moves past enemys`() {
        val sut = Board().makeMove("Pb7b6").makeMove("Pb7b6").makeMove("Pd2d3").makeMove("Pe2e3")
            .makeMove("Bc8a6").makeMove("Bc8e2")
        assertEquals(
            "rn qkbnr" +
                    "p pppppp" +
                    "bp      " +
                    "        " +
                    "        " +
                    "   PP   " +
                    "PPP  PPP" +
                    "RNBQKBNR", sut.toString()
        )
    }
}*/
