package com.abstractcode.ftrain.hardware.nce

import com.abstractcode.ftrain.hardware.nce.NceError._
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.prop.TableDrivenPropertyChecks

class NceErrorSpec extends WordSpec with Matchers with TableDrivenPropertyChecks {

  private val knownErrors = Table(
    ("errorByte", "expected"),
    (0x30.toByte, CommandNotSupported),
    (0x31.toByte, DecoderAddressOutOfRange),
    (0x32.toByte, CabAddressOrOpCodeOutOfRange),
    (0x33.toByte, CVAddressOrDataOutOfRange),
    (0x34.toByte, ByteCountOutOfRange)
  )

  "Errors" should {
    "translate from know error codes" in {
      forAll(knownErrors) {
        (errorByte, expected) => NceError(errorByte) should be(expected)
      }
    }

    "translate from unknown error codes" in {
      NceError(Byte.MaxValue) should be(UnknownNceError(Byte.MaxValue))
    }
  }
}
