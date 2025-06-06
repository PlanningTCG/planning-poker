package de.bkostvest.controller;

import de.bkostvest.classes.Game;
import de.bkostvest.classes.GameList;
import de.bkostvest.common.Htmx;
import de.bkostvest.common.StaticPartialHtmlController;
import io.javalin.Javalin;
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

   	public void setAllRoutes(Javalin app) {
        this.setRoutes(app);

        app.post("/join", (ctx) -> {
        	String code = ctx.formParam("gameId");

        	Game game = GameList.getGameByJoinCode(code);

         	if (game != null && !game.isFull()) {
       			ctx.redirect("/game/" + code);
          	} else {
         		ctx.status(404);
           }
        });
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
			).attr(Htmx.PostAndReplace("/join", "#main"))
		);
	}
}
