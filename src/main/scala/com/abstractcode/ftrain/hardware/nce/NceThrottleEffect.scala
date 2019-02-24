package com.abstractcode.ftrain.hardware.nce

import com.abstractcode.ftrain.hardware.nce.NceThrottleEffect.{HasNceThrottle, NoOp}
import org.atnos.eff._

object NceThrottleEffect {
  sealed trait NceThrottleEffect[A]

  type HasNceThrottle[R] = NceThrottleEffect |= R

  case object NoOp extends NceThrottleEffect[Either[Throwable, Unit]]
}

trait NceThrottleEffectDSL {
  def noop[R: HasNceThrottle](): Eff[R, Either[Throwable, Unit]] = Eff.send(NoOp)
}