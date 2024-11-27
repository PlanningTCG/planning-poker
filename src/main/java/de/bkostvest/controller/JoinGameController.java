package de.bkostvest.controller;

import de.bkostvest.common.StaticPartialHtmlController;
import j2html.tags.specialized.*;

import java.util.function.Function;

import static j2html.TagCreator.*;

public class JoinGameController extends StaticPartialHtmlController {
	public JoinGameController(Function<DivTag, HtmlTag> replaceMain, String route) {
        super(replaceMain, route);
    }

    @Override
    public DivTag view() {
        return JoinGameView();
    }

	public DivTag JoinGameView() {
		return div(
			form(
				input()
					.withType("text")
					.withName("gameId")
					.withPlaceholder("Game ID")
					.withClass("join-game-input"),
				button("Join Game").withType("submit").withClass("basic-button")
			)
		);
	}
}
