import kotlin.random.Random

fun generatePredictablePassword(seed: Int): String {
    var randomPassword = ""
    val random = Random(seed)
    for (i in 1..10) {
        randomPassword += random.nextInt(33, 127).toChar()
    }

	return randomPassword
}