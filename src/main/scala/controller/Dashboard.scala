package controller

import model.{Move, Player, RectangleCell, Top, Bottom, Left, Right}
import scalafx.animation.{Interpolator, TranslateTransition}
import scalafx.util.Duration

class Dashboard(cells: List[RectangleCell], player: Player) {

  var anim = new TranslateTransition {
    duration = Duration(1000.0)
    node = player.circle
    interpolator = Interpolator.Linear
    // autoReverse = true
    // cycleCount = Timeline.Indefinite
  }


  def searchPosition(newX : Double, newY : Double): Option[RectangleCell] = {
    //for(rectangle <- cells if(rectangle.getX == newX)) yield rectangle
    (for (rectangle <- cells if rectangle.getX == newX && rectangle.getY == newY) yield rectangle).headOption
  }



  def move(movement : Move): Unit = movement match {
    case Top  => {

      anim.fromY = player.position.getY;
      anim.toY= player.position.getY-200;
      anim.fromX = player.position.getX;
      anim.toX = player.position.getX;
      val newRectangle = this.searchPosition( anim.toX.toDouble, anim.toY.toDouble)
      if(newRectangle.isDefined) {
        println("From: (" + player.position.getX + ", " + player.position.getY + ") to: (" + anim.toX.toDouble + ", " + anim.toY.toDouble + ")")
        anim.setOnFinished(e => player.position_(newRectangle.get))
        anim.play();
      }
    }
    case Right => {

      anim.fromX = player.position.getX;
      anim.toX= player.position.getX + 200;
      anim.fromY = player.position.getY;
      anim.toY = player.position.getY;
      val newRectangle = this.searchPosition( anim.toX.toDouble, anim.toY.toDouble)
      if(newRectangle.isDefined) {
        println("From: (" + player.position.getX + ", " + player.position.getY + ") to: (" + anim.toX.toDouble + ", " + anim.toY.toDouble + ")")
        anim.setOnFinished(e => player.position_(newRectangle.get))
        anim.play();
      }
    }
    case Bottom => {

      anim.fromY = player.position.getY
      anim.toY= player.position.getY+ 200;
      anim.fromX = player.position.getX;
      anim.toX = player.position.getX;
      val newRectangle = this.searchPosition( anim.toX.toDouble, anim.toY.toDouble)
      if(newRectangle.isDefined) {
        println("From: (" + player.position.getX + ", " + player.position.getY + ") to: (" + anim.toX.toDouble + ", " + anim.toY.toDouble + ")")
        anim.setOnFinished(e => player.position_(newRectangle.get))
        anim.play();
      }
    }
    case Left => {

      anim.fromX = player.position.getX;
      anim.toX= player.position.getX-200;
      anim.fromY = player.position.getY;
      anim.toY = player.position.getY;
      val newRectangle = this.searchPosition( anim.toX.toDouble, anim.toY.toDouble)
      if(newRectangle.isDefined) {
        println("From: (" + player.position.getX + ", " + player.position.getY + ") to: (" + anim.toX.toDouble + ", " + anim.toY.toDouble + ")")
        anim.setOnFinished(e => player.position_(newRectangle.get))
        anim.play();
      }
    }
    case _  => {}
  }
}
