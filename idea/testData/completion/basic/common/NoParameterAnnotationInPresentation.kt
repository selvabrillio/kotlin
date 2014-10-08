fun foo(inlineOptions p: () -> Unit){}

fun bar() {
    <caret>
}

// EXIST: { lookupString:"foo", itemText: "foo(p: () -> Unit)", typeText:"Unit" }
