package controller

import java.io.{ObjectInputStream, ObjectOutputStream}

object FileManager {
  def load[A](input: ObjectInputStream): A = input.readObject().asInstanceOf[A]
  def save[A](output: ObjectOutputStream)(obj: A): Unit = output.writeObject(obj)
}
