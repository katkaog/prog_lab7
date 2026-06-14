package id

object IdGenerator {
    private var nextId: Int = 1

    fun generate(): Int {
        return nextId++
    }

    fun updateId(maxId: Int) {
        if (maxId >= nextId) {
            nextId = maxId + 1
        }
    }
    fun reset() {
        nextId = 1
    }
}