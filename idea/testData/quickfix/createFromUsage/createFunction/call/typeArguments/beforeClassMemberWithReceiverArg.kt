// "Create function 'foo' from usage" "true"

class B<T>(val t: T) {

}

class A<T>(val b: B<T>) {
    fun test(): Int {
        return b.<caret>foo<T, Int, String>(2, "2")
    }
}