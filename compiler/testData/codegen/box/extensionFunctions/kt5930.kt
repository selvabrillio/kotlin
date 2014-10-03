var s = ""

fun getA(a: Int) : Int {
    s += "O"
    return a
}

fun getB(b : Int.(Int)->String): Int.(Int)->String {
    s+= "K"
    return b
}

fun box() : String {
    val result = getA(1).(getB({ s += "END"; s }))(1)
    return if (result != "OKEND") "fail $result" else "OK"
}

