import java.util.concurrent.CountDownLatch

import controller.Dashboard
import exception.{IllegalSizeException, MissingCellException, NoMovementException}
import model.{Player, RectangleCell}
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import org.scalatest.{FlatSpec, FunSpec, FunSuite, Matchers}
import model._

import scala.collection.mutable.ListBuffer



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
        val rect =  RectangleCell(false, false, false, false, 10, 10, 0, 0, false)
      }
    }
  }


  describe("A rectangle cant have height = 0") {
    it(" should raise IllegalSizeException") {
      assertThrows[IllegalSizeException] {
        val r =  RectangleCell(false, false, true, false, 10, 0, 0, 0, false)
      }
    }
  }

  describe("A rectangle cant have width = 0") {
    it(" should raise IllegalSizeException") {
      assertThrows[IllegalSizeException] {
        val r =  RectangleCell(false, false, true, false, 0, 10, 0, 0, false)
      }
    }
  }

  describe("A movement") {
    it(" should raise MissingCellException if done on a missing cell") {
      assertThrows[MissingCellException] {
        var list:List[RectangleCell] = List()
        var re =  RectangleCell(true, true, true, true, 200,200,0,0, false)
        var re2 =  RectangleCell(true, true, true, true, 200,200,0,200, false)
        list = list :+ re
        list = list :+ re2
        val p = PlayerRepresentation(re, "bot.png")
        val dash = Dashboard(list)
        println(p.position)
        dash.->(Right, p,(newRectangle: RectangleCell ,stringUrl : String, isEnded: Boolean)=>{})
      }
    }

    it(" should raise NoMovementException if done on a cell that doesn't allow that movement") {
      assertThrows[NoMovementException] {
        var list:List[RectangleCell] = List()
        var re =  RectangleCell(true, false, true, true, 200,200,0,0, false)
        var re2 =  RectangleCell(true, true, true, true, 200,200,0,200, false)


        list = list :+ re
        list = list :+ re2
        val p = PlayerRepresentation(re, "bot.png")
        val dash = Dashboard(list)
        println(p.position)
        dash.->(Right, p,(newRectangle: RectangleCell ,stringUrl : String, isEnded: Boolean)=>{})
      }
    }

  }

}