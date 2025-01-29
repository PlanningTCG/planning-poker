package de.bkostvest.controller;

import de.bkostvest.common.*;
import de.bkostvest.classes.Game;
import de.bkostvest.classes.GameList;
import io.javalin.Javalin;
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

   	public void setAllRoutes(Javalin app) {
        this.setRoutes(app);

        app.post("/create", (ctx) -> {
        	String theme = ctx.formParam("theme");
			int timeLimit = Integer.parseInt(ctx.formParam("timeLimit"));
			int maxPlayers = Integer.parseInt(ctx.formParam("maxPlayers"));

			Game game = new Game(theme, timeLimit, maxPlayers);
			GameList.Gamelist.add(game);

			ctx.redirect("/game/" + game.joinCode);
        });
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
			).attr(Htmx.PostAndReplace("/create", "#main"))
		);
	}
}
