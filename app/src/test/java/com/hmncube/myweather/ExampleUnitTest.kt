package com.hmncube.myweather

import org.junit.Test

import org.junit.Assert.*
import java.util.Date

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val dateOg = "2022-12-12T05:00"
        val dateTime = dateOg.split("T")
        val date = dateTime[0].split("-")
        val time = dateTime[1].split(":")
        val weatherDate = Date(date[2].toInt(), date[1].toInt(),
            date[0].toInt(), time[0].toInt(), 0)
        val b = Date().after(weatherDate)
        assertEquals(true, b)
    }
}