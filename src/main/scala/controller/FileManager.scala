package controller

import java.io.{FileInputStream, ObjectInputStream}

import model.{PlayerRepresentation, RectangleCell, User}

import scala.collection.mutable.ListBuffer

class Loader[A](input: ObjectInputStream) {
  def load: A = input.readObject().asInstanceOf[A]
}

object FileManager {
  var input: ObjectInputStream = new ObjectInputStream(new FileInputStream("./src/main/saves/save2.txt"))
  implicit val rectangleCellsLoader: Loader[ListBuffer[RectangleCell]] =  new Loader[ListBuffer[RectangleCell]](input)
  implicit val playerRepresentationLoader: Loader[PlayerRepresentation] = new Loader[PlayerRepresentation](input)
  implicit val playerLoader: Loader[User] = new Loader[User](input)
  implicit val difficultyLoader: Loader[Difficulty] = new Loader[Difficulty](input)
  def load[A](implicit loader: Loader[A]): A = loader.load
}
