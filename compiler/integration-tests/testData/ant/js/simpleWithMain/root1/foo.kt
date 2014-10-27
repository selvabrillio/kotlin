package foo

var ok = "FAIL"

fun main(args: Array<String>) {
    ok = "OK"
    println("Hello, Ant!")
}

fun box(): String = ok
