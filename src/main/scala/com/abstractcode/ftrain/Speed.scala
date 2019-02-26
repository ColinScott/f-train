package com.abstractcode.ftrain

sealed trait SpeedSteps {
  def max: Byte
}
case object SpeedSteps28 extends SpeedSteps {
  val max: Byte = 28.toByte
}
case object SpeedSteps128 extends SpeedSteps {
  val max: Byte = 126.toByte
}

final case class Speed private(speed: Byte, steps: SpeedSteps)

object Speed {
  def apply(speed: Int, steps: SpeedSteps): Option[Speed] =
    if (speed >= 0 && speed <= steps.max) Some(new Speed(speed.toByte, steps)) else None
}