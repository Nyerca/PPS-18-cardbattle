import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import org.scalatest.{FlatSpec, FunSpec, FunSuite, Matchers}


@RunWith(classOf[JUnitRunner])
class BasicFunSpec extends FunSpec with Matchers  {


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

  
}