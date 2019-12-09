package controller

import java.io.{FileInputStream, ObjectInputStream}

import model.{PlayerRepresentation, RectangleCell, User}

import scala.collection.mutable.ListBuffer

class Loader[A]() {
  def load(input: ObjectInputStream): A = input.readObject().asInstanceOf[A]
}

object FileManager {
  var input: ObjectInputStream = _
  implicit val rectangleCellsLoader: Loader[ListBuffer[RectangleCell]] =  new Loader[ListBuffer[RectangleCell]]
  implicit val playerRepresentationLoader: Loader[PlayerRepresentation] = new Loader[PlayerRepresentation]
  implicit val playerLoader: Loader[User] = new Loader[User]
  implicit val difficultyLoader: Loader[Difficulty] = new Loader[Difficulty]
  implicit val doubleLoader: Loader[Double] = new Loader[Double]
  def load[A](input: ObjectInputStream)(implicit loader: Loader[A]): A = loader.load(input)
}
