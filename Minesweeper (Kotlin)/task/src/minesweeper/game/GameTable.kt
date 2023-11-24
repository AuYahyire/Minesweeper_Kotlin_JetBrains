package minesweeper.game

import minesweeper.data.FIRST_PROMPT
import minesweeper.data.MOVEMENT_PROMPT
import minesweeper.data.TABLE_HEIGHT_Y
import minesweeper.data.TABLE_WIDTH_X
import minesweeper.game.utils.DisplayTable
import minesweeper.game.utils.GameGridUpdater

open class GameTable {
    val gameState = GameState()
    private val gameGridUpdater = GameGridUpdater(gameState)

    init {
        buildTable()
        DisplayTable(gameState).printTable()
    }


    private fun buildTable() {
        print(FIRST_PROMPT)
        gameState.numberOfMines = readlnOrNull()?.toInt() ?: 1

        val coordinates = firstMove()

        val firstX = coordinates[0].toInt() - 1
        val firstY = coordinates[1].toInt() - 1
        val firstCommand = coordinates[2]

        initializeTables(firstX, firstY, firstCommand)

        // Count the number of mines surrounding each cell.
        gameGridUpdater.updateGameGridWithMineCounts()
        gameGridUpdater.checkWrongMinesCount()
        gameGridUpdater.revealCells(firstX,firstY,true, firstCommand)

        do {
            val impossible = gameGridUpdater.searchDot()
        } while (impossible)

    }

    private fun initializeTables(firstX: Int, firstY: Int, firstCommand: String) {
        // Calculate the total number of cells on the game table.
        val totalCells = TABLE_HEIGHT_Y * TABLE_WIDTH_X

        // Create a list of 'X' characters representing mines.
        val mines = List(gameState.numberOfMines) { 'X' }

        // Create a list of '.' characters representing empty cells.
        val emptyCells = List(totalCells - gameState.numberOfMines) { '.' }

        // Combine and shuffle the lists to create a mixed cell layout.
        val mixedCells = (mines + emptyCells).shuffled()

        // Initialize the game's valuesTable as a 2D mutable list.
        repeat(TABLE_HEIGHT_Y) { y ->
            // Create a row to store values.
            gameState.valuesTable.add(mutableListOf())
            repeat(TABLE_WIDTH_X) { x ->
                val mixedCellIndex = y * TABLE_WIDTH_X + x
                val cellValue = mixedCells[mixedCellIndex]
                //If it is not the first cell, add the cell value.
                if (!(x == firstX && y == firstY)) {
                    gameState.valuesTable[y].add(cellValue)
                } else {
                    when (firstCommand) {
                        "free" -> {
                            gameState.valuesTable[y].add('.')
                            gameState.playersTable[y][x] = '.'
                        }
                        else -> {
                            //Default behavior
                            gameState.valuesTable[y].add(cellValue)
                        }
                    }
                }
            }
        }
    }

    private fun firstMove(): List<String> {
        DisplayTable(gameState).printTable()
        println(MOVEMENT_PROMPT)
        return readln().split(' ').toList()
    }




}




