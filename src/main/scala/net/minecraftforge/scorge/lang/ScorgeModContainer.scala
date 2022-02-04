package net.minecraftforge.scorge.lang

// format: off
import java.util.function.Consumer
import net.minecraftforge.eventbus.EventBusErrorMessage
import net.minecraftforge.eventbus.api.{BusBuilder, Event, IEventBus, IEventListener}
import net.minecraftforge.fml.{ModContainer, ModLoadingException, ModLoadingStage}
import net.minecraftforge.forgespi.language.{IModInfo, ModFileScanData}

import org.apache.logging.log4j.{LogManager, MarkerManager}
import java.util.Optional
import net.minecraftforge.fml.event.IModBusEvent

class ScorgeModContainer(info:IModInfo, className:String, mfsd:ModFileScanData, gl: ModuleLayer) extends ModContainer(info){

  private final val LOADING = MarkerManager.getMarker("LOADING");
  private final val LOGGER = LogManager.getLogger
  private final var eventBus:IEventBus = _
  private var modInstance:AnyRef = _
  private final var modClass:Class[_] = _

  LOGGER.debug(LOADING, "Creating ScorgeModContainer instance for {} with gameLayer {} and classLoader {}", className:Any, gl:Any, getClass.getClassLoader:Any)
  activityMap.put(ModLoadingStage.CONSTRUCT, constructMod _)

  this.eventBus = BusBuilder.builder.setExceptionHandler(this.onEventFailed).setTrackPhases(false).build
  this.configHandler = Optional.of(event => this.eventBus.post(event.self))
  final val context = new ScorgeModLoadingContext(this)

  //CPW and his suppliers
  this.contextExtension = () => context

  try {
    val layer = gl.findModule(info.getOwningFile.moduleName).orElseThrow;
    modClass = Class.forName(layer, className)
    LOGGER.debug(LOADING,"Loaded modClass {} with {}", modClass.getName:String, modClass.getClassLoader:Any)
  } catch {
    case e: Throwable =>
      LOGGER.error(LOADING, "Failed to load class {}", className:Any, e)
      throw new ModLoadingException(info, ModLoadingStage.CONSTRUCT, "Wut!?!", e)
  }

  private def onEventFailed(eventBus:IEventBus, event:Event, eventListener:Array[IEventListener], i:Int, throwable:Throwable): Unit = {
    LOGGER.error(new EventBusErrorMessage(event, i, eventListener, throwable):Any)
  }

  private def constructMod(): Unit = {
    try {
      LOGGER.debug(LOADING, "Loading mod instance {} of type {}", getModId:Any, modClass.getName:Any)
      this.modInstance = modClass.getDeclaredConstructor().newInstance().asInstanceOf[AnyRef]
      LOGGER.debug(LOADING, "Loaded mod instance {} of type {}", getModId:Any, modClass.getName:Any)
    } catch {
      case e: Throwable => {
        LOGGER.error(LOADING, "Failed to create mod instance. ModId {} for class {}", getModId:Any, modClass.getName:Any, e:Any)
        throw new ModLoadingException(modInfo, ModLoadingStage.CONSTRUCT,"Failed to load mod", e, modClass)
      }
    }
  }

  override def matches(mod:AnyRef): Boolean = mod == modInstance

  override def getMod:AnyRef = this.modInstance

  def getEventBus:IEventBus = this.eventBus

  override protected def acceptEvent[T <: Event with IModBusEvent](e: T): Unit = {
    try {
      LOGGER.debug(LOADING, "Firing event for modid {} : {}", this.getModId:Any, e:Any)
      getEventBus.post(e)
      LOGGER.debug(LOADING, "Fired event for modid {} : {}", this.getModId:Any, e:Any)
    } catch {
      case t: Throwable => {
        LOGGER.error(LOADING,"Caught exception during event {} dispatch for modid {}", e:Any, this.getModId:Any, t:Any);
        throw new ModLoadingException(modInfo, modLoadingStage, "fml.modloading.errorduringevent", t)
      }
    }
  }
}

// format: on
