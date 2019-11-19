import java.io._

import controller.{Controller, MapController}
import model.RectangleCell
import scalafx.application.JFXApp
import view.map

object App extends JFXApp {
  val c =  new Controller()
  val mapController =  new MapController()
  val map = new map(mapController)
  c.stage_(map.getStage())
  c.show()

  class Stock(var symbol: String, var price: BigDecimal)
    extends java.io.Serializable {
    override def toString = f"$symbol%s is ${price.toDouble}%.2f"
  }


  def save(): Unit = {
    val rectangleCell = RectangleCell.generateRandomCard()

    val output = new ObjectOutputStream(new FileOutputStream("./src/main/saves/save2.txt"))
    output.writeObject(new Stock("a",3))
    output.close()

    val input = new ObjectInputStream(new FileInputStream("./src/main/saves/save2.txt"))
    println(input.readObject())
    input.close()


    val file_Object = new File("./src/main/saves/save.txt" )

    // Passing reference of file to the printwriter
    val print_Writer = new PrintWriter(file_Object)
    print_Writer.print(rectangleCell)
    // Closing printwriter
    print_Writer.close()
  }

}
