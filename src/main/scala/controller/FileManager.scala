package controller

import java.io.{ObjectInputStream, ObjectOutputStream}

import model.{PlayerRepresentation, RectangleCell, User}

class Loader[A]() {
  def load(input: ObjectInputStream): A = input.readObject().asInstanceOf[A]
}

object FileManager {
  implicit val rectangleCellsLoader: Loader[List[RectangleCell]] =  new Loader[List[RectangleCell]]
  implicit val playerRepresentationLoader: Loader[PlayerRepresentation] = new Loader[PlayerRepresentation]
  implicit val playerLoader: Loader[User] = new Loader[User]
  implicit val difficultyLoader: Loader[Difficulty] = new Loader[Difficulty]
  implicit val doubleLoader: Loader[Double] = new Loader[Double]
  def load[A](input: ObjectInputStream)(implicit loader: Loader[A]): A = loader.load(input)
  def save[A](output: ObjectOutputStream)(obj: A): Unit = output.writeObject(obj)
}
