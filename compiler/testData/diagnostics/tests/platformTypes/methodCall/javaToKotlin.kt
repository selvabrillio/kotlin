// !DIAGNOSTICS: -UNUSED_PARAMETER
// FILE: p/J.java

package p;

public class J {
}

// FILE: k.kt

import p.*

fun takeJ(j: J) {}

fun test() {
    takeJ(J())
}