package minesweeper.game.utils

import minesweeper.data.FieldState
import minesweeper.data.TABLE_HEIGHT_Y
import minesweeper.data.TABLE_WIDTH_X
import minesweeper.game.GameState

class GameGridUpdater(gameState: GameState) {
    private val gameGrid = gameState.valuesTable
    private val playerGrid = gameState.playersTable


    fun iterateGrid(action: (Int, Int) -> Unit) {
        repeat(TABLE_HEIGHT_Y) { rowIndexY ->
            repeat(TABLE_WIDTH_X) { columnIndexX ->
                action(rowIndexY, columnIndexX)
            }
        }
    }

    /**
     * Counts the number of mines surrounding each cell and updates the game grid with appropriate values.
     */
    fun updateGameGridWithMineCounts() {
        val rowOffsetsY = listOf(-1, -1, -1, 0, 0, 1, 1, 1)
        val columnOffsetsX = listOf(-1, 0, 1, -1, 1, -1, 0, 1)
        // Iterate through each cell in the game grid.
        iterateGrid { rowIndexY, columnIndexX ->
            // Skip cells marked with '/'.
            if (gameGrid[rowIndexY][columnIndexX] != 'X') {

                var minesCount = 0

                // Check neighboring cells in 8 directions.
                for (directionIndex in 0 until 8) {
                    val neighborRowIndex = rowIndexY + rowOffsetsY[directionIndex]
                    val neighborColumnIndex = columnIndexX + columnOffsetsX[directionIndex]

                    // Ensure the neighbor cell is within bounds and contains a mine ('X').
                    if (neighborRowIndex in 0 until gameGrid.size && neighborColumnIndex in 0 until gameGrid[rowIndexY].size && gameGrid[neighborRowIndex][neighborColumnIndex] == 'X') {
                        minesCount++
                    }
                }

                // Update the current cell with the count of neighboring mines.
                if (minesCount != 0) {
                    gameGrid[rowIndexY][columnIndexX] = minesCount.digitToChar()
                    if (playerGrid[rowIndexY][columnIndexX] == '/') {
                        playerGrid[rowIndexY][columnIndexX] = minesCount.digitToChar()
                    }
                }

            }
        }
    }

    fun checkWrongMinesCount() {
        iterateGrid {
            y, x ->
            if (playerGrid[y][x] == '/' && gameGrid[y][x].isDigit()) {
                playerGrid[y][x] = gameGrid[y][x]
            }
        }
    }

    fun searchDot() : Boolean {
        repeat(TABLE_HEIGHT_Y) { y ->
            repeat(TABLE_WIDTH_X) { x ->
                if (playerGrid[y][x] == '.') {
                    if (hasSlashNeighbor(y, x)) {
                        revealCells(x, y, false, "free")
                        return true
                    }
                }
            }
        }
        return false
    }


    private fun hasSlashNeighbor(row: Int, col: Int): Boolean {
        val directions = arrayOf(-1, 0, 1)

        for (i in directions) {
            for (j in directions) {
                if (i == 0 && j == 0) continue // Skip the current cell
                val newRow = row + i
                val newCol = col + j

                // Check if the new position is within the bounds of the grid
                if (newRow in 0 until TABLE_HEIGHT_Y && newCol in 0 until TABLE_WIDTH_X) {
                    if (playerGrid[newRow][newCol] == '/') {
                        return true
                    }
                }
            }
        }
        return false
    }




    fun revealCells(x: Int, y: Int, firstMove: Boolean, command: String): FieldState {

        fun cellIsInRange(x: Int, y: Int): Boolean {
            return x in 0 until TABLE_WIDTH_X && y in 0 until TABLE_HEIGHT_Y
        }


        fun typeOfCell(x: Int, y: Int): FieldState {
            if (cellIsInRange(x, y)) {
                return when (playerGrid[y][x]) {
                    '.' -> FieldState.NOT_REVEALED
                    '*' -> FieldState.FLAGGED
                    '/' -> FieldState.MARKED_FREE
                    else -> {
                        // Check the valuesTable if none of the above conditions match
                        when (gameGrid[y][x]) {
                            'X' -> FieldState.IS_MINE
                            in '0'..'9' -> FieldState.IS_NUMBER
                            else -> throw IllegalArgumentException("Invalid cell value")
                        }
                    }
                }
            }
            // If the cell is out of bounds, return the corresponding FieldState
            return FieldState.OUT_OF_BOUNDS
        }


        fun cellIsNotRevealed(): FieldState {
            // Initialize a set to store the next cells to be processed
            val nextCells = mutableSetOf(Pair(y, x))

            // Continue processing cells until the set is empty
            while (nextCells.isNotEmpty()) {
                // Get the coordinates of the first cell in the set
                val (nextY, nextX) = nextCells.first()

                if (command == "free") {
                    // If the cell is not a mine, update playerGrid with the revealed value
                    if (gameGrid[nextY][nextX] != 'X') {
                        playerGrid[nextY][nextX] =
                            if (gameGrid[nextY][nextX] == '.' || playerGrid[nextY][nextX] == '*') '/' else gameGrid[nextY][nextX]

                        // If the revealed cell has no adjacent mines, continue revealing neighbors
                        if (gameGrid[nextY][nextX] == '.' && typeOfCell(nextY, nextX) != FieldState.IS_NUMBER) {
                            // Store the next 8-cardinal points to verify the next cell
                            for (i in -1..1) {
                                for (j in -1..1) {
                                    // Skip the cell itself
                                    if (i == 0 && j == 0) continue

                                    // Determine the coordinates of the next cell to be processed
                                    val newY = nextY + i
                                    val newX = nextX + j

                                    // Check if the new cell is within the game grid boundaries
                                    if (cellIsInRange(
                                            newY,
                                            newX
                                        ) && (playerGrid[newY][newX] == '.' || playerGrid[newY][newX] == '*') && gameGrid[newY][newX] != 'X'
                                    ) {
                                        // Add only not revealed and not mine cells to the set
                                        nextCells.add(Pair(newY, newX))
                                    }
                                }
                            }
                        }
                    }

                } else {
                    // If the command is not "free," mark the cell with '*'
                    playerGrid[nextY][nextX] = '*'
                }

                // Remove the processed cell from the set
                nextCells.remove(nextCells.first())
            }


            // Once all relevant cells are processed, return the state as NOT_REVEALED
            return FieldState.NOT_REVEALED
        }


        fun cellIsFlag(): FieldState {
            playerGrid[y][x] = '.'
            return FieldState.FLAGGED
        }

        fun cellIsNumber(): FieldState {
            playerGrid[y][x] = gameGrid[y][x]
            return FieldState.IS_NUMBER
        }

        fun cellIsMarkedFree(firstMove: Boolean): FieldState {
            return if (firstMove) {
                gameGrid[y][x] = '.'
                cellIsNotRevealed()
                FieldState.MARKED_FREE
            } else {
                FieldState.MARKED_FREE
            }
        }

        fun cellIsMine(): FieldState {
            val mine = 'X'
            playerGrid.mapIndexed { y, row ->
                row.mapIndexed { x, currentValue ->
                    if (y < TABLE_HEIGHT_Y && x < TABLE_WIDTH_X && gameGrid[y][x] == mine) mine else currentValue
                }
            }
            return FieldState.IS_MINE
        }

        return when (typeOfCell(x, y)) {
            FieldState.NOT_REVEALED -> cellIsNotRevealed()
            FieldState.IS_NUMBER -> cellIsNumber()
            FieldState.FLAGGED -> cellIsFlag()
            FieldState.IS_MINE -> cellIsMine()
            FieldState.MARKED_FREE -> cellIsMarkedFree(firstMove)
            FieldState.OUT_OF_BOUNDS -> FieldState.OUT_OF_BOUNDS
        }

    }

}