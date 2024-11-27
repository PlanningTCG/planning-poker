package de.bkostvest.controller;

import de.bkostvest.common.StaticPartialHtmlController;
import j2html.tags.specialized.*;

import java.util.function.Function;

import static j2html.TagCreator.*;

public class HomeController extends StaticPartialHtmlController {
    public HomeController(Function<DivTag, HtmlTag> replaceMain, String route) {
        super(replaceMain, route);
    }

    @Override
    public DivTag view() {
        return div(
        	JoinGameButton(),
         	CreateNewGameButton()
        ).withClasses("d-flex space-around");
    }

    public DivTag JoinGameButton() {
		return div(
			text("Join Game")
		);
	}

	 public DivTag CreateNewGameButton() {
		return div(
			text("Create New Game")
		);
	}
}
