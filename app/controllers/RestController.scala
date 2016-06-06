package controllers

import javax.inject.Inject

import models.{PinboardArticleRepo, PinboardRepo, ArticleRepo}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Harold on 2016-06-05.
  */
class RestController @Inject() (pinboardRepo: PinboardRepo,
                                pinboardArticleRepo: PinboardArticleRepo,
                                articleRepo: ArticleRepo) extends Controller {
  def index = Action {
    var text = "-- pinboard repo --" + System.lineSeparator()
    text += Await.result(pinboardRepo.listPinboards, Duration.Inf).foldLeft("")((text, t) => {
      text + "id: " + t.id + " name: " + t.name + System.lineSeparator()
    })
    text += "-- pinboard article repo --" + System.lineSeparator()
    text += Await.result(pinboardArticleRepo.listPinboardArticles, Duration.Inf).foldLeft("")((text, t) => {
      text + "pinboard id: " + t.pinboardId + " article id: " + t.articleId + " pinned at: " + t.pinnedAt + System.lineSeparator()
    })
    text += "-- article repo --" + System.lineSeparator()
    text += Await.result(articleRepo.listArticles, Duration.Inf).foldLeft("")((text, t) => {
      text + "id: " + t.id + " title: " + t.title + " body: " + t.body + System.lineSeparator()
    })
    Ok(text).as("text/plain")
  }
  /*
    - POST /pinboards

    creates a new pinboard, and returns at least the id
   */
  def createPinboard = Action {
    request => {
      val map = request.body.asFormUrlEncoded.get
      val seqName = map.get("name")
      val pinboard = seqName match {
        case Some(seqName) => pinboardRepo.addPinboard(seqName.head)
        case None => pinboardRepo.addPinboard("no name")
      }
      Ok(Json.toJson(Await.result(pinboard, Duration.Inf))).as("application/json")
    }
  }

  /*
    - DELETE /pimboards/[somepinboard-id]

    deletes an existing pinboard
   */
  def deletePinboard(id: Long) = Action {
    val rows = Await.result(pinboardRepo.deletePinboard(id), Duration.Inf)
    Ok("rows deleted: " + rows).as("text/plain")
  }

  /*
    - PUT /pinboards/[somepinboard-id]/[some-article-id]

     pins an article to a pinboard
   */
  def pinArticle(pinboardId: Long, articleId: Long) = Action {
    Ok(Json.toJson(Await.result(pinboardArticleRepo.pinArticle(pinboardId, articleId), Duration.Inf)))
      .as("application/json")
  }

  /*
    - DELETE /pinboards/[somepinboard-id]/[some-article-id]

    unpins an article to a pinboard
   */
  def unpinArticle(pinboardId: Long, articleId: Long) = Action {
    Ok(Await.result(pinboardArticleRepo.unpinArticle(pinboardId, articleId), Duration.Inf).toString)
  }

  /*
    - GET /pinboards/[somepinboard-id]

    gets all the pins of one pinboard
   */
  def listArticles(pinboardId: Long) = Action {
    Ok(Await.result(pinboardArticleRepo.listArticles(pinboardId), Duration.Inf).foldLeft("")((text, t) => {
      text + "title: " + t._1 + " body: " + t._2 + System.lineSeparator()
    })).as("text/plain")
  }
}
