package controllers

import javax.inject.Inject

import models.Pinboard
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats.longFormat
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by Harold on 2016-06-06.
  * Simple front-end that allows to create and delete pinboards (will only be used internally)
  */
case class PinboardForm(name: Option[String])
case class DeletePinboardForm(id: Long)
object PinboardForm {
  val pinboardForm = Form(
    mapping(
      "name" -> optional(text)
    )(PinboardForm.apply)(PinboardForm.unapply)
  )
  val deletePinboardForm = Form(
    mapping(
      "id" -> of[Long]
    )(DeletePinboardForm.apply)(DeletePinboardForm.unapply)
  )
}
class TestController @Inject() (ws: WSClient) extends Controller {
  def index = Action {
    Ok{views.html.test(PinboardForm.pinboardForm, PinboardForm.deletePinboardForm)}
  }

  def createPinboard = Action {
    implicit request => {
      val wsrequest = ws.url("http://" + request.host + "/pinboards")
      val nameOpt = PinboardForm.pinboardForm.bindFromRequest().get.name

      val requestNameOpt = nameOpt match {
        case Some(name) => wsrequest.withBody(Map("name" -> Seq(name)))
        case None => wsrequest
      }

      val resultAsPinboard = requestNameOpt.execute("POST").map(response => response.json.as[Pinboard])
      val result = resultAsPinboard.map(pinboard => "created new pinboard with id: " + pinboard.id + " name: " + pinboard.name)
      Ok(Await.result(result, Duration.Inf))
    }
  }
  def deletePinboard = Action {
    implicit request => {
      val wsrequest = ws.url("http://" + request.host + "/pinboards/" + PinboardForm.deletePinboardForm.bindFromRequest().get.id)
      val result = wsrequest.execute("DELETE").map(response => response.body)
      Ok(Await.result(result, Duration.Inf))
    }
  }
}
