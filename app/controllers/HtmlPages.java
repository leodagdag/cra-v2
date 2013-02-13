package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.layout.unsupportedBrowser;

/**
 * @author f.patin
 */
public class HtmlPages extends Controller {

	public static Result unsupportedBrowser() {
		return ok(unsupportedBrowser.render());
	}

}
