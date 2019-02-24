package com.abstractcode.ftrain.hardware.nce

import com.abstractcode.ftrain.communications.Serial
import com.abstractcode.ftrain.hardware.nce.NceSerialCommunicationsEffect._
import org.atnos.eff.{Eff, |=}

object NceSerialCommunicationsEffect {

  sealed trait NceSerialCommunicationsEffect[A]

  type HasNceSerialCommunications[R] = NceSerialCommunicationsEffect |= R

  sealed trait ResponseSize {
    val size: Int
  }
  object ResponseSize {
    private case class ResponseSizeImpl(size: Int) extends ResponseSize

    val zeroByte: ResponseSize = ResponseSizeImpl(0)
    val singleByte: ResponseSize = ResponseSizeImpl(1)
    val doubleByte: ResponseSize = ResponseSizeImpl(2)
    val tripleByte: ResponseSize = ResponseSizeImpl(3)
    val quadByte: ResponseSize = ResponseSizeImpl(4)
    val sixteenByte: ResponseSize = ResponseSizeImpl(16)
  }

  case class Request(serial: Serial, payload: Vector[Byte], responseSize: ResponseSize) extends NceSerialCommunicationsEffect[Either[Throwable, Response]]
  case class Response(payload: Vector[Byte])

}

trait NceSerialCommunicationsEffectDSL {
  def makeRequest[R: HasNceSerialCommunications](
    serial: Serial,
    payload: Vector[Byte],
    responseSize: ResponseSize
  ): Eff[R, Either[Throwable, Response]] =
    Eff.send(Request(serial, payload, responseSize))
}
