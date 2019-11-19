import scalafx.application.JFXApp
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

}
