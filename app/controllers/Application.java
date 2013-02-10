package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

	@SubjectPresent
    public static Result index() {
        return ok(index.render());
    }
    // -- Javascript routing

	@SubjectPresent
    public static Result javascriptRoutes() {
        response().setContentType("text/javascript");
        return ok(
                Routes.javascriptRouter("jsRoutes",
                        controllers.routes.javascript.Application.index())

        );
    }
}
