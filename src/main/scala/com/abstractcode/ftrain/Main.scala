package com.abstractcode.ftrain

import cats.effect.IO
import com.abstractcode.ftrain.hardware.nce.NceSerialCommunicationsEffect.NceSerialCommunicationsEffect
import org.atnos.eff._
import org.atnos.eff.syntax.addon.cats.effect._
import DSL._
import Syntax._
import com.abstractcode.ftrain.communications.CreatePortEffect.CreatePortEffect
import com.abstractcode.ftrain.hardware.nce.NceThrottleEffect.NceThrottleEffect


object Main extends App {

  type FullStack = Fx.fx5[IO, Memoized, CreatePortEffect, NceSerialCommunicationsEffect, NceThrottleEffect]

  class FullStackWrapper extends StackWrapper {
    type Stack = FullStack
  }

  val program: Eff[FullStack, Either[Throwable, Unit]] = for {
    response <- noop[FullStack]()
  } yield response

  val stack: Eff[Fx1[IO], Either[Throwable, Unit]] = program
    .runNceThrottle
    .runNceSerialCommunications
    .runCreatePort
    .runIoMemo(ConcurrentHashMapCache())

  val b = stack.unsafeRunSync

  println(b)
}
