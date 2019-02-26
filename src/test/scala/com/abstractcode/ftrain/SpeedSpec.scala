package com.abstractcode.ftrain

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{Inside, Matchers, WordSpec}

class SpeedSpec extends WordSpec with Matchers with TableDrivenPropertyChecks with Inside {
  private val validSpeeds = Table(
    ("steps", "speed"),
    (SpeedSteps28, 0),
    (SpeedSteps28, 1),
    (SpeedSteps28, 28),
    (SpeedSteps128, 0),
    (SpeedSteps128, 1),
    (SpeedSteps128, 126)
  )

  private val invalidSpeeds = Table(
    ("steps", "speed"),
    (SpeedSteps28, Int.MinValue),
    (SpeedSteps28, -128),
    (SpeedSteps28, -1),
    (SpeedSteps28, 29),
    (SpeedSteps28, 127),
    (SpeedSteps28, Int.MaxValue),

    (SpeedSteps128, Int.MinValue),
    (SpeedSteps128, -128),
    (SpeedSteps128, -1),
    (SpeedSteps128, 127),
    (SpeedSteps128, Int.MaxValue)
  )

  "Speeds" should {
    "result from valid values" in {
      forAll(validSpeeds) {
        (steps, speed) => inside(Speed(speed, steps)) {
          case Some(s) =>
            s.speed should be(speed)
            s.steps should be(steps)
        }
      }
    }

    "not allow invalid values" in {
      forAll(invalidSpeeds) {
        (steps, speed) => Speed(speed, steps) should be(None)
      }
    }
  }
}
