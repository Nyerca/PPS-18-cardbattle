import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.control.ToolBar
import scalafx.scene.input.KeyCode
import scalafx.scene.{Group, Scene}
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}

object test extends JFXApp {

  val circle = new Circle {
    centerX = 100
    centerY = 100
    radius = 20
    fill = Color.Blue
  }


  val list = List(
    new Rectangle() {
      width = 200
      height = 200
      x= 0
      y=0
      fill=Color.Grey
    },new Rectangle() {
      width = 200
      height = 200
      x= 0
      y=200
      fill=Color.Grey
    },new Rectangle() {
      width = 200
      height = 200
      x= 200
      y=200
      fill=Color.Grey
    },new Rectangle() {
    width = 200
    height = 200
    x= 400
    y=200
    fill=Color.Grey
  }
  );

  var rect2 = new Rectangle() {
    width=200;
    height=20;
    fill=Color.Red;
  }

  stage = new PrimaryStage {
    title = "Cardbattle"
    scene = new Scene(1200, 800) {
      content = List();
      for(el <- list) yield { content.add(el); }
      content.add(circle)
    }
  }
}






