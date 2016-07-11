package org.mbari.vars.annotation.model

import java.time.{ Duration, Instant }
import java.util.UUID

import org.mbari.vars.annotation.PersistentObject

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-06-15T16:53:00
 */
trait Observation extends PersistentObject {

  var uuid: UUID
  var imagedMoment: ImagedMoment

  /**
   * A Concept is the term used for the annotation. For science purposes, this is
   * typically the name of the object, such as a species name
   */
  var concept: String

  /**
   * The duration can be used to capture how long an observation existed.
   */
  var duration: Duration

  /**
   * At MBARI, we have set-up mulitple databases for different annotations. For
   * example, one database for ROV video tapes, another for video files, and yet
   * another images. Going forward, we could store all of these in the same
   * database, but organize them using this group tag.
   */
  var group: String

  /**
   * An ID for the person or software that made the observation
   */
  var observer: String

  /**
   * The date that the observation was made
   */
  var observationDate: Instant
  def lastUpdated: Option[Instant]
  def addAssociation(association: Association): Unit
  def removeAssociation(association: Association): Unit
  def associations: Iterable[Association]
}
