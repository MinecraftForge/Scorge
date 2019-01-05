package net.minecraftforge.scorge.lang

import java.util.function.Consumer

import net.minecraftforge.fml.LifecycleEventProvider
import net.minecraftforge.fml.language.IModLanguageProvider.IModLanguageLoader
import net.minecraftforge.fml.language.{IModInfo, IModLanguageProvider, ModFileScanData}
import org.apache.logging.log4j.LogManager

object ScorgeModLanguageProvider extends IModLanguageProvider{

  private final val LOGGER = LogManager.getLogger

  override def name(): String = "scalafml"

  override def getFileVisitor: Consumer[ModFileScanData] = ???

  override def preLifecycleEvent(lifecycleEvent: LifecycleEventProvider.LifecycleEvent): Unit = ???

  override def postLifecycleEvent(lifecycleEvent: LifecycleEventProvider.LifecycleEvent): Unit = ???


  private class ScorgeModTarget(className:String, modId:String) extends IModLanguageLoader {

    override def loadMod[T](info: IModInfo, modClassLoader: ClassLoader, modFileScanResults: ModFileScanData): T = ???
  }
}
