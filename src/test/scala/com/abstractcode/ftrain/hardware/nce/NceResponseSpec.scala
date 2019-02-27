package com.abstractcode.ftrain.hardware.nce

import com.abstractcode.ftrain.hardware.nce.NceResponse._
import org.scalatest.{EitherValues, Matchers, WordSpec}
import org.scalatest.prop.TableDrivenPropertyChecks

class NceResponseSpec extends WordSpec with Matchers with TableDrivenPropertyChecks with EitherValues {

  private val knownErrors = Table(
    ("errorByte", "expected"),
    (0x30.toByte, CommandNotSupported),
    (0x31.toByte, DecoderAddressOutOfRange),
    (0x32.toByte, CabAddressOrOpCodeOutOfRange),
    (0x33.toByte, CVAddressOrDataOutOfRange),
    (0x34.toByte, ByteCountOutOfRange)
  )

  "Responses" should {
    "should produce Unit for success response" in {
      NceResponse(0x21.toByte).right.value should be(())
    }

    "should produce failure for error code" in {
      forAll(knownErrors) {
        (errorByte, expected) => NceResponse(errorByte).left.value should be (NceFailure(expected))
      }
    }

    "should produce unknown error failure for unknown response" in {
      NceResponse(Byte.MaxValue).left.value should be(NceFailure(UnknownNceError(Byte.MaxValue)))
    }
  }
}
