package com.abstractcode.ftrain

import cats.effect.IO
import com.abstractcode.ftrain.communications.Serial.BaudRate19200
import com.abstractcode.ftrain.hardware.nce.NceSerialCommunicationsEffect.{NceSerialCommunicationsEffect, Response, ResponseSize}
import org.atnos.eff._
import org.atnos.eff.syntax.addon.cats.effect._
import DSL._
import Syntax._
import com.abstractcode.ftrain.communications.CreatePortEffect.CreatePortEffect


object Main extends App {

  type FullStack = Fx.fx4[IO, Memoized, CreatePortEffect, NceSerialCommunicationsEffect]

  class FullStackWrapper extends StackWrapper {
    type Stack = FullStack
  }

  val program: Eff[FullStack, (Either[Throwable, Response], Either[Throwable, Response])] = for {
    port1 <- createPort[FullStack]("ttyUSB0", BaudRate19200)
    a <- makeRequest[FullStack](port1, Vector(0x80.toByte), ResponseSize.singleByte)
    port2 <- createPort[FullStack]("ttyUSB0", BaudRate19200)
    b <- makeRequest[FullStack](port2, Vector(0x80.toByte), ResponseSize.singleByte)
  } yield (a, b)

  val a = program.runNceSerialCommunications.runCreatePort.runIoMemo(ConcurrentHashMapCache()).unsafeRunSync

  println(a)
}
