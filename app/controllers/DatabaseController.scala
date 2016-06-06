package controllers

import javax.inject.Inject

import models.{Article, ArticleRepo, PinboardArticleRepo, PinboardRepo}
import play.api.mvc.{Action, Controller}
import slick.driver.H2Driver.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Harold on 2016-06-05.
  * used as a dirty hack to generate a SQL evolution file
  * see also: https://www.playframework.com/documentation/2.5.x/Evolutions
  */
class DatabaseController @Inject() (articleRepo: ArticleRepo,
                                    pinboardRepo: PinboardRepo,
                                    pinboardArticleRepo: PinboardArticleRepo) extends Controller {

  def generateDDL = {
    val schemas = articleRepo.articles.schema ++ pinboardRepo.pinboards.schema ++ pinboardArticleRepo.pinboardArticles.schema
    var ddl = "# --- !Ups" + System.lineSeparator()
    ddl += schemas.createStatements.foldLeft("")((ddl, statement) => ddl + statement + ";" + System.lineSeparator())
    ddl += System.lineSeparator() + "# --- !Downs" + System.lineSeparator()
    ddl += schemas.dropStatements.foldLeft("")((ddl, statement) => ddl + statement + ";" + System.lineSeparator())
    Action {
      Ok(ddl).as("text/plain")
    }
  }

  def insertArticles = {
    val listArticles = List(Article(0, "hello", "world"), Article(0, "Good bye", "world"))
    Action {
      Ok("Articles added: " + Await.result(articleRepo.addListArticle(listArticles), Duration.Inf)).as("text/plain")
    }
  }
}
