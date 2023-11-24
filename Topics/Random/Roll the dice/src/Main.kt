import kotlin.random.Random

fun throwD6(): Int {
    val numberOnTheDice = Random.nextInt(1, 7)
	return numberOnTheDice
}
