package org.mbari.vars.annotation.api

import java.util.UUID

import org.mbari.vars.annotation.controllers.CachedAncillaryDatumController
import org.scalatra.{ BadRequest, NotFound }
import org.scalatra.swagger.Swagger

import scala.concurrent.ExecutionContext

/**
 * @author Brian Schlining
 * @since 2017-05-01T13:24:00
 */
class CachedAncillaryDatumV1Api(controller: CachedAncillaryDatumController)(implicit val swagger: Swagger, val executor: ExecutionContext)
    extends APIStack {

  override protected def applicationDescription: String = "CachedAncillaryDatum API (v1)"

  before() {
    contentType = "application/json"
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }

  get("/:uuid") {
    val uuid = params.getAs[UUID]("uuid").getOrElse(halt(BadRequest("Please provide a UUID")))
    controller.findByUUID(uuid).map({
      case None => halt(NotFound(
        body = "{}",
        reason = s"An ImagedMoment with a UUID of $uuid was not found"
      ))
      case Some(v) => toJson(v)
    })
  }

  get("/imagedmoment/:uuid") {
    val uuid = params.getAs[UUID]("uuid").getOrElse(halt(BadRequest("Please provide an ImageReference UUID")))
    controller.findByImagedMomentUUID(uuid)
      .map({
        case None => halt(NotFound(reason = s"No imagereference with a uuid of $uuid was found"))
        case Some(im) => toJson(im)
      })
  }

  get("/observation/:uuid") {
    val uuid = params.getAs[UUID]("uuid").getOrElse(halt(BadRequest("Please provide an Observation UUID")))
    controller.findByObservationUUID(uuid)
      .map({
        case None => halt(NotFound(reason = s"No observation with a uuid of $uuid was found"))
        case Some(im) => toJson(im)
      })
  }

  post("/") {
    validateRequest() // Apply API security
    val imagedMomentUuid = params.getAs[UUID]("imaged_moment_uuid")
      .getOrElse(halt(BadRequest("An imaged_moment_uuid is required")))
    val latitude = params.getAs[Double]("latitude")
      .getOrElse(halt(BadRequest("A latitude is required")))
    val longitude = params.getAs[Double]("longitude")
      .getOrElse(halt(BadRequest("A longitude is required")))
    val depthMeters = params.getAs[Float]("depth_meters")
      .getOrElse(halt(BadRequest("A depth_meters is required")))
    val altitude = params.getAs[Float]("altitude_meters")
    val crs = params.get("crs")
    val salinity = params.getAs[Float]("salinity")
    val oxygenMlL = params.getAs[Float]("oxygen")
    val temperature = params.getAs[Float]("temperature_celsius")
    val pressureDbar = params.getAs[Float]("pressure_dbar")
    val lightTransmission = params.getAs[Float]("light_transmission")
    val x = params.getAs[Double]("x")
    val y = params.getAs[Double]("y")
    val z = params.getAs[Double]("z")
    val posePositionUnits = params.get("pose_position_units")
    val phi = params.getAs[Double]("phi")
    val theta = params.getAs[Double]("theta")
    val psi = params.getAs[Double]("psi")

    controller.create(imagedMomentUuid, latitude, longitude, depthMeters, altitude, crs, salinity,
      oxygenMlL, temperature, pressureDbar, lightTransmission, x, y, z, posePositionUnits, phi, theta, psi)
      .map(toJson)

  }

  put("/:uuid") {
    validateRequest() // Apply API security
    val uuid = params.getAs[UUID]("uuid").getOrElse(halt(BadRequest(
      body = "{}",
      reason = "A video reference 'uuid' parameter is required"
    )))
    val latitude = params.getAs[Double]("latitude")
    val longitude = params.getAs[Double]("longitude")
    val depthMeters = params.getAs[Float]("depth_meters")
    val altitudeMeters = params.getAs[Float]("altitude_meters")
    val crs = params.get("crs")
    val salinity = params.getAs[Float]("salinity")
    val oxygenMlL = params.getAs[Float]("oxygen")
    val temperature = params.getAs[Float]("temperature_celsius")
    val pressureDbar = params.getAs[Float]("pressure_dbar")
    val lightTransmission = params.getAs[Float]("light_transmission")
    val x = params.getAs[Double]("x")
    val y = params.getAs[Double]("y")
    val z = params.getAs[Double]("z")
    val posePositionUnits = params.get("pose_position_units")
    val phi = params.getAs[Double]("phi")
    val theta = params.getAs[Double]("theta")
    val psi = params.getAs[Double]("psi")

    controller.update(uuid, latitude, longitude, depthMeters, altitudeMeters, crs, salinity,
      oxygenMlL, temperature, pressureDbar, lightTransmission, x, y, z, posePositionUnits, phi, theta, psi)
      .map({
        case None => halt(NotFound(reason = s"A CachedAncillaryDatm with uuid of $uuid was not found"))
        case Some(v) => toJson(v)
      })
  }

}