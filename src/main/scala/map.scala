import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene

object test extends JFXApp {
  stage = new PrimaryStage {
    title = "Cardbattle"
    scene = new Scene(1200, 800)
  }
}
