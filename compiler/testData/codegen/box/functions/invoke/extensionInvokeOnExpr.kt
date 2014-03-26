class A

class B {
    fun A.invoke() = "##"
    fun A.invoke(i: Int) = "#${i}"
}

fun foo() = A()

fun B.test(): String {
    if (A()() != "##") return "fail1"
    if (A()(1) != "#1") return "fail2"
    if (foo()() != "##") return "fail3"
    if (foo()(42) != "#42") return "fail4"
    if ((foo())(42) != "#42") return "fail5"
    if ({() -> A()}()() != "##") return "fail6"
    if ({() -> A()}()(37) != "#37") return "fail7"
    return "OK"
}

fun box(): String = B().test()