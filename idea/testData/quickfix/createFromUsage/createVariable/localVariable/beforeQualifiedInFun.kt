// "Create local variable 'foo'" "false"
// ACTION: Create property 'foo' from usage
// ACTION: Split property declaration
// ERROR: Unresolved reference: foo

class A

fun test(a: A) {
    val t: Int = a.<caret>foo
}