// ERROR: Unresolved reference: x
class C {
    private val string = getString()

    class object {

        fun getString(): String? {
            return x()
        }
    }
}