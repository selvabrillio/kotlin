// "Create function 'foo'" "true"

fun test(): Int {
    return foo<String, Int, Boolean>(2, "2")
}

fun <T, T1, T2> foo(arg: T1, arg1: T): T1 {
    throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
}