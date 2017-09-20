package org.mbari.vars.annotation.dao.jpa

import java.sql.Timestamp
import java.time.{Duration, Instant}
import java.util.UUID
import javax.persistence.{Convert, Index, _}
import java.util.{ArrayList => JArrayList, List => JList}

import com.google.gson.annotations.{Expose, SerializedName}
import org.mbari.vars.annotation.Constants
import org.mbari.vars.annotation.model.{CachedAncillaryDatum, ImageReference, ImagedMoment, Observation}
import org.mbari.vcr4j.time.Timecode

import scala.collection.JavaConverters._

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-06-16T14:12:00
 */
@Entity(name = "ImagedMoment")
@Table(name = "imaged_moments", indexes = Array(
  new Index(name = "idx_recorded_timestamp", columnList = "recorded_timestamp"),
  new Index(name = "idx_elapsed_time", columnList = "elapsed_time_millis"),
  new Index(name = "idx_timecode", columnList = "timecode")
))
@EntityListeners(value = Array(classOf[TransactionLogger]))
@NamedNativeQueries(Array(
  new NamedNativeQuery(
    name = "ImagedMoment.findAllVideoReferenceUUIDs",
    query = "SELECT DISTINCT video_reference_uuid FROM imaged_moments ORDER BY video_reference_uuid ASC"
  )
))
@NamedQueries(Array(
  new NamedQuery(
    name = "ImagedMoment.findAll",
    query = "SELECT i FROM ImagedMoment i"
  ),
  new NamedQuery(
    name = "ImagedMoment.findByVideoReferenceUUID",
    query = "SELECT i FROM ImagedMoment i WHERE i.videoReferenceUUID = :uuid"
  ),
  new NamedQuery(
    name = "ImagedMoment.findWithImageReferences",
    query = "SELECT i FROM ImagedMoment i LEFT JOIN i.javaImageReferences r WHERE i.videoReferenceUUID = :uuid"
  ),
  new NamedQuery(
    name = "ImagedMoment.findByObservationUUID",
    query = "SELECT i FROM ImagedMoment i LEFT JOIN i.javaObservations o WHERE o.uuid = :uuid"
  ),
  new NamedQuery(
    name = "ImagedMoment.findByUUID",
    query = "SELECT i FROM ImagedMoment i WHERE i.uuid = :uuid"
  ),
  new NamedQuery(
    name = "ImagedMoment.findByVideoReferenceUUIDAndTimecode",
    query = "SELECT i FROM ImagedMoment i WHERE i.timecode = :timecode AND i.videoReferenceUUID = :uuid"
  ),
  new NamedQuery(
    name = "ImagedMoment.findByVideoReferenceUUIDAndElapsedTime",
    query = "SELECT i FROM ImagedMoment i WHERE i.elapsedTime = :elapsedTime AND i.videoReferenceUUID = :uuid"
  ),
  new NamedQuery(
    name = "ImagedMoment.findByVideoReferenceUUIDAndRecordedDate",
    query = "SELECT i FROM ImagedMoment i WHERE i.recordedDate = :recordedDate AND i.videoReferenceUUID = :uuid"
  ),
  new NamedQuery(
    name = "ImagedMoment.deleteByVideoReferenceUUID",
    query = "DELETE FROM ImagedMoment i WHERE i.videoReferenceUUID = :uuid"
  ),
  new NamedQuery(
    name = "ImagedMoment.findByImageReferenceUUID",
    query = "SELECT i FROM ImagedMoment i LEFT JOIN i.javaImageReferences r WHERE r.uuid = :uuid"
  )
))
class ImagedMomentImpl extends ImagedMoment with JPAPersistentObject {

  @Expose(serialize = true)
  @Column(
    name = "elapsed_time_millis",
    nullable = true
  )
  @Convert(converter = classOf[DurationConverter])
  override var elapsedTime: Duration = _

  @Expose(serialize = true)
  @Column(
    name = "recorded_timestamp",
    nullable = true
  )
  @Temporal(value = TemporalType.TIMESTAMP)
  @Convert(converter = classOf[InstantConverter])
  override var recordedDate: Instant = _

  @Expose(serialize = true)
  @Column(
    name = "timecode",
    nullable = true
  )
  @Convert(converter = classOf[TimecodeConverter])
  override var timecode: Timecode = _

  @Expose(serialize = true)
  @SerializedName(value = "video_reference_uuid")
  @Column(
    name = "video_reference_uuid",
    nullable = true
  )
  @Convert(converter = classOf[UUIDConverter])
  override var videoReferenceUUID: UUID = _

  @Expose(serialize = true)
  @SerializedName(value = "observations")
  @OneToMany(
    targetEntity = classOf[ObservationImpl],
    cascade = Array(CascadeType.ALL),
    fetch = FetchType.EAGER,
    mappedBy = "imagedMoment",
    orphanRemoval = true
  )
  protected var javaObservations: JList[ObservationImpl] = new JArrayList[ObservationImpl]

  override def addObservation(observation: Observation): Unit = {
    javaObservations.add(observation.asInstanceOf[ObservationImpl])
    observation.imagedMoment = this
  }

  override def removeObservation(observation: Observation): Unit = {
    javaObservations.remove(observation)
    observation.imagedMoment = null
  }

  override def observations: Iterable[Observation] = javaObservations.asScala

  @Expose(serialize = true)
  @SerializedName(value = "image_references")
  @OneToMany(
    targetEntity = classOf[ImageReferenceImpl],
    cascade = Array(CascadeType.ALL),
    fetch = FetchType.EAGER,
    mappedBy = "imagedMoment",
    orphanRemoval = true
  )
  protected var javaImageReferences: JList[ImageReferenceImpl] = new JArrayList[ImageReferenceImpl]

  override def addImageReference(imageReference: ImageReference): Unit = {
    javaImageReferences.add(imageReference.asInstanceOf[ImageReferenceImpl])
    imageReference.imagedMoment = this
  }

  override def imageReferences: Iterable[ImageReference] = javaImageReferences.asScala

  override def removeImageReference(imageReference: ImageReference): Unit = {
    javaImageReferences.remove(imageReference)
    imageReference.imagedMoment = null
  }

  @Expose(serialize = true)
  @SerializedName(value = "ancillary_data")
  @OneToOne(
    mappedBy = "imagedMoment",
    cascade = Array(CascadeType.ALL),
    optional = true,
    targetEntity = classOf[CachedAncillaryDatumImpl]
  )
  protected var _ancillaryDatum: CachedAncillaryDatum = _

  def ancillaryDatum = _ancillaryDatum
  def ancillaryDatum_=(ad: CachedAncillaryDatum): Unit = {
    if (_ancillaryDatum != null) _ancillaryDatum.imagedMoment = null
    _ancillaryDatum = ad
    ad.imagedMoment = this
  }

  override def toString: String = Constants.GSON.toJson(this)
}

object ImagedMomentImpl {

  def apply(
    videoReferenceUUID: Option[UUID] = None,
    recordedDate: Option[Instant] = None,
    timecode: Option[Timecode] = None,
    elapsedTime: Option[Duration] = None
  ): ImagedMomentImpl = {

    val im = new ImagedMomentImpl
    videoReferenceUUID.foreach(im.videoReferenceUUID = _)
    recordedDate.foreach(im.recordedDate = _)
    timecode.foreach(im.timecode = _)
    elapsedTime.foreach(im.elapsedTime = _)
    im
  }

  def apply(im: ImagedMoment): ImagedMomentImpl = im match {
    case v: ImagedMomentImpl => v
    case v: _ =>
      val i = new ImagedMomentImpl
      i.elapsedTime = v.elapsedTime
      i.recordedDate = v.recordedDate
      i.timecode = v.timecode
      v.observations.map(o => ObservationImpl(o))
          .foreach(o => i.addObservation(o))
      v.imageReferences.map(ir => ImageReferenceImpl(ir))
          .foreach(ir => i.addImageReference(ir))
      i.ancillaryDatum = CachedAncillaryDatumImpl(v.ancillaryDatum)
      i.videoReferenceUUID = v.videoReferenceUUID
      i.uuid = v.uuid
      v.lastUpdated.foreach(t => i.lastUpdatedTime = new Timestamp(t.getEpochSecond))
      i
  }

}
