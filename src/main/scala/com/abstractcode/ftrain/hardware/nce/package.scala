package com.abstractcode.ftrain.hardware

package object nce {
  type NceComms[A] = Either[Throwable, A]
}
