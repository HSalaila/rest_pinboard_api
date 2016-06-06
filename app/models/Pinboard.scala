package models

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import slick.lifted.{TableQuery, Tag}
import slick.driver.H2Driver.api._
import _root_.util.BaseRepo

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by Harold on 2016-06-04.
  */

case class Pinboard(id: Long, name: String)
object Pinboard {
  // http://stackoverflow.com/questions/22367092/using-tupled-method-when-companion-object-is-in-class
  def tupled = (Pinboard.apply _).tupled

  implicit val pinboardWrites = new Writes[Pinboard] {
    def writes(pinboard: Pinboard) = Json.obj(
      "id" -> pinboard.id,
      "name" -> pinboard.name
    )
  }
}
class PinboardTable(tag: Tag) extends Table[Pinboard](tag, "pinboard") {
  def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
  def name = column[String]("name")

  override def * = (id, name) <> (Pinboard.tupled, Pinboard.unapply)
}
class PinboardRepo @Inject()(dbConfigProvider: DatabaseConfigProvider) extends BaseRepo(dbConfigProvider) {
  val pinboards = TableQuery[PinboardTable]

  def addPinboard(name: String): Future[Pinboard] = {
    val pinboard = Pinboard(0, name)
    val action = pinboards returning pinboards.map(_.id) into ((p, id) => p.copy(id = id))
    db.run(action += pinboard)
  }

  def deletePinboard(id: Long): Future[Int] = {
    val action = pinboards.filter(_.id === id).delete
    db.run(action)
  }

  def listPinboards: Future[Seq[(Long, String)]] = {
    val action = pinboards.map(pinboard => (pinboard.id, pinboard.name))
    db.run(action.result)
  }
}