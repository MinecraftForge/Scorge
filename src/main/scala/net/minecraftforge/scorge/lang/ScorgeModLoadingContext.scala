package net.minecraftforge.scorge.lang

import java.util.function.Supplier

import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.{ExtensionPoint, ModLoadingContext}

import scala.beans.{BeanProperty, BooleanBeanProperty}



object ScorgeModLoadingContext {


  def get: ScorgeModLoadingContext = ModLoadingContext.get().extension()

}

class ScorgeModLoadingContext(container:ScorgeModContainer) {


  def getModEventBus: IEventBus = container.getEventBus

}