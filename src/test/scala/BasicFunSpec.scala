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
        val rect = new RectangleCellImpl(false, false, false, false, 10, 10, 0, 0)
      }
    }
  }


  describe("A rectangle cant have height = 0") {
    it(" should raise IllegalSizeException") {
      assertThrows[IllegalSizeException] {
        val r = new RectangleCellImpl(false, false, true, false, 10, 0, 0, 0)
      }
    }
  }

  describe("A rectangle cant have width = 0") {
    it(" should raise IllegalSizeException") {
      assertThrows[IllegalSizeException] {
        val r = new RectangleCellImpl(false, false, true, false, 0, 10, 0, 0)
      }
    }
  }

  describe("A movement") {
    it(" should raise MissingCellException if done on a missing cell") {
      assertThrows[MissingCellException] {
        val list = new ListBuffer[RectangleWithCell]
        var re = new RectangleCellImpl(true, true, true, true, 200,200,0,0)
        var re2 = new RectangleCellImpl(true, true, true, true, 200,200,0,200)
        val recell = new RectangleWithCell(re.getWidth, re.getHeight, re.x, re.getY,re)
        val recell2 = new RectangleWithCell(re2.getWidth, re2.getHeight, re2.x, re2.getY,re)
        list.append(recell)
        list.append(recell2)
        val p = new PlayerWithCell(re, "bot.png")
        val dash = new Dashboard(list ,p)
        println(p._position)
        dash.move(Right,(newRectangle: RectangleCell ,stringUrl : String, isEnded: Boolean)=>{})
      }
    }

    it(" should raise NoMovementException if done on a cell that doesn't allow that movement") {
      assertThrows[NoMovementException] {
        val list = new ListBuffer[RectangleWithCell]
        var re = new RectangleCellImpl(true, false, true, true, 200,200,0,0)
        var re2 = new RectangleCellImpl(true, true, true, true, 200,200,0,200)
        val recell = new RectangleWithCell(re.getWidth, re.getHeight, re.x, re.getY,re)
        val recell2 = new RectangleWithCell(re2.getWidth, re2.getHeight, re2.x, re2.getY,re)
        list.append(recell)
        list.append(recell2)
        val p = new PlayerWithCell(re, "bot.png")
        val dash = new Dashboard(list ,p)
        println(p._position)
        dash.move(Right,(newRectangle: RectangleCell ,stringUrl : String, isEnded: Boolean)=>{})
      }
    }

  }
}