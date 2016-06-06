package util

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

/**
  * Created by Harold on 2016-06-04.
  */
class BaseRepo(protected val dbConfigProvider: DatabaseConfigProvider) {
  protected val db = dbConfigProvider.get[JdbcProfile].db
}
