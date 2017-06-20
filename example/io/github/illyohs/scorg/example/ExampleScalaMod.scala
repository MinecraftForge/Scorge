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
package io.github.illyohs.scorg.example

@Mod(name = "ExampleScalaMod", modid = "examplescalamod", version = "1.0.0", modLanguageAdapter = "io.github.illyohs.scorg.ScalaAdapter")
//This must be an object block
object ExampleScalaMod {

  @Instance("examplescalamod")
  var instance = this

  @SidedProxy(modId = "examplescalamod", serverSide = "io.github.illyohs.scorg.example.ExampleServerProxy", clientSide = "io.github.illyohs.scorg.example.ExampleServerProxy")
  var proxy:ExampleServerProxy = null

  @EventHandler
  def preInit(event:FMLPreInitializationEvent): Unit = {

  }

  @EventHandler
  def init(event:FMLInitializationEvent): Unit = {

  }

  @EventHandler
  def postInit(event:FMLPostInitializationEvent): Unit = {

  }
}
