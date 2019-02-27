package com.abstractcode.ftrain.communications

import com.abstractcode.ftrain.StackWrapper
import com.abstractcode.ftrain.communications.SerialCommunicationsEffect.{SerialCommunicationsEffect, Request, Response, ResponseSize}
import org.atnos.eff._
import org.atnos.eff.addon.cats.effect.IOEffect
import org.atnos.eff.addon.cats.effect.IOEffect._Io
import org.atnos.eff.interpret._

object SerialCommunicationsInterpretation {
  def runSerialCommunications[RRun <: StackWrapper, R, U: Member.Aux[SerialCommunicationsEffect, R, ?] : _Io, A](eff: Eff[R, A]): Eff[U, A] = for {
    result <- translate(eff)(new Translate[SerialCommunicationsEffect, U] {
      override def apply[X](kv: SerialCommunicationsEffect[X]): Eff[U, X] = kv match {
        case Request(serial, payload, responseSize) => sendRequest(serial, payload, responseSize)
      }
    })
  } yield result

  private def sendRequest[RRun <: StackWrapper, R: _Io](serial: Serial, payload: Vector[Byte], responseSize: ResponseSize): Eff[R, Either[Throwable, Response]] = for {
    _ <- IOEffect.fromIO[R, Either[Throwable, Unit]] { serial.write(payload) }
    read <- IOEffect.fromIO[R, Either[Throwable, Vector[Byte]]] { serial.read(responseSize.size) }
  } yield read.map(Response)
}

trait SerialCommunicationsSyntax {
  implicit class SerialCommunicationsInterpretationOps[R, A](eff: Eff[R, A]) {
    def runSerialCommunications[U: Member.Aux[SerialCommunicationsEffect, R, ?] : _Io]: Eff[U, A] = SerialCommunicationsInterpretation.runSerialCommunications(eff)
  }
}