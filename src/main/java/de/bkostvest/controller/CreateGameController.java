package de.bkostvest.controller;

import de.bkostvest.common.StaticPartialHtmlController;
import j2html.tags.specialized.*;

import java.util.function.Function;

import static j2html.TagCreator.*;

public class CreateGameController extends StaticPartialHtmlController {
	public CreateGameController(Function<DivTag, HtmlTag> replaceMain, String route) {
        super(replaceMain, route);
    }

    @Override
    public DivTag view() {
        return CreateGameView();
    }

	//thema, zeit limit, max spieler
	public DivTag CreateGameView() {
		return div(
			form(
				input()
					.withType("text")
					.withName("theme")
					.withPlaceholder("Theme")
					.withClass("join-game-input"),
				input()
					.withType("number")
					.withName("timeLimit")
					.withPlaceholder("Time Limit")
					.withClass("join-game-input"),
				input()
					.withType("number")
					.withName("maxPlayers")
					.withPlaceholder("Max Players")
					.withClass("join-game-input"),
				button("Create Game").withType("submit").withClass("basic-button")
			)
		);
	}
}
