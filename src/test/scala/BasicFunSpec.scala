import model.{IllegalSizeException, NoMovementException, RectangleCell}
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import org.scalatest.{FlatSpec, FunSpec, FunSuite, Matchers}


@RunWith(classOf[JUnitRunner])
class BasicFunSpec extends FunSpec with Matchers  {
  var rect : RectangleCell = _


  describe(" A Set ") {
    describe(" when empty ") {
      it(" should have size 0 ") {
        assert(Set.empty.size == 0)
      }
      it(" should raise N oS uc hEl em en tEx ce pt ion for head ") {
        assertThrows[NoSuchElementException] {
          Set.empty.head
        }
      }
    }

    describe(" when not empty"){
      it("should have the correct size") { // Here, 'it' refers to "A Set (when non-empty)". This test's full
        assert(Set(1, 2, 3).size == 3)     // name is: "A Set (when non-empty) should have the correct size"
      }

    }
  }

  describe("A rectangle cant have all connections to false") {
    it(" should raise NoMovementException") {
      assertThrows[NoMovementException] {
        val rect = new RectangleCell(false, false, false, false, 10, 10, 0, 0)
      }
    }
  }


  describe("A rectangle cant have height = 0") {
    it(" should raise IllegalSizeException") {
      assertThrows[IllegalSizeException] {
        val r = new RectangleCell(false, false, true, false, 10, 0, 0, 0)
      }
    }
  }

  describe("A rectangle cant have width = 0") {
    it(" should raise IllegalSizeException") {
      assertThrows[IllegalSizeException] {
        val r = new RectangleCell(false, false, true, false, 0, 10, 0, 0)
      }
    }
  }
}