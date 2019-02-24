package com.abstractcode.ftrain.communications

import com.abstractcode.ftrain.StackWrapper
import com.abstractcode.ftrain.communications.CreatePortEffect.{CreatePort, CreatePortEffect}
import org.atnos.eff._
import org.atnos.eff.interpret._
import org.atnos.eff.addon.cats.effect.IOEffect
import org.atnos.eff.addon.cats.effect.IOEffect._Io

object CreatePortInterpretation {
  def runCreatePort[RRun <: StackWrapper, R, U: Member.Aux[CreatePortEffect, R, ?]: _Io, A](
    eff: Eff[R, A]
  ): Eff[U, A] = for {
    result <- translate(eff)(new Translate[CreatePortEffect, U] {
      override def apply[X](kv: CreatePortEffect[X]): Eff[U, X] = kv match {
        case CreatePort(n, b) => IOEffect.fromIO(Serial.open(n, b))
      }
    })
  } yield result
}

trait CreatePortSyntax {
  implicit class CreatePortInterpretationOps[R, A](eff: Eff[R, A]) {
    def runCreatePort[U: Member.Aux[CreatePortEffect, R, ?]: _Io]: Eff[U, A] = CreatePortInterpretation.runCreatePort(eff)
  }
}
