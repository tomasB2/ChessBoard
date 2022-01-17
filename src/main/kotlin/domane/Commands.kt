package domane
import Storage.*

interface CommandInterface{
    /**
     * Executes this command passing it the given parameter
     * @param parameter the commands parameter, or null, if no parameter has been passed
     */
    fun execute(parameter: String?): Any?

    /**
     * Overload of invoke operator, for convenience.
     */
    operator fun invoke(parameter: String? = null) = execute(parameter)

}

class OpenCommand(
    private val board: MongoDbBoard
):CommandInterface{
    override fun execute(parameter: String?) = CommandResult(board.open(parameter))
}

class JoinCommand(
    private val board: MongoDbBoard
):CommandInterface{
    override fun execute(parameter: String?) = CommandResult(board.join(parameter))
}

class PlayCommand(
    private val board: MongoDbBoard

):CommandInterface{
    override fun execute(parameter: String?): Any {
        if (!board.board.hasJoined())return CommandResult(board)
        if(board.board.actionState==Commands.WIN)return CommandResult(board)
        if(parameter == null){
            return CommandResult(board.copy(board=board.board.copy(actionState = Commands.INVALID)))
        }
        if(board.dbMode == DbMode.REMOTE && board.board.team!= board.board.turn ){
            return CommandResult(board.copy(board=board.board.copy(actionState = Commands.INVALID)))
        }
        val sanitized = sanitiseString(parameter,board.board)
        return if(sanitized == null){
            return CommandResult(board.copy(board=board.board.copy(actionState = Commands.INVALID)))
        }
        else {
            CommandResult(board.makeMove(sanitized, callFunc.PLAY))
        }
    }
}

class MoveCommand(
    private val board: MongoDbBoard
):CommandInterface{
    override fun execute(parameter: String?) = CommandResult(board.board.moves())
}

class RefreshCommand(
    private val board: MongoDbBoard
):CommandInterface{
    override fun execute(parameter: String?): CommandResult<MongoDbBoard> {
        if (!board.board.hasJoined())return CommandResult(board)
        return if(board.board.actionState==Commands.WIN) CommandResult(board)
        else CommandResult(board.refresh())
    }
}

class ExitCommand:CommandInterface{
    override fun execute(parameter: String?) = ExitResult
}

class HelpCommand:CommandInterface{
    override fun execute(parameter: String?) = CommandResult(Commands.VALID)
}
