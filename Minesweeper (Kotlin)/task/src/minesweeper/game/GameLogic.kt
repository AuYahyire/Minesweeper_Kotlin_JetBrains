package minesweeper.game

import minesweeper.data.FieldState
import minesweeper.data.TABLE_HEIGHT_Y
import minesweeper.data.TABLE_WIDTH_X
import minesweeper.game.utils.DisplayTable
import minesweeper.game.utils.GameGridUpdater

/**
 * The `GameLogic` class manages the execution of the minesweeper game.
 * It interacts with the game state and table to facilitate gameplay.
 *
 * @property gameState The current state of the game, including the values on the table.
 * @property gameTable The representation of the game table.
 */
class GameLogic(private val gameState: GameState, private val gameTable: GameTable) {

    /**
     * Executes the minesweeper game loop, allowing the player to set/delete mine marks
     * until all mines are found or the game is ended.
     */
    fun executeGame() {
        var gameEnded = false
        game@do {
            println("Set/unset mines marks or claim a cell as free:")
            val coordinates = readln().split(' ').toList()
            if (coordinates[0] == "admin") {
                adminMode()
                continue@game
            }
            val x = coordinates[0].toInt() - 1
            val y = coordinates[1].toInt() - 1
            val command = coordinates[2]

            val position = checkPosition(x, y)
            val updateGrid = updateGridAndReturnBoolean(x, y, position, command)

            if (updateGrid) {
                do {
                    GameGridUpdater(gameState).checkWrongMinesCount()
                    val impossible = GameGridUpdater(gameState).searchDot()
                } while (impossible)
                DisplayTable(gameState).printTable()
            }

            checkGameStatus()
        } while (gameState.status == GameState.PlayerStatus.PLAYING)
    }

    private fun adminMode() {
        do {
            println("Action (print values, print player, print minesCount, count displayed mines):")
            val action = readln()
            when (action) {
                "print values" -> DisplayTable(gameState, gameState.valuesTable).printTable()
                "print player" -> DisplayTable(gameState).printTable()
                "print minesCount" -> println(gameState.numberOfMines)
                "count displayed mines" -> println(gameState.valuesTable.flatten().count { it == 'X' })
            }
        } while (action != "exit")
    }

    /**
     * Checks the game status to determine if the game has ended.
     *
     * @return `true` if the game has ended, `false` otherwise.
     */
    private fun checkGameStatus() {
        val gridWithMines = gameState.valuesTable
        val gridWithFlags = gameState.playersTable

        // Verifica si todas las minas están marcadas con banderas
        for (i in gridWithMines.indices) {
            for (j in gridWithMines[i].indices) {
                val isMine = gridWithMines[i][j] == 'X'
                val isFlagged = gridWithFlags[i][j] == '*'

                if (isMine && !isFlagged) {
                    // Si alguna mina no está marcada con bandera, el juego no ha terminado
                    return
                }
            }
        }

        // Verifica si no hay celdas sin revelar que no son minas
        for (i in gridWithMines.indices) {
            for (j in gridWithMines[i].indices) {
                val isMine = gridWithMines[i][j] == 'X'
                val isNotRevealed = gridWithFlags[i][j] == '.'

                if (!isMine && isNotRevealed) {
                    // Si hay una celda sin revelar que no es una mina, el juego no ha terminado
                    return
                }
            }
        }

        // Si llegamos a este punto, el jugador ha ganado
        println("Congratulations! You found all the mines!")
        gameState.status = GameState.PlayerStatus.WIN
    }





    /**
     * Checks the position of the given coordinates on the game table.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The state of the field at the specified coordinates.
     * @throws IllegalArgumentException if the cell value is invalid.
     */
    private fun checkPosition(x: Int, y: Int): FieldState {
        // Check the valuesTable first
        return when (gameState.valuesTable[y][x]) {
            'X' -> FieldState.IS_MINE
            in '0'..'9' -> FieldState.IS_NUMBER
            else -> {
                // If valuesTable doesn't match, check the playersTable
                when (gameState.playersTable[y][x]) {
                    '.' -> FieldState.NOT_REVEALED
                    '*' -> FieldState.FLAGGED
                    '/' -> FieldState.MARKED_FREE
                    else -> throw IllegalArgumentException("Invalid cell value")
                }
            }
        }
    }



    /**
     * Updates the game grid based on the specified coordinates and field state.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param state The state of the field at the specified coordinates.
     * @return `true` if the grid was updated, `false` otherwise.
     * @throws IllegalArgumentException if the field state is invalid.
     */
    private fun updateGridAndReturnBoolean(x: Int, y: Int, state: FieldState, command: String): Boolean {
        return when {
            state == FieldState.NOT_REVEALED && command == "mine" -> {
                gameState.playersTable[y][x] = '*'
                true
            }

            state == FieldState.NOT_REVEALED && command == "free" -> {
                GameGridUpdater(gameState).revealCells(x,y,false,command)
                true
            }

            state == FieldState.FLAGGED -> {
                GameGridUpdater(gameState).revealCells(x,y,false,command)
                true
            }

            state == FieldState.IS_NUMBER -> {
                if (gameState.playersTable[y][x] == '.') {
                    println("There is a number here!")
                }
                GameGridUpdater(gameState).revealCells(x,y,false,command)
                true
            }

            state == FieldState.MARKED_FREE -> {
                println("This cell is already free!")
                true
            }

            state == FieldState.IS_MINE && command == "free" -> {
                println("You stepped on a mine and failed!")
                cellIsMine()
                gameState.status = GameState.PlayerStatus.LOSE
                true
            }

            state == FieldState.IS_MINE && command == "mine" -> {
                GameGridUpdater(gameState).revealCells(x,y,false,command)
                true
            }

            else -> throw IllegalArgumentException("Invalid FieldState: $state")
        }
    }

    private fun cellIsMine(): FieldState {
        val mine = 'X'
        GameGridUpdater(gameState).iterateGrid {
            y, x -> if(gameState.valuesTable[y][x] == mine) {
                gameState.playersTable[y][x] = mine
        }
        }
        return FieldState.IS_MINE
    }
}
