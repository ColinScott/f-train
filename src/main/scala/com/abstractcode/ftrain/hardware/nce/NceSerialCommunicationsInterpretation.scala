package com.abstractcode.ftrain.hardware.nce

import com.abstractcode.ftrain.StackWrapper
import com.abstractcode.ftrain.communications.Serial
import com.abstractcode.ftrain.hardware.nce.NceSerialCommunicationsEffect.{NceSerialCommunicationsEffect, Request, Response, ResponseSize}
import org.atnos.eff._
import org.atnos.eff.interpret._
import org.atnos.eff.addon.cats.effect.IOEffect
import org.atnos.eff.addon.cats.effect.IOEffect._Io

object NceSerialCommunicationsInterpretation {
  def runNceSerialCommunications[RRun <: StackWrapper, R, U: Member.Aux[NceSerialCommunicationsEffect, R, ?] : _Io, A](eff: Eff[R, A]): Eff[U, A] = for {
    result <- translate(eff)(new Translate[NceSerialCommunicationsEffect, U] {
      override def apply[X](kv: NceSerialCommunicationsEffect[X]): Eff[U, X] = kv match {
        case Request(serial, payload, responseSize) => sendRequest(serial, payload, responseSize)
      }
    })
  } yield result

  private def sendRequest[RRun <: StackWrapper, R: _Io](serial: Serial, payload: Vector[Byte], responseSize: ResponseSize): Eff[R, Either[Throwable, Response]] = for {
    _ <- IOEffect.fromIO[R, Either[Throwable, Unit]] { Serial.write(serial)(payload) }
    read <- IOEffect.fromIO[R, Either[Throwable, Vector[Byte]]] { Serial.read(serial)(responseSize.size) }
  } yield read.map(Response)
}

trait NceSerialCommunicationsSyntax {
  implicit class NceSerialCommunicationsInterpretationOps[R, A](eff: Eff[R, A]) {
    def runNceSerialCommunications[U: Member.Aux[NceSerialCommunicationsEffect, R, ?]: _Io]: Eff[U, A] = NceSerialCommunicationsInterpretation.runNceSerialCommunications(eff)
  }
}