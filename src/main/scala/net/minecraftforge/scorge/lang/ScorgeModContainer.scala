package net.minecraftforge.scorge.lang

import java.util.function.Consumer

import net.minecraftforge.eventbus.EventBusErrorMessage
import net.minecraftforge.eventbus.api.{BusBuilder, Event, IEventBus, IEventListener}
import net.minecraftforge.fml.LifecycleEventProvider.LifecycleEvent
import net.minecraftforge.fml._
import net.minecraftforge.forgespi.language.{IModInfo, ModFileScanData}
import org.apache.logging.log4j.{LogManager, MarkerManager}

/**
  *
  * @param info
  * @param className
  * @param mcl
  * @param mfsd
  */
class ScorgeModContainer(info:IModInfo, className:String, mcl:ClassLoader, mfsd:ModFileScanData) extends ModContainer(info){


  private final val LOADING = MarkerManager.getMarker("LOADING");
  private final val LOGGER = LogManager.getLogger
  private final var eventBus:IEventBus = _
  private var modInstance:AnyRef = _
  private var modClass:Class[_] = _

  triggerMap.put(ModLoadingStage.CONSTRUCT, dummy
    .andThen(this.beforeEvent)
    .andThen(this.constructMod)
    .andThen(afterEvent)
  )

  triggerMap.put(ModLoadingStage.CREATE_REGISTRIES, dummy
    .andThen(this.beforeEvent)
    .andThen(this.fireEvent)
    .andThen(this.afterEvent)
  )

  triggerMap.put(ModLoadingStage.LOAD_REGISTRIES, dummy
    .andThen(this.beforeEvent)
    .andThen(this.fireEvent)
    .andThen(this.afterEvent)
  )

  triggerMap.put(ModLoadingStage.COMMON_SETUP, dummy
    .andThen(this.beforeEvent)
    .andThen(this.preInitMod)
    .andThen(this.fireEvent)
    .andThen(this.afterEvent)
  )

  triggerMap.put(ModLoadingStage.SIDED_SETUP, dummy
    .andThen(this.beforeEvent)
    .andThen(this.fireEvent)
    .andThen(this.afterEvent)
  )

  triggerMap.put(ModLoadingStage.ENQUEUE_IMC, dummy
    .andThen(this.beforeEvent)
    .andThen(this.initMod)
    .andThen(this.fireEvent)
    .andThen(this.afterEvent)
  )

  triggerMap.put(ModLoadingStage.PROCESS_IMC, dummy
    .andThen(this.beforeEvent)
    .andThen(this.fireEvent)
    .andThen(this.afterEvent)
  )

  triggerMap.put(ModLoadingStage.COMPLETE, dummy
    .andThen(this.beforeEvent)
    .andThen(this.completeLoading)
    .andThen(this.fireEvent)
    .andThen(this.afterEvent)
  )

  this.eventBus = BusBuilder.builder.setExceptionHandler(this.onEventFailed).setTrackPhases(false).build
  final  val context = new ScorgeModLoadingContext(this)

  //CPW and his suppliers
  this.contextExtension = () => context

  try {
    modClass = Class.forName(className, true, mcl)
    LOGGER.debug(LOADING,"Loaded modclass {} with {}", modClass.getName:String, modClass.getClassLoader:Any)
  } catch {
    case e: Throwable =>
      LOGGER.error(LOADING, "Failed to load class {}", className:Any, e)
      throw new ModLoadingException(info, ModLoadingStage.CONSTRUCT, "Wut!?!", e)
  }

  private def dummy: Consumer[LifecycleEvent] = _ => {}

  private def completeLoading(lifecycleEvent:LifecycleEvent): Unit = {

  }

  private def initMod(lifecycleEvent:LifecycleEvent): Unit = {

  }

  private def onEventFailed(eventBus:IEventBus, event:Event, eventListener:Array[IEventListener], i:Int, throwable:Throwable): Unit = {
    LOGGER.error(new EventBusErrorMessage(event, i, eventListener, throwable):Any)
  }

  private def beforeEvent(lifecycleEvent:LifecycleEvent): Unit = {
  }

  private def fireEvent(lifecycleEvent:LifecycleEvent): Unit = {
    val event = lifecycleEvent.getOrBuildEvent(this)
    LOGGER.debug(LOADING, "Firing event for modid {} : {}", this.getModId:Any, event.getClass.getName:Any)

    try {
      eventBus.post(event)
      LOGGER.debug(LOADING, "Fired event for modid {} : {}", this.modId:Any, event.getClass.getName:Any)
    } catch {
      case e:Throwable => throw new ModLoadingException(modInfo, lifecycleEvent.fromStage(), "error firing event", e)
    }
  }

  private def afterEvent(lifecycleEvent:LifecycleEvent): Unit = {
    if (getCurrentState == ModLoadingStage.ERROR) {
      LOGGER.error(LOADING,"An error occurred while dispatching event {} to {}", lifecycleEvent.fromStage:Any, getMod:Any)
    }
  }

  private def preInitMod(lifecycleEvent:LifecycleEvent): Unit = {}

  private def constructMod(lifecycleEvent:LifecycleEvent): Unit = {
    try {
      LOGGER.debug(LOADING, "Loading mod instance {} of type {}", getModId:Any, modClass.getName:Any)
      //TODO I dont like this but the compiler will complain otherwise
      this.modInstance = modClass.newInstance()
      LOGGER.debug(LOADING, "Loaded mod instance {} of type {}", getModId:Any, modClass.getName:Any)
    } catch {
      case e: Throwable => {
        LOGGER.error(LOADING, "Failed to create mod instance. ModId {} for class {}", getModId:Any, modClass.getName:Any, e:Any)
        throw new ModLoadingException(modInfo, lifecycleEvent.fromStage,"Failed to load mod", e, modClass)
      }
    }
  }

  override def matches(mod:AnyRef): Boolean = mod == modInstance

  override def getMod:AnyRef = this.modInstance

  def getEventBus:IEventBus = this.eventBus
}
