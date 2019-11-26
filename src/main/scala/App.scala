import java.io._

import controller.{Controller, Dashboard, MapController}
import model.{PlayerRepresentation, RectangleCell, RectangleWithCell}
import scalafx.application.JFXApp
import scalafx.scene.shape.Rectangle
import view.{cards, map, shop, shop2}

import scala.collection.mutable.ListBuffer
import javafx.scene.paint.ImagePattern
import model._
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

import scala.collection.mutable.ListBuffer
import scala.util.Random
import javafx.scene.input.MouseEvent

object App extends JFXApp {
 // val c =  new Controller()

  val mapController =  new MapController()
 // val map = new map(mapController)

  //val cards = new cards()
  //val shop = new shop()
  val shop2 = new shop2()

 // c.stage_(shop2.getStage())
 // c.show()

  import javafx.scene.control.Alert.AlertType

  val alert = new Alert(AlertType.INFORMATION)
  alert.setTitle("Information Dialog")
  alert.setHeaderText("Look, an Information Dialog")
  alert.setContentText("I have a great message for you!")

  def write(): Unit = {
    val output = new ObjectOutputStream(new FileOutputStream("./src/main/saves/save2.txt"))

    val outList = new ListBuffer[RectangleCell]
    for(el <- mapController.list) {
      outList.append(el.rectCell)

    }
    output.writeObject(outList)
    output.writeObject(mapController.player.player)
    output.close()
  }
  def read(): Unit = {
    val input = new ObjectInputStream(new FileInputStream("./src/main/saves/save2.txt"))
    val list  : ListBuffer[RectangleCell] = input.readObject().asInstanceOf[ListBuffer[RectangleCell]]
    val player : PlayerRepresentation = input.readObject().asInstanceOf[PlayerRepresentation]

    input.close()



    val lis :ListBuffer[RectangleWithCell] = new ListBuffer[RectangleWithCell]
    for (tmpRect <- list) {
      new RectangleWithCell(tmpRect.getWidth, tmpRect.getHeight, tmpRect.getX, tmpRect.getY,tmpRect).fill_=(RectangleCell.createImage(tmpRect.url, tmpRect.rotation))
      lis.append(new RectangleWithCell(tmpRect.getWidth, tmpRect.getHeight, tmpRect.getX, tmpRect.getY,tmpRect) {
        fill = (RectangleCell.createImage(tmpRect.url, tmpRect.rotation))
      } )
    }

    val pl : PlayerWithCell = PlayerRepresentation.createPlayerCell(player.position, player.url)
    val mapController =  new MapController(lis, Option(pl.player.position))
  }



  class Stock(var symbol: String, var price: BigDecimal)
    extends java.io.Serializable {
    var valu : Int = _
    override def toString = f"$symbol%s is ${price.toDouble}%.2f"
    def setV(v:Int) = valu = v
  }

  def save(): Unit = {
    val rectangleCell = RectangleCell.generateRandomCard()
    val dashboard = new Dashboard(null, null)
    val player = new PlayerRepresentation(rectangleCell, "bot.png")

    val output = new ObjectOutputStream(new FileOutputStream("./src/main/saves/save2.txt"))
    output.writeObject(rectangleCell)
    output.close()

    val input = new ObjectInputStream(new FileInputStream("./src/main/saves/save2.txt"))
    println(input.readObject())
    input.close()

  }

}
