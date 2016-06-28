package org.mbari.vars.annotation.dao.jpa

import java.util
import javax.persistence.{ EntityManagerFactory, Persistence }

import org.eclipse.persistence.config.PersistenceUnitProperties

import scala.collection.JavaConverters._

/**
 * https://stackoverflow.com/questions/4106078/dynamic-jpa-connection
 *
 * THis factory allows us to instantiate an javax.persistence.EntityManager from the
 * basic parameters (url, driver, password, username). You can pass in a map of additional properties
 * to customize the EntityManager.
 *
 * @author Brian Schlining
 * @since 2016-05-05T17:29:00
 */
object EntityManagerFactories {

  def apply(properties: Map[String, String]): EntityManagerFactory = {
    val customProps = properties + (PersistenceUnitProperties.SESSION_CUSTOMIZER -> "org.mbari.vars.annotation.dao.jpa.UUIDSequence")
    Persistence.createEntityManagerFactory("video-asset-manager", customProps.asJava)
  }

  def apply(
    url: String,
    username: String,
    password: String,
    driverName: String,
    properties: Map[String, String] = Map.empty
  ): EntityManagerFactory = {

    val map = Map(
      "javax.persistence.jdbc.url" -> url,
      "javax.persistence.jdbc.user" -> username,
      "javax.persistence.jdbc.password" -> password,
      "javax.persistence.jdbc.driver" -> driverName
    )
    apply(map ++ properties)
  }

}