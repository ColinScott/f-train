package com.abstractcode.ftrain.hardware.nce

import com.abstractcode.ftrain.hardware.nce.NceResponse.NceResponse
import com.abstractcode.ftrain.{Direction, LocomotiveId, Speed}
import com.abstractcode.ftrain.hardware.nce.NceThrottleEffect.{HasNceThrottle, NoOp, SetLocomotiveSpeed}
import org.atnos.eff._

object NceThrottleEffect {
  sealed trait NceThrottleEffect[A]

  type HasNceThrottle[R] = NceThrottleEffect |= R

  case object NoOp extends NceThrottleEffect[NceResponse]
  case class SetLocomotiveSpeed(locomotiveId: LocomotiveId, speed: Speed, direction: Direction) extends NceThrottleEffect[NceResponse]
}

trait NceThrottleEffectDSL {
  def noop[R: HasNceThrottle](): Eff[R, NceResponse] = Eff.send(NoOp)
  def setLocomotiveSpeed[R: HasNceThrottle](locomotiveId: LocomotiveId, speed: Speed, direction: Direction): Eff[R, NceResponse] =
    Eff.send(SetLocomotiveSpeed(locomotiveId, speed, direction))
}