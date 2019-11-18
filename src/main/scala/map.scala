import controller.Dashboard
import javafx.scene.paint.ImagePattern
import model.{Bottom, Left, Player, RectangleCell, Right, Top}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.control.{Button, ToolBar}
import scalafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
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
  var selected:Option[RectangleCell] = Option.empty;

  val list = ListBuffer(
    new RectangleCell(false, true, false, false, elementX= 200.0, elementY=200.0, paint=Color.Grey),
    new RectangleCell(false, true, true, true, elementX= 400.0, elementY=200.0, paint=Color.Grey),
    new RectangleCell(true, true, false, false, elementX= 400.0, elementY=400.0, paint=Color.Grey),
    new RectangleCell(false, true, false, true, elementX= 600.0, elementY=400.0, paint=Color.Grey)
  );
  import javafx.scene.input.MouseEvent


  def keyPressed (keyCode: KeyCode, dashboard : Dashboard): Unit = {
    keyCode.getName match {
      case "Up" => dashboard.move(Top);
      case "Left" => dashboard.move(Left);
      case "Down" => dashboard.move(Bottom);
      case "Right" => dashboard.move(Right);
      case _ => {}
    }
  }

  val pane = new Pane {
    children = list
  }

  def createBottomCard(): ListBuffer[Button] = {
    val tmpList = ListBuffer[Button]()
    val btn = new Button {
      val re = new RectangleCell(true, true, true, true, elementX= 0.0, elementY=0.0, paint=Color.Grey)
      onAction = () => selected = Option(re)
      defaultButton = true
      graphic = new ImageView(new Image(re.url()))
    }
    val btn2 = new Button {
      val re =new RectangleCell(false, true, false, true, elementX= 0.0, elementY=0.0, paint=Color.Grey)
      onAction = () => selected = Option(re)
      defaultButton = true
      graphic = new ImageView(new Image(re.url()))
    }
    tmpList.append(btn)
    tmpList.append(btn2)
    tmpList
  }

  stage = new PrimaryStage {
    title = "Cardbattle"
    scene = new Scene(1200, 800) {



      fill = (new ImagePattern(new Image( "noroad.png"), 0, 0, 200, 200, false));
      val bpane = new BorderPane {
        center = pane
        bottom = new HBox() {

          layoutX = 10
          layoutY = 580
          id = "pane"
          children = List()



          val addList = createBottomCard
          children = addList
        }
      }
      val player = new Player(new RectangleCell(false, true, false, false, elementX= 200.0, elementY=200.0, paint=Color.Grey));
      val dashboard = new Dashboard(list, player, bpane);
      content = bpane
      content.add(player.icon);
      //content.add(hbox);


      onKeyPressed = (ke : KeyEvent) => {
        keyPressed(ke.code, dashboard);
      }


      onMouseClicked = (e: MouseEvent) => {

        if(selected.isDefined) {
          val tmpRect = selected.get;
          dashboard.showMap
          tmpRect.x_=(e.x - dashboard.traslationX - e.x % 200)
          tmpRect.y_=(e.y - dashboard.traslationY- e.y % 200)
          tmpRect.setX(e.x - dashboard.traslationX - e.x % 200)
          tmpRect.setY(e.y - dashboard.traslationY- e.y % 200)
          println("SELECTED: " + selected);


          val listTmp = new ListBuffer[RectangleCell]()
          for(el <- list) yield { listTmp.append(el); }
          listTmp.append(tmpRect)
          pane.children =(listTmp)

          println("------")
          dashboard.addCell(tmpRect)
          dashboard.showMap

        }
        println(dashboard.searchPosition(e.x, e.y))
      }

    }
  }
  stage.resizable = false
  stage.fullScreen = true




}