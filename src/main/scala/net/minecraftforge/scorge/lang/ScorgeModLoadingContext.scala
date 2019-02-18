package net.minecraftforge.scorge.lang

import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.ModLoadingContext

object ScorgeModLoadingContext {

  def get: ScorgeModLoadingContext = ModLoadingContext.get().extension()

}

class ScorgeModLoadingContext(container:ScorgeModContainer) {

  def getModEventBus: IEventBus = container.getEventBus

}