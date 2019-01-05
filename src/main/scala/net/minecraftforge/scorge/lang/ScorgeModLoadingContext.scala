package net.minecraftforge.scorge.lang

import java.util.function.Supplier

import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.ExtensionPoint
import scala.beans.{BeanProperty, BooleanBeanProperty}



object ScorgeModloadingContext {

  private val context: ThreadLocal[ScorgeModLoadingContext] = ThreadLocal.withInitial(() => new ScorgeModLoadingContext)

  def get: ScorgeModLoadingContext = context.get

}

class ScorgeModLoadingContext {

  @BeanProperty
  var activeContainer: ScorgeModContainer = _

  def registerExtensionPoint[T](point: ExtensionPoint[T], extension: Supplier[T]): Unit = {
    getActiveContainer.registerExtensionPoint(point, extension)
  }

  def getModEventBus: IEventBus = getActiveContainer.getEventBus

}