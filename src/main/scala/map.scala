import model.{Player, RectangleCell}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.control.ToolBar
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.{Group, Scene}
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.Includes._

object test extends JFXApp {

val r0 = new RectangleCell(false, true, false, false,elementX = 0, elementY = 0, paint=Color.Grey)


  val list = List(
    r0,
    new RectangleCell(false, false, true, true,elementX = 200, elementY = 0, paint=Color.Grey),
    new RectangleCell(true, true, false, false,elementX = 200, elementY = 200, paint=Color.Grey),
    new RectangleCell(false, false, false, true,elementX = 400, elementY = 200, paint=Color.Grey)
  );

  def keyPressed (keyCode: KeyCode): Unit = {
    keyCode.getName match {
      case "Up" => println("TOP")
      case "Left" => println("LEFT")
      case "Down" => println("BOTTOM")
      case "Right" => println("RIGHT")
      case _ => {}
    }
  }


  stage = new PrimaryStage {
    title = "Cardbattle"
    scene = new Scene(1200, 800) {
      content = List();
      for(el <- list) yield { println(el.borders); content.add(el); for(rectangle <- el.borders) yield { content.add(rectangle)} }

      val p = new Player()
      p.position_(r0)
      content.add(p.circle)

      onKeyPressed = (ke : KeyEvent) => {
        keyPressed(ke.code);
      }
    }
  }
}






