// "Create property 'foo' from usage" "true"
// ERROR: Property must be initialized or be abstract

object A {
    val foo: Int

}

fun test() {
    val a: Int = A.foo
}
