package org.mbari.vars.annotation.dao.jpa

import java.net.URL
import java.sql.Timestamp
import javax.persistence._

import com.google.gson.annotations.{Expose, SerializedName}
import org.mbari.vars.annotation.model.{ImageReference, ImagedMoment}

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-06-17T13:10:00
 */
@Entity(name = "ImageReference")
@Table(name = "image_references", indexes = Array(
  new Index(name = "idx_url", columnList = "url")
))
@EntityListeners(value = Array(classOf[TransactionLogger]))
@NamedQueries(Array(
  new NamedQuery(
    name = "ImageReference.findAll",
    query = "SELECT r FROM ImageReference r"
  ),
  new NamedQuery(
    name = "ImageReference.findByURL",
    query = "SELECT r FROM ImageReference r WHERE r.url = :url"
  )
))
class ImageReferenceImpl extends ImageReference with JPAPersistentObject {

  @Expose(serialize = true)
  @Column(
    name = "description",
    length = 256,
    nullable = true
  )
  override var description: String = _

  @Expose(serialize = true)
  @Column(
    name = "url",
    unique = true,
    length = 1024,
    nullable = false
  )
  @Convert(converter = classOf[URLConverter])
  override var url: URL = _

  @ManyToOne(
    cascade = Array(CascadeType.PERSIST, CascadeType.DETACH),
    optional = false,
    targetEntity = classOf[ImagedMomentImpl]
  )
  @JoinColumn(name = "imaged_moment_uuid", nullable = false)
  override var imagedMoment: ImagedMoment = _

  @Expose(serialize = true)
  @SerializedName(value = "height_pixels")
  @Column(
    name = "height_pixels",
    nullable = true
  )
  override var height: Int = _

  @Expose(serialize = true)
  @SerializedName(value = "width_pixels")
  @Column(
    name = "width_pixels",
    nullable = true
  )
  override var width: Int = _

  @Expose(serialize = true)
  @Column(
    name = "format",
    length = 64,
    nullable = true
  )
  override var format: String = _
}

object ImageReferenceImpl {

  def apply(
    url: URL,
    width: Option[Int] = None,
    height: Option[Int] = None,
    format: Option[String] = None,
    description: Option[String] = None
  ): ImageReferenceImpl = {
    val i = new ImageReferenceImpl()
    i.url = url
    width.foreach(i.width = _)
    height.foreach(i.height = _)
    format.foreach(i.format = _)
    description.foreach(i.description = _)
    i
  }

  def apply(ir: ImageReference): ImageReferenceImpl = ir match {
    case v: ImageReferenceImpl => v
    case v: _ =>
      val i = new ImageReferenceImpl
      i.description = v.description
      i.format = v.format
      i.height = v.height
      i.url = v.url
      i.width = v.width
      i.imagedMoment = ImagedMomentImpl(v.imagedMoment)
      i.uuid = v.uuid
      v.lastUpdated.foreach(t => i.lastUpdatedTime = new Timestamp(t.getEpochSecond))
      i
  }
}
