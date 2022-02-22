import Composables.mainWindow
import Storage.*
import Storage.mongoDB.createMongoClient
import androidx.compose.ui.window.application
import com.mongodb.client.MongoClient


private fun selectPath(dbInfo: DBConnectionInfo):Pair<DbMode, MongoClient>{
    return if (dbInfo.mode == DbMode.REMOTE) Pair(DbMode.REMOTE,createMongoClient(dbInfo.connectionString))
    else Pair(DbMode.LOCAL,createMongoClient())
}
fun main() {
    val dbInfo = getDBConnectionInfo()
    val driver = selectPath(dbInfo)

    driver.second.use {
        val repoInfo = Pair(dbInfo.mode, DbOperations(it.getDatabase(dbInfo.dbName)))
        application {
            mainWindow(repoInfo, ::exitApplication )
        }
    }
}