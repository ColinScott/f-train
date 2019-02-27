package com.abstractcode.ftrain

import cats.effect.IO
import com.abstractcode.ftrain.communications.SerialCommunicationsEffect.SerialCommunicationsEffect
import org.atnos.eff._
import org.atnos.eff.syntax.all._
import org.atnos.eff.syntax.addon.cats.effect._
import DSL._
import Syntax._
import com.abstractcode.ftrain.communications.CreatePortEffect.CreatePortEffect
import com.abstractcode.ftrain.communications.Serial.BaudRate19200
import com.abstractcode.ftrain.communications.SerialConfiguration
import com.abstractcode.ftrain.communications.SerialConfigurationEffect.SerialConfigurationReader
import com.abstractcode.ftrain.hardware.nce.NceResponse.NceResponse
import com.abstractcode.ftrain.hardware.nce.NceThrottleEffect.NceThrottleEffect


object Main extends App {

  type FullStack = Fx.fx6[IO, Memoized, SerialConfigurationReader, CreatePortEffect, SerialCommunicationsEffect, NceThrottleEffect]

  class FullStackWrapper extends StackWrapper {
    type Stack = FullStack
  }

  val program: Eff[FullStack, NceResponse] = for {
    response <- noop[FullStack]()
  } yield response

  val stack: Eff[Fx1[IO], NceResponse] = program
    .runNceThrottle
    .runSerialCommunications
    .runCreatePort
    .runReader(SerialConfiguration("ttyUSB0", BaudRate19200))
    .runIoMemo(ConcurrentHashMapCache())

  val b = stack.unsafeRunSync

  println(b)
}
