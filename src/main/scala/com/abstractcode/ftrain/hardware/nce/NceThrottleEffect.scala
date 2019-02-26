package com.abstractcode.ftrain.hardware.nce

import com.abstractcode.ftrain.{Direction, LocomotiveId, Speed}
import com.abstractcode.ftrain.hardware.nce.NceThrottleEffect.{HasNceThrottle, NoOp, SetLocomotiveSpeed}
import org.atnos.eff._

object NceThrottleEffect {
  sealed trait NceThrottleEffect[A]

  type HasNceThrottle[R] = NceThrottleEffect |= R

  case object NoOp extends NceThrottleEffect[Either[Throwable, Unit]]

  case class SetLocomotiveSpeed(locomotiveId: LocomotiveId, speed: Speed, direction: Direction) extends NceThrottleEffect[Either[Throwable, Unit]]
}

trait NceThrottleEffectDSL {
  def noop[R: HasNceThrottle](): Eff[R, Either[Throwable, Unit]] = Eff.send(NoOp)
  def setLocomotiveSpeed[R: HasNceThrottle](locomotiveId: LocomotiveId, speed: Speed, direction: Direction): Eff[R, Either[Throwable, Unit]] =
    Eff.send(SetLocomotiveSpeed(locomotiveId, speed, direction))
}