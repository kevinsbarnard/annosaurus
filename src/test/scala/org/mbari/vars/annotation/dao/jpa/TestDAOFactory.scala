/*
 * Copyright 2017 Monterey Bay Aquarium Research Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mbari.vars.annotation.dao.jpa

import java.util.concurrent.TimeUnit
import javax.persistence.EntityManagerFactory

import com.typesafe.config.ConfigFactory
import org.eclipse.persistence.config.TargetDatabase

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * @author Brian Schlining
  * @since 2017-01-23T15:32:00
  */
object TestDAOFactory {

  val TestProperties = Map(
    "eclipselink.logging.level.sql"                             -> "FINE",
    "eclipselink.logging.parameters"                            -> "true",
    "eclipselink.logging.level"                                 -> "INFO",
    "javax.persistence.schema-generation.scripts.action"        -> "drop-and-create",
    "javax.persistence.schema-generation.scripts.create-target" -> "target/test-database-create.ddl",
    "javax.persistence.schema-generation.scripts.drop-target"   -> "target/test-database-drop.ddl"
  )

  //val Instance: SpecDAOFactory = DerbyTestDAOFactory.asInstanceOf[SpecDAOFactory]
  val Instance: SpecDAOFactory = DevelopmentTestDAOFactory.asInstanceOf[SpecDAOFactory]

  def cleanup(): Unit = Instance.cleanup()

}

trait SpecDAOFactory extends JPADAOFactory {

  lazy val config = ConfigFactory.load()

  def cleanup(): Unit = {

    import scala.concurrent.ExecutionContext.Implicits.global
    val dao = newImagedMomentDAO()

    val f = dao.runTransaction(_ => {
      val all = dao.findAll()
      all.foreach(dao.delete)
    })
    f.onComplete(_ => dao.close())
    Await.result(f, Duration(400, TimeUnit.SECONDS))

  }

  def testProps(): Map[String, String]
}

object DerbyTestDAOFactory extends SpecDAOFactory {

  override def testProps(): Map[String, String] =
    TestDAOFactory.TestProperties ++
      Map(
        "eclipselink.target-database"             -> TargetDatabase.Derby,
        "javax.persistence.database-product-name" -> TargetDatabase.Derby
      )

  lazy val entityManagerFactory: EntityManagerFactory = {
    val driver   = config.getString("org.mbari.vars.annotation.database.derby.driver")
    val url      = config.getString("org.mbari.vars.annotation.database.derby.url")
    val user     = config.getString("org.mbari.vars.annotation.database.derby.user")
    val password = config.getString("org.mbari.vars.annotation.database.derby.password")
    EntityManagerFactories(url, user, password, driver, testProps())
  }

}

object H2TestDAOFactory extends SpecDAOFactory {

  override def testProps(): Map[String, String] =
    TestDAOFactory.TestProperties ++
      Map(
        "eclipselink.target-database"             -> TargetDatabase.Derby,
        "javax.persistence.database-product-name" -> TargetDatabase.Derby
      )

  lazy val entityManagerFactory: EntityManagerFactory = {
    val driver   = config.getString("org.mbari.vars.annotation.database.h2.driver")
    val url      = config.getString("org.mbari.vars.annotation.database.h2.url")
    val user     = config.getString("org.mbari.vars.annotation.database.h2.user")
    val password = config.getString("org.mbari.vars.annotation.database.h2.password")
    EntityManagerFactories(url, user, password, driver, testProps())
  }

}

object DevelopmentTestDAOFactory extends SpecDAOFactory {

  val productName = config.getString("org.mbari.vars.annotation.database.development.name")

  override def testProps(): Map[String, String] =
    TestDAOFactory.TestProperties ++
      Map(
        "eclipselink.target-database"             -> productName,
        "javax.persistence.database-product-name" -> productName
      )

  lazy val entityManagerFactory: EntityManagerFactory = {
    val driver   = config.getString("org.mbari.vars.annotation.database.development.driver")
    val url      = config.getString("org.mbari.vars.annotation.database.development.url")
    val user     = config.getString("org.mbari.vars.annotation.database.development.user")
    val password = config.getString("org.mbari.vars.annotation.database.development.password")
    EntityManagerFactories(url, user, password, driver, testProps())
  }
}
