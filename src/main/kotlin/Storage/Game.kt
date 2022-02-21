package Storage

import java.util.*

data class Game(val board: MongoDbBoard,val list:LinkedList<String>,val gamesString: String,)