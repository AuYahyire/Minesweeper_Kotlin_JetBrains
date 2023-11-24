fun main() {
    val alfa = 97
    val sumOf = intArrayOf(20,6,4,11,11,14,6)
    var beta = byteArrayOf()
    for (i in 0..7) {
        beta += if (beta.isEmpty()) alfa.toByte() else (alfa + sumOf[i - 1]).toByte()
    }
    beta.forEach { print(it.toInt().toChar()) }
}
