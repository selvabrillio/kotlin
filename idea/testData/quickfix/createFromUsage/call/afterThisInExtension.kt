// "Create function 'foo' from usage" "true"

class A<T>(val n: T) {
    fun foo(i: Int, s: String): A<Int> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

fun <U> A<U>.test(): A<Int> {
    return this.foo(2, "2")
}