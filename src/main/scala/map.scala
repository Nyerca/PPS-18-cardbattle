import controller.Dashboard
import model.{Player, RectangleCell, Top, Right, Bottom, Left}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.control.ToolBar
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.{Group, Scene}
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.Includes._
import scalafx.animation.{Interpolator, TranslateTransition}
import scalafx.util.Duration

object test extends JFXApp {

val r0 = new RectangleCell(false, true, false, false,elementX = 0, elementY = 0, paint=Color.Grey)


  val list = List(
    r0,
    new RectangleCell(false, false, true, true,elementX = 200, elementY = 0, paint=Color.Grey),
    new RectangleCell(true, true, false, false,elementX = 200, elementY = 200, paint=Color.Grey),
    new RectangleCell(false, false, false, true,elementX = 400, elementY = 200, paint=Color.Grey)
  );

  val p = new Player(r0)


  def keyPressed (keyCode: KeyCode, dashboard : Dashboard): Unit = {
    keyCode.getName match {
      case "Up" => dashboard.move(Top);
      case "Left" => dashboard.move(Left);
      case "Down" => dashboard.move(Bottom);
      case "Right" => dashboard.move(Right);
      case _ => {}
    }
  }

  stage = new PrimaryStage {
    title = "Cardbattle"
    scene = new Scene(1200, 800) {
      content = List();
      for(el <- list) yield {content.add(el); }

      val dashboard = new Dashboard(list, p);
      content.add(p.icon)

      onKeyPressed = (ke : KeyEvent) => {
        keyPressed(ke.code, dashboard);
      }
    }
  }
}






