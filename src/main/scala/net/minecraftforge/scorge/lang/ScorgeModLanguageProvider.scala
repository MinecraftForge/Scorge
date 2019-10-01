package net.minecraftforge.scorge.lang

// format: off
import java.lang.reflect.InvocationTargetException
import java.util
import java.util.function.{Consumer, Supplier}

import net.minecraftforge.forgespi.language.IModLanguageProvider.IModLanguageLoader
import net.minecraftforge.forgespi.language.{ILifecycleEvent, IModInfo, IModLanguageProvider, ModFileScanData}
import net.minecraftforge.scorge.lang.ScorgeModLanguageProvider.ScorgeModTarget

import org.apache.logging.log4j.LogManager

import scala.beans.BeanProperty

object ScorgeModLanguageProvider {

  private val LOGGER = LogManager.getLogger("Loading")

  class ScorgeModTarget(className:String, @BeanProperty modId:String) extends IModLanguageLoader {
    override def loadMod[T](info: IModInfo, modClassLoader: ClassLoader, modFileScanResults: ModFileScanData): T = {
      try {

        val scorgeContainer: Class[_] = Class.forName("net.minecraftforge.scorge.lang.ScorgeModContainer",
          true, Thread.currentThread().getContextClassLoader)
        val constructor =scorgeContainer.getConstructor(classOf[IModInfo], classOf[String], classOf[ClassLoader], classOf[ModFileScanData])
        constructor.newInstance(info, className, modClassLoader, modFileScanResults).asInstanceOf[T]
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
    scanResult.getIModInfoData.forEach(infos => infos.getMods.forEach(imds => {
      val modID = imds.getModId
      val entry = imds.getModConfig.get("entryClass").asInstanceOf[String]
      LOGGER.debug("Loading mod {} from entryClass {}", modID:Any, entry:Any)
      targetMap.put(modID, new ScorgeModTarget(entry, modID))
    }))
    //Put info into target map
    scanResult.addLanguageLoader(targetMap)
  }

  override def consumeLifecycleEvent[R <:ILifecycleEvent[R]](consumeEvent:Supplier[R]): Unit = {}
}
// format: on
