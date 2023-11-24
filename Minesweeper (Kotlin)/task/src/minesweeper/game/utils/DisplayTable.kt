package minesweeper.game.utils

import minesweeper.data.TABLE_HEIGHT_Y
import minesweeper.data.TABLE_WIDTH_X
import minesweeper.game.GameState
import minesweeper.game.GameTable

class DisplayTable(gameState: GameState) {
    private var table = gameState.playersTable

    /**
     * Displays the table using data from gameState's valuesTable.
     *
     * The table is printed row by row.
     */
    fun printTable() {
        println(" │123456789│")
        println("—│—————————│")
        repeat(TABLE_HEIGHT_Y) {
            // Print each row
            println("${it + 1}│${arrangeRowElements(it, table)}│")
        }
        println("—│—————————│")
    }


    /**
     * Arranges and returns the elements of a given row from the gameState's valuesTable as a formatted string.
     *
     * @param row The row index to be arranged.
     * @return A formatted string containing the elements of the specified row separated by spaces.
     */
    private fun arrangeRowElements(row: Int, table: MutableList<MutableList<Char>>): String {
        val rowElements = StringBuilder()
        repeat(TABLE_WIDTH_X) { i ->
            // Append each element of the row with a space separator.
            rowElements.append("${table[row][i]}")
        }
        return rowElements.toString()
    }

    constructor(gameState: GameState, _table: MutableList<MutableList<Char>>) : this(gameState) {
        table = _table
    }
}