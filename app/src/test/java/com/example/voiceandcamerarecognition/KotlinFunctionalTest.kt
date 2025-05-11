package com.example.voiceandcamerarecognition

import org.junit.Test
import org.junit.Assert.*

/** ===== Delegation Test ===== */
interface SoundMaker {
    fun makeSound(): String
}

class Dog : SoundMaker {
    override fun makeSound(): String {
        return "Bow-wow!"
    }
}

class Cat : SoundMaker {
    override fun makeSound(): String {
        return "Meow!"
    }
}

// SoundMaker の実装を別のオブジェクトに委譲するクラス
class SoundRepeater(
    private val soundMaker: SoundMaker,
    private val times: Int): SoundMaker by soundMaker {

    override fun makeSound(): String {
        val sounds: MutableList<String> = mutableListOf()
        for (i in 0 until times) {
            sounds.add(soundMaker.makeSound())
        }
        return sounds.joinToString("\n")
    }
}
/** ===== Delegation Test ===== */


class KotlinFunctionalTest {
    @Test
    fun deligation_functions() {
        var obj: SoundMaker
        var actual: String

        obj = Dog()
        actual = obj.makeSound()
        assertEquals("Bow-wow!", actual)

        obj = Cat()
        actual = obj.makeSound()
        assertEquals("Meow!", actual)

        obj = SoundRepeater(Dog(), 3)
        actual = obj.makeSound()
        assertEquals("""Bow-wow!
            |Bow-wow!
            |Bow-wow!""".trimMargin(), actual)
    }
}