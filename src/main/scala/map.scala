import controller.Dashboard
import javafx.scene.paint.ImagePattern
import model.{Bottom, Left, Player, RectangleCell, Right, Top}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.control.{Button, ToolBar}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.{Group, Scene}
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.Includes._
import scalafx.animation.{Interpolator, TranslateTransition}
import scalafx.scene.image.{Image, ImageView}
import scalafx.util.Duration

import scala.collection.mutable.ListBuffer

object test extends JFXApp {

val r0 = new RectangleCell(false, true, false, false, elementX= 0.0, elementY=0.0, paint=Color.Grey)


  val list = ListBuffer(
   r0 ,
    new RectangleCell(false, true, true, true, elementX= 200.0, elementY=0.0, paint=Color.Grey),
    new RectangleCell(true, true, false, false, elementX= 200.0, elementY=200.0, paint=Color.Grey),
    new RectangleCell(false, true, false, true, elementX= 400.0, elementY=200.0, paint=Color.Grey)
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

  var selected:Option[RectangleCell] = Option.empty;

  val addList = List(
    new RectangleCell(false, true, false, false, elementX= 0.0, elementY=0.0, paint=Color.Grey),
    new RectangleCell(false, true, true, true, elementX= 200.0, elementY=0.0, paint=Color.Grey),
    new RectangleCell(true, true, false, false, elementX= 200.0, elementY=200.0, paint=Color.Grey),
    new RectangleCell(false, true, false, true, elementX= 400.0, elementY=200.0, paint=Color.Grey)
  );

  val bpane = new BorderPane {
    center = new scalafx.scene.layout.Pane {

      children = List()
      for(el <- list) yield { children.add(el); }
    }
    bottom = new HBox() {

      layoutX = 10
      layoutY = 580
      id = "pane"
      children = List()

      for(el <- addList) yield {
        children.add(el)
      }
      val btn = new Button {
        onAction = () => selected = Option(new RectangleCell(true, true, true, true, elementX= 0.0, elementY=0.0, paint=Color.Grey))
        defaultButton = true
        graphic = new ImageView(new Image("4road.png"))
      }

      children.add(btn);
    }
  }


  stage = new PrimaryStage {
    title = "Cardbattle"
    scene = new Scene(1200, 800) {
      val img = new Image( "noroad.png")
      fill = (new ImagePattern(img, 0, 0, 200, 200, false));
      content = List();

      val dashboard = new Dashboard(list, p, bpane);
      content = bpane
      content.add(p.icon)

      onKeyPressed = (ke : KeyEvent) => {
        keyPressed(ke.code, dashboard);
      }


      import javafx.scene.input.MouseEvent



      onMouseClicked = (e: MouseEvent) => {

        if(selected.isDefined) {
          val tmpRect = selected.get;
          tmpRect.x_=(e.x - e.x % 200)
          tmpRect.y_=(e.y - e.y % 200)
          println("SELECTED: " + selected);
          val centerRef = bpane.center
          bpane.center = new Pane {

            children = List()
            for(el <- list) yield { children.add(el); }
            children.add(tmpRect)
            println("XXX: " + tmpRect.getX)
            tmpRect.setX(e.x - e.x % 200)
            tmpRect.setY(e.y - e.y % 200)
            dashboard.addCell(tmpRect)
            println(dashboard.searchPosition(410,10))
            dashboard.showMap
          }
        }
        println(dashboard.searchPosition(e.x, e.y))
      }

    }
  }
}






