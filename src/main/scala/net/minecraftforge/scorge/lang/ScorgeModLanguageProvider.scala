package net.minecraftforge.scorge.lang

// format: off
import java.lang.reflect.InvocationTargetException
import java.util
import java.util.function.{Consumer, Supplier}

import net.minecraftforge.forgespi.language.IModLanguageProvider.IModLanguageLoader
import net.minecraftforge.forgespi.language.{IConfigurable, ILifecycleEvent, IModInfo, IModLanguageProvider, ModFileScanData}
import net.minecraftforge.scorge.lang.ScorgeModLanguageProvider.ScorgeModTarget
import org.apache.logging.log4j.LogManager

import scala.beans.BeanProperty

object ScorgeModLanguageProvider {

  private val LOGGER = LogManager.getLogger("Loading")

  class ScorgeModTarget(className:String, @BeanProperty modId:String) extends IModLanguageLoader {
    override def loadMod[T](info: IModInfo, modFileScanResults: ModFileScanData, gameLayer: ModuleLayer): T = {
      try {
        val scorgeContainer: Class[_] = Class.forName("net.minecraftforge.scorge.lang.ScorgeModContainer",
          true, Thread.currentThread().getContextClassLoader)
        val constructor = scorgeContainer.getConstructor(classOf[IModInfo], classOf[String], classOf[ModFileScanData], classOf[ModuleLayer])
        constructor.newInstance(info, className, modFileScanResults, gameLayer).asInstanceOf[T]
      } catch {
        case e@(_: NoSuchMethodException | _: ClassNotFoundException | _: InstantiationException | _: IllegalAccessException | _: InvocationTargetException) =>
          LOGGER.fatal("Unable to load ScorgeModContainer, wat?", e:Any)
          throw new RuntimeException(e)
      }
    }

  }

}

//Import for the logger
import net.minecraftforge.scorge.lang.ScorgeModLanguageProvider._
class ScorgeModLanguageProvider extends IModLanguageProvider{

  override def name(): String = "scorge"

  override def getFileVisitor: Consumer[ModFileScanData] = scanResult => {
    val targetMap = new util.HashMap[String, ScorgeModTarget]
    //Scan the mod infos for entryObjects and mod ids
    scanResult.getIModInfoData.forEach(infos => infos.getMods.forEach {
      case ci: IConfigurable with IModInfo =>
        val modID = ci.getModId
        val entryOpt = ci.getConfigElement("entryClass")
        if (!entryOpt.isPresent) {
          LOGGER.fatal(s"Unable to find config element 'entryClass' from the root of $modID's mod config.")
          throw new RuntimeException()
        }
        LOGGER.debug("Loading mod {} from entryClass {}", modID: Any, entryOpt: Any)
        targetMap.put(modID, new ScorgeModTarget(entryOpt.get(), modID))
      case e =>
        LOGGER.fatal(s"Unable to retrieve config properties from IModInfo instance. Class: ${e.getClass.getName}")
    })
    //Put info into target map
    scanResult.addLanguageLoader(targetMap)
  }

  override def consumeLifecycleEvent[R <:ILifecycleEvent[R]](consumeEvent:Supplier[R]): Unit = {}
}
// format: on
