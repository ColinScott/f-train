package com.abstractcode.ftrain.communications

import com.abstractcode.ftrain.StackWrapper
import com.abstractcode.ftrain.communications.CreatePortEffect.{CreatePort, CreatePortEffect}
import com.abstractcode.ftrain.communications.Serial.BaudRate
import org.atnos.eff._
import org.atnos.eff.interpret._
import org.atnos.eff.addon.cats.effect.IOEffect
import org.atnos.eff.addon.cats.effect.IOEffect._Io
import org.atnos.eff.memo._

object CreatePortInterpretation {
  def runCreatePort[RRun <: StackWrapper, R, U: Member.Aux[CreatePortEffect, R, ?]: _Io : _Memo, A](
    eff: Eff[R, A]
  ): Eff[U, A] = for {
    result <- translate(eff)(new Translate[CreatePortEffect, U] {
      override def apply[X](kv: CreatePortEffect[X]): Eff[U, X] = kv match {
        case CreatePort(n, b) => createPort(n, b)
      }
    })
  } yield result

  def createPort[R: _Memo : _Io](portName: String, baudRate: BaudRate): Eff[R, Serial] = {
    val cacheKey = s"$portName:$baudRate"
    for {
      cache <- getCache[R]
      cached = cache.get[Serial](cacheKey)
      serial <- cached match {
        case Some(s) => Eff.pure[R, Serial](s)
        case None => for {
          s <- IOEffect.fromIO(Serial(portName, baudRate))
        } yield cache.put[Serial](cacheKey, s)
      }
    } yield serial
  }

}

trait CreatePortSyntax {
  implicit class CreatePortInterpretationOps[R, A](eff: Eff[R, A]) {
    def runCreatePort[U: Member.Aux[CreatePortEffect, R, ?]: _Io: _Memo]: Eff[U, A] = CreatePortInterpretation.runCreatePort(eff)
  }
}