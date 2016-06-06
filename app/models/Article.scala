package models

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{Json, Writes}
import slick.lifted.Tag
import slick.driver.H2Driver.api._
import util.BaseRepo

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by Harold on 2016-06-04.
  */
case class Article(id: Long, title: String, body: String)
object Article {
  // http://stackoverflow.com/questions/22367092/using-tupled-method-when-companion-object-is-in-class
  def tupled = (Article.apply _).tupled

  implicit val ArticleWrites = new Writes[Article] {
    def writes(article: Article) = Json.obj(
      "id" -> article.id,
      "title" -> article.title,
      "body" -> article.body
    )
  }
}
class ArticleTable(tag: Tag) extends Table[Article](tag, "article") {
  val id = column[Long]("id", O.AutoInc, O.PrimaryKey)
  val title = column[String]("title")
  val body = column[String]("body")

  override def * = (id, title, body) <> (Article.tupled, Article.unapply)
}
class ArticleRepo @Inject()(dbConfigProvider: DatabaseConfigProvider) extends BaseRepo(dbConfigProvider) {
  val articles = TableQuery[ArticleTable]

  def addArticle(title: String, body: String): Future[Article] = {
    val article = Article(0, title, body)
    val action = articles returning articles.map(_.id) into ((a, id) => a.copy(id = id))
    db.run(action += article)
  }

  def addListArticle(listArticles: Seq[Article]): Future[Option[Int]] ={
    db.run(articles ++= listArticles)
  }

  def deleteArticle(id: Long): Future[Int] = {
    val action = articles.filter(_.id === id).delete
    db.run(action)
  }

  def listArticles: Future[Seq[(String, String)]] = {
    val action = articles.map(article => (article.title, article.body))
    db.run(action.result)
  }
}
