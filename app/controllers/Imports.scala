package controllers

import java.util.UUID
import play.api.mvc.Action
import security.MyDeadboltHandler
import utils.uploader.{MissionUploader, UserUploader, CustomerUploader}

/**
 * @author f.patin
 */
object Imports extends BaseController {

  def users = Restrict(batch, new MyDeadboltHandler()) {
    Action(parse.multipartFormData) {
      request =>
        request.body.file("file").map {
          file =>
            import java.io.File
            val filename = s"/tmp/${UUID.randomUUID()}"
            file.ref.moveTo(new File(filename))
            val result = UserUploader.batchUpload(filename)
            Ok(s"File uploaded - insert:${result._2} / total:${result._1})")
        }.getOrElse {
          BadRequest("error : Missing file")
        }
    }
  }

  def customers = Restrict(batch, new MyDeadboltHandler()) {
    Action(parse.multipartFormData) {
      request =>
        request.body.file("file").map {
          file =>
            import java.io.File
            val filename = s"/tmp/${UUID.randomUUID()}"
            file.ref.moveTo(new File(filename))
            val result = CustomerUploader.batchUpload(filename)
            Ok(s"File uploaded - insert:${result._2} / total:${result._1})")
        }.getOrElse {
          BadRequest("error : Missing file")
        }
    }
  }

  def missions = Restrict(batch, new MyDeadboltHandler()) {
    Action(parse.multipartFormData) {
      request =>
        request.body.file("file").map {
          file =>
            import java.io.File
            val filename = s"/tmp/${UUID.randomUUID()}"
            file.ref.moveTo(new File(filename))
            val result = MissionUploader.batchUpload(filename)
            Ok(s"File uploaded - insert:${result._2} / total:${result._1})")
        }.getOrElse {
          BadRequest("error : Missing file")
        }
    }
  }
}
