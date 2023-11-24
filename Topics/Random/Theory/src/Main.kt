import kotlin.random.Random

// You can experiment here, it won’t be checked

fun main(args: Array<String>) {
  print(rpgDices(5,5))
}

fun rpgDices(dice1: Int, dice2: Int): Int {

    return Random.nextInt(dice1) + Random.nextInt(dice2)
}
