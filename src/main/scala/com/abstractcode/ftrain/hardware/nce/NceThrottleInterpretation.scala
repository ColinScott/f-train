package com.abstractcode.ftrain.hardware.nce

import cats.implicits._
import com.abstractcode.ftrain.DSL._
import com.abstractcode.ftrain.StackWrapper
import com.abstractcode.ftrain.communications.CreatePortEffect.HasCreatePort
import com.abstractcode.ftrain.communications.Serial.BaudRate19200
import com.abstractcode.ftrain.hardware.nce.NceSerialCommunicationsEffect.{HasNceSerialCommunications, ResponseSize}
import com.abstractcode.ftrain.hardware.nce.NceThrottleEffect.{NceThrottleEffect, NoOp}
import org.atnos.eff._
import org.atnos.eff.interpret._

object NceThrottleInterpretation {
  def runNceThrottle[RRun <: StackWrapper, R, U: Member.Aux[NceThrottleEffect, R, ?] : HasCreatePort : HasNceSerialCommunications, A](eff: Eff[R, A]): Eff[U, A] = for {
    result <- translate(eff)(new Translate[NceThrottleEffect, U] {
      override def apply[X](kv: NceThrottleEffect[X]): Eff[U, X] = kv match {
        case NoOp => noop()
      }
    })
  } yield result

  private def noop[RRun <: StackWrapper, R: HasCreatePort : HasNceSerialCommunications](): Eff[R, Either[Throwable, Unit]] = for {
    port <- createPort("ttyUSB0", BaudRate19200)
    response <- makeRequest(port, Vector(0x80.toByte), ResponseSize.singleByte)
    validResponse <- response.fold(
      l => Eff.pure[R, NceComms[Unit]](l.asLeft[Unit]),
      r =>
        if (r.payload == Vector(0x21.toByte)) Eff.pure[R, NceComms[Unit]](().asRight[Throwable])
        else Eff.pure[R, NceComms[Unit]](new Exception(s"Unknown response from hardware: ${r.payload}").asLeft[Unit])
    )
  } yield validResponse

}

trait NceThrottleSyntax {
  implicit class NceThrottleInterpretationOps[R, A](eff: Eff[R, A]) {
    def runNceThrottle[RRun <: StackWrapper, U: Member.Aux[NceThrottleEffect, R, ?]: HasCreatePort: HasNceSerialCommunications]: Eff[U, A] = NceThrottleInterpretation.runNceThrottle(eff)
  }
}