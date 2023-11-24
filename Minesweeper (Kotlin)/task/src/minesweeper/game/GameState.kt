package minesweeper.game

import minesweeper.data.TABLE_HEIGHT_Y
import minesweeper.data.TABLE_WIDTH_X

data class GameState(
    var status: PlayerStatus = PlayerStatus.PLAYING,
    var numberOfMines: Int = 0,
    var valuesTable: MutableList<MutableList<Char>> = mutableListOf(),
    var valuesTableWithHiddenMines: MutableList<MutableList<Char>> = mutableListOf(),
    var playersTable: MutableList<MutableList<Char>> =
        MutableList(TABLE_HEIGHT_Y)
        {
            MutableList(TABLE_WIDTH_X)
            { '.' }
        }
) {
    enum class PlayerStatus {
        PLAYING, LOSE, WIN
    }
}
