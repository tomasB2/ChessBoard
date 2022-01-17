package Composables.board

import Composables.Coordinate
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import domane.Team

@Composable
fun buildBoard(boardString: String, buildCoordinate:(String)->Unit) {
    println(boardString)
    val chunks = boardString.chunked(8)
    Column {
        buildBoardRow(Team.WHITE, chunks[0], columnNum = 8, buildCoordinate)
        buildBoardRow(Team.BLACK, chunks[1], columnNum = 7, buildCoordinate)
        buildBoardRow(Team.WHITE, chunks[2], columnNum = 6, buildCoordinate)
        buildBoardRow(Team.BLACK, chunks[3], columnNum = 5, buildCoordinate)
        buildBoardRow(Team.WHITE, chunks[4], columnNum = 4, buildCoordinate)
        buildBoardRow(Team.BLACK, chunks[5], columnNum = 3, buildCoordinate)
        buildBoardRow(Team.WHITE, chunks[6], columnNum = 2, buildCoordinate)
        buildBoardRow(Team.BLACK, chunks[7], columnNum = 1, buildCoordinate)
    }
}

@Composable
private fun buildBoardRow(startingColor: Team, string:String, columnNum: Int, buildCoordinate:(String)->Unit){
    Row{
        tile(startingColor, string[0], Coordinate('a',columnNum), buildCoordinate)
        tile(startingColor.next(), string[1], Coordinate('b',columnNum), buildCoordinate)
        tile(startingColor, string[2], Coordinate('c',columnNum), buildCoordinate)
        tile(startingColor.next(), string[3], Coordinate('d',columnNum), buildCoordinate)
        tile(startingColor, string[4], Coordinate('e',columnNum), buildCoordinate)
        tile(startingColor.next(), string[5], Coordinate('f',columnNum), buildCoordinate)
        tile(startingColor, string[6], Coordinate('g',columnNum), buildCoordinate)
        tile(startingColor.next(), string[7], Coordinate('h',columnNum), buildCoordinate)
    }
}