package net.minecraftforge.scorge.lang

import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.ModLoadingContext

object ScorgeModLoadingContext {

  /**
   * Helper to get the right instance from the [[ModLoadingContext]] correctly.
   * @return The ScorgeMod language specific extension from the ModLoadingContext
   */
  def get: ScorgeModLoadingContext = ModLoadingContext.get().extension()

}

class ScorgeModLoadingContext(container:ScorgeModContainer) {

  /**
   * @return The mod's event bus, to allow subscription to Mod specific events
   */
  def getModEventBus: IEventBus = container.getEventBus

}