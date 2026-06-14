package myapp

import collection.*
import commands.*
import id.IdGenerator
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.time.ZonedDateTime

class DragonAppTest {

    @BeforeEach
    fun setUp() {
        IdGenerator.reset()
    }

    @Test
    fun `Coordinates should throw exception if X is less than -5`() {
        assertThrows<IllegalArgumentException> {
            Coordinates(-10.0, 5L)
        }
    }

    @Test
    fun `IdGenerator should increment IDs correctly`() {
        IdGenerator.reset()
        val first = IdGenerator.generate()
        val second = IdGenerator.generate()
        assertEquals(first + 1, second)
        assertEquals(1, first)
    }

    @Test
    fun `Dragons should be compared by age`() {
        val d1 = createTestDragon(id = 1, age = 10)
        val d2 = createTestDragon(id = 2, age = 50)
        assertTrue(d1 < d2)
    }

    @Test
    fun `CollectionManager should add dragon successfully`() {
        val printer = mockk<ConsolePrinter>(relaxed = true)
        val manager = CollectionManager(printer)
        val dragon = createTestDragon(1, 10)

        manager.add(dragon)
        assertEquals(1, manager.getCollection().size)
    }

    @Test
    fun `CollectionManager clear should empty the deque`() {
        val printer = mockk<ConsolePrinter>(relaxed = true)
        val manager = CollectionManager(printer)
        manager.add(createTestDragon(1, 10))

        manager.clear()
        assertTrue(manager.getCollection().isEmpty())
    }

    @Test
    fun `RemoveById should return true if dragon existed`() {
        val printer = mockk<ConsolePrinter>(relaxed = true)
        val manager = CollectionManager(printer)
        manager.add(createTestDragon(5, 20))

        val result = manager.removeById(5)
        assertTrue(result)
        assertEquals(0, manager.getCollection().size)
    }

    @Test
    fun `AddIfMin should not add dragon if its age is greater than min`() {
        val printer = mockk<ConsolePrinter>(relaxed = true)
        val manager = CollectionManager(printer)
        manager.add(createTestDragon(1, 10))

        val olderDragon = createTestDragon(2, 50)
        val added = manager.addIfMin(olderDragon)

        assertFalse(added)
        assertEquals(1, manager.getCollection().size)
    }

    @Test
    fun `RemoveByIdCommand should print error if ID is not a number`() {
        val manager = mockk<CollectionManager>(relaxed = true)
        val printer = mockk<ConsolePrinter>(relaxed = true)
        val command = RemoveByIdCommand(manager, printer)

        val result = command.execute(listOf("abc"), null)

        assertEquals(false, result)
        verify { printer.printError(any()) }
    }

    @Test
    fun `AddCommand should call manager add when executed`() {
        val manager = mockk<CollectionManager>(relaxed = true)
        val printer = mockk<ConsolePrinter>(relaxed = true)

        val command = AddCommand(manager, printer)
        val testDragon = createTestDragon(1, 10)

        val result = command.execute(emptyList(), testDragon)

        assertTrue(result)
        verify { manager.add(any()) }
    }

    @Test
    fun `CollectionManager getById should return null for non-existing ID`() {
        val printer = mockk<ConsolePrinter>(relaxed = true)
        val manager = CollectionManager(printer)

        assertNull(manager.getById(999))
    }

    private fun createTestDragon(id: Int, age: Int): Dragon {
        return Dragon(
            id, "Test", Coordinates(0.0, 0L), ZonedDateTime.now(),
            age, "Desc", 100, null, null
        )
    }
}