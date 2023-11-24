package minesweeper

import minesweeper.game.GameLogic
import minesweeper.game.GameState
import minesweeper.game.GameTable

fun main() {
    val gameTable = GameTable()
    GameLogic(gameTable.gameState, gameTable).executeGame()
}
