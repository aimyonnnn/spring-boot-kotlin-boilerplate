package com.example.demo.kotest.utils

import com.example.demo.utils.ConvertUtils.convertLocalDateTimeToStringFormat
import com.example.demo.utils.ConvertUtils.convertStringToLocalDateTimeFormat
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

@ActiveProfiles("test")
@Tags("kotest-unit-test")
class ConvertUtilsTests : BehaviorSpec({

  Given("Convert Given String DateTime to LocalDateTime") {
    val stringDateTime = LocalDateTime.now().withNano(0).toString()

    When("Given Current String datetime & Pattern") {

      val localDateTime = convertStringToLocalDateTimeFormat(
        stringDateTime,
        "yyyy-MM-dd'T'HH:mm:ss"
      )

      Then("String Datetime to LocalDatetime") {
        localDateTime::class.java shouldBeSameInstanceAs (LocalDateTime::class.java)
      }
    }

    When("Given Current String datetime & Wrong Pattern") {

      shouldThrowExactly<IllegalArgumentException> {
        convertStringToLocalDateTimeFormat(
          stringDateTime,
          "wrong pattern"
        )
      }
    }

    When("Given Wrong String datetime & Current Pattern") {

      shouldThrowExactly<DateTimeParseException> {
        convertStringToLocalDateTimeFormat(
          "",
          "yyyy-MM-dd'T'HH:mm:ss"
        )
      }
    }
  }

  Given("Convert Given LocalDateTime to String DateTime") {

    When("Given Current LocalDateTime & Pattern") {
      val stringDateTime = convertLocalDateTimeToStringFormat(
        LocalDateTime.now().withNano(0),
        "yyyy-MM-dd'T'HH:mm:ss"
      )

      Then("LocalDatetime to String Datetime") {
        stringDateTime::class.java shouldBeSameInstanceAs String::class.java
      }
    }

    When("Given Current LocalDateTime & Wrong Pattern") {

      shouldThrowExactly<IllegalArgumentException> {
        convertLocalDateTimeToStringFormat(
          LocalDateTime.now(),
          "wrong pattern"
        )
      }
    }
  }
})
