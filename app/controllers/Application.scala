package controllers

import play.api.mvc.{Action, Controller}

/**
  * Created by Harold on 2016-06-04.
  */
class Application extends Controller {

    def index = Action {
      Redirect(routes.TestController.index())
    }
}
