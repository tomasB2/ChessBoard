/*import isel.leic.tds.trab1.*
import org.junit.Test
import kotlin.test.assertEquals

class BoardTests {
    @Test
    fun `Initial position Board`() {
        val sut = Board()
        assertEquals(
            "rnbqkbnr" +
                    "pppppppp" +
                    "        ".repeat(4) +
                    "PPPPPPPP" +
                    "RNBQKBNR", sut.toString()
        )
    }

    @Test
    fun `MakeMove in Board with 'normal' input`() {
        val sut = Board().makeMove("Pe2e4").makeMove("Pe7e5").makeMove("Nb1c3")
        assertEquals(
            "rnbqkbnr" +
                    "pppp ppp" +
                    "        " +
                    "    p   " +
                    "    P   " +
                    "  N     " +
                    "PPPP PPP" +
                    "R BQKBNR", sut.toString()
        )
    }
    @Test
    fun `MakeMove in Board with no piece specified`() {
        val sut = Board().makeMove("e2e4").makeMove("e7e5").makeMove("Nb1c3")
        assertEquals(
            "rnbqkbnr" +
                    "pppp ppp" +
                    "        " +
                    "    p   " +
                    "    P   " +
                    "  N     " +
                    "PPPP PPP" +
                    "R BQKBNR", sut.toString()
        )
    }
    @Test
    fun `Tests invalid string 1`() {
        val sut = Board()
            .makeMove("P98rt")
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
    fun `Tests invalid string 2`() {
        val sut = Board().makeMove("   ")
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
    fun `Tests invalid string 3`() {
        val sut = Board()
            .makeMove("Pq8rt")
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
    fun `Tests if pieces dont go out the board`() {
        val sut = Board().makeMove("Pd7d6")
            .makeMove("Bc8i2")
        assertEquals(
            "rnbqkbnr" +
                    "ppp pppp" +
                    "   p    " +
                    "        " +
                    "        " +
                    "        " +
                    "PPPPPPPP" +
                    "RNBQKBNR", sut.toString()
        )
    }
    @Test
    fun `Tests eating same team does nothing`() {
        val sut = Board().makeMove("Ra1a2")
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
}
*/
