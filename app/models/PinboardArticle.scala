package models

import javax.inject.Inject

import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.H2Driver.api._
import slick.lifted.Tag
import util.BaseRepo
import com.github.tototoshi.slick.H2JodaSupport._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by Harold on 2016-06-04.
  */
case class PinboardArticle(pinboardId: Long, articleId: Long, pinnedAt: DateTime)
class PinboardArticleTable(tag: Tag) extends Table[PinboardArticle](tag, "pinboard_article") {
  val pinboards = TableQuery[PinboardTable]
  val articles = TableQuery[ArticleTable]

  val pinboardId = column[Long]("pinboard_id")
  val articleId = column[Long]("article_id")
  val pinnedAt = column[DateTime]("pinned_at")

  val pinboardFk = foreignKey("pinboard_fk", pinboardId, pinboards)(_.id)
  val articleFk = foreignKey("article_fk", articleId, articles)(_.id)

  override def * = (pinboardId, articleId, pinnedAt) <> (PinboardArticle.tupled, PinboardArticle.unapply)
}
class PinboardArticleRepo @Inject()(dbConfigProvider: DatabaseConfigProvider) extends BaseRepo(dbConfigProvider) {
  val pinboardArticles = TableQuery[PinboardArticleTable]
  val pinboards = TableQuery[PinboardTable]
  val articles = TableQuery[ArticleTable]

  def pinArticle(pinboardId: Long, articleId: Long): Future[PinboardArticle] = {
    val pinboardArticle = PinboardArticle(pinboardId, articleId, new DateTime())
    val action = pinboardArticles += pinboardArticle
    db.run(action).map(_ => pinboardArticle)
  }

  def unpinArticle(pinboardId: Long, articleId: Long): Future[Int] = {
    val action = pinboardArticles.filter(p => p.pinboardId === pinboardId && p.articleId === articleId).delete
    db.run(action)
  }

  def listArticles(pinboardId: Long): Future[Seq[(String, String)]] = {
    val query = pinboardArticles.filter(_.pinboardId === pinboardId)
    val joinQuery = query join articles on {
      case (pa, article) => pa.articleId === article.id
    }
    val namedQuery = joinQuery.map{
      case (pa, article) => (article.title, article.body)
    }
    db.run(namedQuery.result)
  }
}
