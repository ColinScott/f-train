package com.abstractcode.ftrain.hardware.nce

import com.abstractcode.ftrain.hardware.nce.NceThrottleEffect.{HasNceThrottle, NoOp}
import org.atnos.eff.{Eff, |=}

object NceThrottleEffect {
  sealed trait NceThrottleEffect[A]

  type HasNceThrottle[R] = NceThrottleEffect |= R

  case object NoOp extends NceThrottleEffect[Unit]
}

trait NceThrottleEffectDSL {
  def noop[R: HasNceThrottle](): Eff[R, Unit] = Eff.send(NoOp)
}
