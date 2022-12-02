package converter

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import kotlin.math.pow

// Do not delete this line

fun main() {

    loop1@ while (true) {
        println("Enter two numbers in format: {source base} {target base} (To quit type /exit)")
        when (val answer1 = readln()){
            "/exit" -> break
            else -> {
                loop2@ while (true) {
                    val (sourceBase, targetBase) = answer1.split(" ")
                    println("Enter number in base $sourceBase to convert to base $targetBase (To go back type /back)")
                    when (val answer2 = readln().uppercase()) {
                        "/BACK" -> continue@loop1
                        else ->  println("Conversion result: ${answer2.toDecimal(sourceBase.toInt()).toBase(targetBase.toInt())}")
                    }
                }
            }
        }
    }
}

fun String.toDecimal(base: Int): BigDecimal {
    val parts = this.split(".")
    var fractionPart = ""
    try {
        fractionPart = parts[1]
    } catch (exc: IndexOutOfBoundsException) {
        //do nothing
    }
    return parts.first().reversed()
        .mapIndexed { i, c -> c.fromHex() * base.toDouble().pow(i.toDouble()) }.sum().toBigDecimal().setScale(0) +
           fractionPart.mapIndexed { i, c -> c.fromHex() * base.toDouble().pow((-i-1).toDouble()) }.sum().toBigDecimal().setScale(fractionPart.length, RoundingMode.HALF_UP)
}

fun Char.fromHex() = if (this.isDigit()) this.digitToInt() else (this.code - 55)
fun Int.inHex() = if (this > 9) (this + 55).toChar().toString() else this.toString()

fun BigDecimal.toBase(base: Int): String {
    var intPart = this.toBigInteger()
    var fractionPart = this % BigDecimal.ONE
    var outIntPart = ""

    while (intPart > 0.toBigInteger()) {
        val remnant = intPart % base.toBigInteger()
        intPart /=  base.toBigInteger()
        outIntPart += remnant.toInt().inHex()
    }
    outIntPart = outIntPart.reversed()

    if (fractionPart != 0.toBigDecimal()) {
        outIntPart += '.'
        var outFractionPart = ""
        fractionPart = fractionPart.setScale(5, RoundingMode.HALF_UP)

        repeat(5) {
            fractionPart *= base.toBigDecimal()
            outFractionPart += fractionPart.toString().split(".").first().toInt().inHex()
            fractionPart %= 1.toBigDecimal()
        }
        outIntPart += outFractionPart
    }

    return outIntPart.ifEmpty { "${BigInteger.ZERO}" }
}