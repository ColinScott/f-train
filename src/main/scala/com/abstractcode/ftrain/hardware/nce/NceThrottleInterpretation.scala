package com.abstractcode.ftrain.hardware.nce

import cats.implicits._
import com.abstractcode.ftrain.DSL._
import com.abstractcode.ftrain._
import com.abstractcode.ftrain.communications.CreatePortEffect.HasCreatePort
import com.abstractcode.ftrain.communications.SerialConfiguration
import com.abstractcode.ftrain.communications.SerialConfigurationEffect.HasSerialConfiguration
import com.abstractcode.ftrain.hardware.nce.NceSerialCommunicationsEffect.{HasNceSerialCommunications, ResponseSize}
import com.abstractcode.ftrain.hardware.nce.NceThrottleEffect.{NceThrottleEffect, NoOp, SetLocomotiveSpeed}
import org.atnos.eff._
import org.atnos.eff.interpret._
import org.atnos.eff.all._
import com.abstractcode.ftrain.hardware.nce.IdTranslation._

object NceThrottleInterpretation {
  def runNceThrottle[RRun <: StackWrapper, R, U: Member.Aux[NceThrottleEffect, R, ?] : HasSerialConfiguration : HasCreatePort : HasNceSerialCommunications, A](eff: Eff[R, A]): Eff[U, A] = for {
    result <- translate(eff)(new Translate[NceThrottleEffect, U] {
      override def apply[X](kv: NceThrottleEffect[X]): Eff[U, X] = kv match {
        case NoOp => noop()
        case SetLocomotiveSpeed(id, speed, direction) => setLocomotiveSpeed(id, speed, direction)
      }
    })
  } yield result

  private def noop[RRun <: StackWrapper, R : HasSerialConfiguration : HasCreatePort : HasNceSerialCommunications](): Eff[R, Either[Throwable, Unit]] = for {
    config <- ask[R, SerialConfiguration]
    port <- createPort(config.portName, config.baudRate)
    response <- makeRequest(port, Vector(0x80.toByte), ResponseSize.singleByte)
    validResponse <- response.fold(
      l => Eff.pure[R, NceComms[Unit]](l.asLeft[Unit]),
      r =>
        if (r.payload == Vector(0x21.toByte)) Eff.pure[R, NceComms[Unit]](().asRight[Throwable])
        else Eff.pure[R, NceComms[Unit]](new Exception(s"Unknown response from hardware: ${r.payload}").asLeft[Unit])
    )
  } yield validResponse

  private def setLocomotiveSpeed[RRun <: StackWrapper, R : HasSerialConfiguration : HasCreatePort : HasNceSerialCommunications](id: LocomotiveId, speed: Speed, direction: Direction): Eff[R, Either[Throwable, Unit]] = {
    val locomotiveId = id.toNceFormat
    val opcode: Byte = (speed.steps, direction) match {
      case (SpeedSteps28, Reverse) => 0x01.toByte
      case (SpeedSteps28, Forward) => 0x02.toByte
      case (SpeedSteps128, Reverse) => 0x03.toByte
      case (SpeedSteps128, Forward) => 0x04.toByte
    }
    val command = Vector(0xa2.toByte, locomotiveId.high, locomotiveId.low, opcode, speed.speed)
    for {
      config <- ask[R, SerialConfiguration]
      port <- createPort(config.portName, config.baudRate)
      response <- makeRequest(port, command, ResponseSize.singleByte)
      validResponse <- response.fold(
        l => Eff.pure[R, NceComms[Unit]](l.asLeft[Unit]),
        r =>
          if (r.payload == Vector(0x21.toByte)) Eff.pure[R, NceComms[Unit]](().asRight[Throwable])
          else Eff.pure[R, NceComms[Unit]](new Exception(s"Error from hardware: ${r.payload}").asLeft[Unit])
      )
    } yield validResponse
  }

}

trait NceThrottleSyntax {
  implicit class NceThrottleInterpretationOps[R, A](eff: Eff[R, A]) {
    def runNceThrottle[RRun <: StackWrapper, U: Member.Aux[NceThrottleEffect, R, ?] : HasSerialConfiguration : HasCreatePort: HasNceSerialCommunications]: Eff[U, A] = NceThrottleInterpretation.runNceThrottle(eff)
  }
}