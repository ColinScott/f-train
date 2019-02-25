package com.abstractcode.ftrain.hardware.nce

import com.abstractcode.ftrain.{Broadcast, ExtendedAddress, PrimaryAddress}
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.prop.TableDrivenPropertyChecks
import com.abstractcode.ftrain.hardware.nce.IdTranslation._

class LocomotiveIdTranslationSpec extends WordSpec with Matchers with TableDrivenPropertyChecks {
  "Broadcast Id" should {
    "translate to zeros" in {
      Broadcast.toNceFormat should be(NceLocomotiveId(0, 0))
    }
  }

  private val primaryAddresses = Table[Byte](
    "address",
    1,
    2,
    127
  )

  "Primary Addresses" should {
    "have a zero upper byte" in {
      forAll(primaryAddresses) {
        address => PrimaryAddress(address).toNceFormat.high should be(0x00.toByte)
      }
    }

    "have a lower byte equal to the address" in {
      forAll(primaryAddresses) {
        address => PrimaryAddress(address).toNceFormat.low should be(address)
      }
    }
  }

  private val extendedAddresses = Table[Short, Byte, Byte](
    ("address", "high", "low"),
    (3.toShort, 0xc0.toByte, 0x03.toByte),
    (7562.toShort, 0xdd.toByte, 0x8a.toByte),
  )

  "Extended Addresses" should {
    "translate to NCE format" in {
      forAll(extendedAddresses) {
        (address, high, low) => ExtendedAddress(address).toNceFormat should be (NceLocomotiveId(high, low))
      }
    }
  }
}
