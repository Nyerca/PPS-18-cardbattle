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

  val p = new Player()
  p.position_(r0)

  var anim = new TranslateTransition {
    duration = Duration(1000.0)
    node = p.circle
    interpolator = Interpolator.Linear
    // autoReverse = true
    // cycleCount = Timeline.Indefinite
  }

  def keyPressed (keyCode: KeyCode): Unit = {
    keyCode.getName match {
      case "Up" => {
        anim.fromX = p.position.getX
        anim.fromY=p.position.getY;
        anim.toX = p.position.getX
        anim.toY=p.position.getY-200;
        anim.play()
      }
      case "Left" => {
        anim.fromX = p.position.getX
        anim.fromY=p.position.getY;
        anim.toX=p.position.getX-200;
        anim.toY=p.position.getY;
        anim.play()
      }
      case "Down" => {
        anim.fromX = p.position.getX
        anim.fromY=p.position.getY;
        anim.toX = p.position.getX
        anim.toY=p.position.getY+200;
        anim.play()
      }
      case "Right" =>{
        anim.fromX = p.position.getX
        anim.fromY=p.position.getY;
        anim.toX=p.position.getX+200;
        anim.toY=p.position.getY;
        anim.play()
      }
      case _ => {}
    }
  }


  stage = new PrimaryStage {
    title = "Cardbattle"
    scene = new Scene(1200, 800) {
      content = List();
      for(el <- list) yield { println(el.borders); content.add(el); for(rectangle <- el.borders) yield { content.add(rectangle)} }


      content.add(p.circle)

      onKeyPressed = (ke : KeyEvent) => {
        keyPressed(ke.code);
      }
    }
  }
}






