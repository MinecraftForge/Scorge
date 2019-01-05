package net.minecraftforge.scorge.lang

import java.util.function.Consumer

import net.minecraftforge.eventbus.EventBusErrorMessage
import net.minecraftforge.eventbus.api.{Event, IEventBus, IEventListener}
import net.minecraftforge.fml.LifecycleEventProvider.LifecycleEvent
import net.minecraftforge.fml.Logging._
import net.minecraftforge.fml._
import org.apache.logging.log4j.LogManager

/**
  *
  * @param info
  * @param className
  * @param mcl
  * @param mfsd
  */
class ScorgeModContainer(info:IModInfo, className:String, mcl:ClassLoader, mfsd:ModFileScanData) extends ModContainer (info){

  private final val LOGGER = LogManager.getLogger
  private final var eventBus:IEventBus = _
  private var modInstance:Any = _
  private final var modClass:Class[_] = _

  triggerMap.put(ModLoadingStage.CONSTRUCT, dummy
    .andThen(this.beforeEvent)
    .andThen(this.constructMod)
    .andThen(afterEvent)
  )

  triggerMap.put(ModLoadingStage.PREINIT, dummy
    .andThen(this.beforeEvent)
    .andThen(this.preInitMod)
    .andThen(this.fireEvent)
    .andThen(this.afterEvent)
  )


  this.eventBus = IEventBus.create(this.onEventFailed)

  try {
    modClass = Class.forName(className, true, mcl)
    LOGGER.debug(LOADING, "Loaded modclass {} with {}", modClass.getName, modClass.getClassLoader)
  } catch {
    case e: Throwable =>
      LOGGER.error(LOADING, "Failed to load class {}", className, e)
      throw new ModLoadingException(info, ModLoadingStage.CONSTRUCT, "Wut!?!", e)
  }

  private def dummy: Consumer[LifecycleEvent] = _ => {}

  private def completeLoading(lifecycleEvent:LifecycleEvent): Unit = {

  }

  private def initMod(lifecycleEvent:LifecycleEvent): Unit = {

  }

  private def onEventFailed(eventBus:IEventBus, event:Event, eventListener:Array[IEventListener], i:Int, throwable:Throwable): Unit = {
    LOGGER.error(new EventBusErrorMessage(event, i, eventListener, throwable))
  }

  private def beforeEvent(lifecycleEvent:LifecycleEvent): Unit = {
    ScorgeModloadingContext.get.setActiveContainer(this)
    ModThreadContext.get.setActiveContainer(this)
  }

  private def fireEvent(lifecycleEvent:LifecycleEvent): Unit = {
    val event =lifecycleEvent.buildModEvent(this)
    LOGGER.debug(LOADING, "Firing event for modid {} : {}", this.getModId, event.getClass.getName)

    try {
      eventBus.post(event)
      LOGGER.debug(LOADING, "Fired event for modid {} : {}", this.modId, event.getClass.getName)
    } catch {
      case e:Throwable => throw new ModLoadingException(modInfo, lifecycleEvent.fromStage(), "error firing event", e)
    }
  }

  private def afterEvent(lifecycleEvent:LifecycleEvent): Unit = {
    ModThreadContext.get.setActiveContainer(null)
    ScorgeModloadingContext.get.setActiveContainer(null)
    if (getCurrentState == ModLoadingStage.ERROR) {
      LOGGER.error(LOADING,"An error occurred while dispatching event {} to {}", lifecycleEvent.fromStage(), getModId());
    }
  }

  //TODO not sure if this will work with scala will have to re-evaluate when forge mod loading actually works
  //Also should scorge work with magic annotaions or should I find a more scala way of doing thigns
  private def preInitMod(lifecycleEvent:LifecycleEvent): Unit = {
    LOGGER.debug(LOADING, "Injecting Automatic event subscribers for {}", getModId)
    AutomaticEventSubscriber.inject(this, mfsd, mcl)
    LOGGER.debug(LOADING, "Completed Automatic event subscriptions for {}", getModId)
  }

  private def constructMod(lifecycleEvent:LifecycleEvent): Unit = {
    try {
      LOGGER.debug(LOADING, "Loading mod instance {} of type {}", getModId, modClass.getName)
      this.modInstance = modClass.newInstance
      LOGGER.debug(LOADING, "Loaded mod instance {} of tupe {}", getModId, modClass.getName)
    } catch {
      case e: Throwable => {
        LOGGER.error(LOADING, "Failed to create mod instance. ModId {} for class {}", getModId, modClass.getName, e)
        throw new ModLoadingException(modInfo, lifecycleEvent.fromStage,"Failed to load mod", e, modClass)
      }
    }
  }

  override def matches(mod:Any): Boolean = mod == modInstance

  override def getMod: Any = this.modInstance

  def getEventBus:IEventBus = this.eventBus
}
