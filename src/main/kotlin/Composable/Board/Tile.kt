package Composables.board

import Composables.Coordinate
import Composables.pieceToImage
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import domane.Team

@Composable
fun tile(tileColor: Team, piece: Char, coordinate: Coordinate, buildCoordinate:(String)->Unit){
    val colorRGB =
        if(tileColor == Team.WHITE) Color(0xFF49d6de)
        else Color(0xFFb065e63)
    val image = pieceToImage(piece)
    Box(
        modifier = Modifier.background(colorRGB)
            .size(100.dp)
            .clickable { buildCoordinate(piece + coordinate.toString()) }
    ){
        if(image != null){
            Image(
                painter = painterResource(image),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
        }
    }
}