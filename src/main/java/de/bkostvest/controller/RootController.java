package de.bkostvest.controller;

import static j2html.TagCreator.*;

import de.bkostvest.common.Htmx;
import io.javalin.http.*;
import j2html.tags.specialized.DivTag;
import j2html.tags.specialized.HtmlTag;

public class RootController {

	private final String view;

	public RootController() {
		view = replaceMain(MainView()).render();
	}

	public void get(Context ctx) {
		ctx.contentType(ContentType.TEXT_HTML);
		ctx.result(view);
	}

	public HtmlTag replaceMain(DivTag content) {
		return HTML(content);
	}

	public static HtmlTag HTML(DivTag content) {
		return html(
			head(
				meta().withCharset("utf-8"),
				meta().attr("name=description").withContent("Demo"),
				script().withSrc("htmx.js"),
				script().withSrc("htmx-ext-sse.js"),
				link().withRel("stylesheet").withHref("styles.css")
			),
			body(
				header(a(h1("Planning-Poker")).withHref("/"), nav()),
				main(
					content
					// SSEController.ChatView(),
					// SSEController.MessageSendFormView()
				).withId("main")
			)
		).withId("html");
	}

	public DivTag MainView() {
		return div(
			JoinGameButton(),
			CreateNewGameButton()
		).withClasses("d-flex space-around");
	}

	public DivTag JoinGameButton() {
		return div(
			text("Join Game")
		).withClass("basic-button")
		.attr(Htmx.GetAndReplace("/join", "#main"));
	}

	public DivTag CreateNewGameButton() {
		return div(
			text("Create New Game")
		).withClass("basic-button")
		.attr(Htmx.GetAndReplace("/create", "#main"));
	}
}
