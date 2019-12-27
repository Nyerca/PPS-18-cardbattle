package model

import view.scenes.ObserverScene

abstract class Observable {

  private var observers: List[ObserverScene] = List()

  def addObserver(observer: ObserverScene): Unit = observers = observer :: observers

  def removeObserver(observer: ObserverScene): Unit = observers = observers diff List(observer)

  def notifyObserver[A](model: A): Unit = observers.foreach(ob => ob.update(model))
}
