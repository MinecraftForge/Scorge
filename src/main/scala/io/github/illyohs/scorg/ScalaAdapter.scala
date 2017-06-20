/*
 * Anthony Anderson (Illyohs)
 * Copyright (c) 2017.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package io.github.illyohs.scorg

import java.lang.reflect.{Field, Method}

import net.minecraftforge.fml.common._
import net.minecraftforge.fml.relauncher.Side
import org.apache.logging.log4j.{Level, LogManager}

/**
  * Created by anthony on 6/18/17.
  */
class ScalaAdapter
  extends ILanguageAdapter {

  private val log = LogManager.getLogger("ScalaAdapter")

  override def supportsStatics(): Boolean = false

  override def setProxy(target: Field, proxyTarget: Class[_], proxy:AnyRef): Unit = {

    var prox = proxyTarget
    try if (!prox.getName.endsWith("$")) {

      prox = Class.forName(proxyTarget.getName + "$", true, proxyTarget.getClassLoader)

    } catch {

      case e: ClassNotFoundException => {
        log.printf(Level.INFO, "An error occurred trying to load a proxy into %.%. Did you declare your mod as 'class' instead of 'object'?", proxyTarget.getSimpleName, target.getName)
        return
      }

    }

    val targetInstance: AnyRef = prox.getField("MODULE$").get(null)
    val setterName: String = target.getName + "_$eq"

    for (setter <- prox.getMethods) {

      val setterParameters: Array[Class[_]] = setter.getParameterTypes

      if (setterName == setter.getName && setterParameters.length == 1 && setterParameters(0).isAssignableFrom(proxy.getClass)) {

        setter.invoke(targetInstance, proxy)
        return
      }
    }

    log.printf(Level.ERROR,"Failed loading proxy into %.%, could not find setter function. Did you declare the field with 'val' instead of 'var'?", proxyTarget.getSimpleName, target.getName)
    throw new LoaderException(String.format(
        "Failed loading proxy into %s.%s, could not find setter function. Did you declare the field with 'val' instead of 'var'?",
        proxyTarget.getSimpleName,
        target.getName))

  }

  override def getNewInstance(container: FMLModContainer, objectClass: Class[_], classLoader: ClassLoader, factoryMarkedAnnotation: Method): AnyRef = {
    val sObjectClass: Class[_] = Class.forName(objectClass.getName + "$", true, classLoader)

    sObjectClass.getField("MODULE$").get(null)

  }

  override def setInternalProxies(mod: ModContainer, side: Side, loader: ClassLoader): Unit = {
    val proxyTarget:Class[_] = mod.getMod.getClass

    if (proxyTarget.getName.endsWith("$")) {

      for (target <- proxyTarget.getDeclaredFields) {

        if (target.getAnnotation(classOf[SidedProxy]) != null) {
          val targetType: String = if (side.isClient) {
              target.getAnnotation(classOf[SidedProxy]).clientSide()
            } else {
              target.getAnnotation(classOf[SidedProxy]).serverSide()
            }

          try {

            val proxy = Class.forName(targetType, true, loader).newInstance()

            if (!target.getType.isAssignableFrom(proxy.getClass)) {
              log.printf(Level.ERROR,"Attempted to load a proxy type % into %s.%s, but the types don't match", targetType, proxyTarget.getSimpleName, target.getName)
              throw new LoaderException(String.format("Attempted to load a proxy type %s into %s.%s, but the types don't match", targetType, proxyTarget.getSimpleName, target.getName))
            }

            setProxy(target, proxyTarget, proxy.asInstanceOf[AnyRef])

          } catch {
            case e:LoaderException => {
              log.printf(Level.TRACE,"An error occurred trying to load a proxy into %s.%s", proxyTarget.getSimpleName, target.getName)
              throw new LoaderException(e)
            }
          }
        }

      }
    } else {
      log.trace("Mod does not appear to be a singleton.")
    }
  }
}
