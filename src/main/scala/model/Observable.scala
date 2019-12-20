package model


import view.scenes.ObserverScene


abstract class Observable {
  private var observers: List[ObserverScene] = List()

  def addObserver(observer: ObserverScene): Unit = observers = observer :: observers

  def notifyObserver[A](model: A): Unit = observers.foreach(ob => ob.update(model))
}
