package de.bkostvest.controller;

import de.bkostvest.classes.Game;
import de.bkostvest.classes.GameList;
import de.bkostvest.common.Htmx;
import de.bkostvest.common.StaticPartialHtmlController;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import j2html.tags.specialized.*;

import java.util.Optional;
import java.util.function.Function;

import static j2html.TagCreator.*;

public class GameController extends StaticPartialHtmlController {
	public GameController(Function<DivTag, HtmlTag> replaceMain, String route) {
        super(replaceMain, route);
    }


   	public void setAllRoutes(Javalin app) {
        this.setRoutes(app);

        app.get("/game/{joinCode}", (ctx) -> {
			String joinCode = ctx.pathParam("joinCode");

			Game foundGame = GameList.getGameByJoinCode(joinCode);

			if (foundGame != null) {
				//javalin get current session

				foundGame.addPlayer(ctx.req().getSession());
				this.get(ctx, GameView(foundGame).render());
			} else {
				ctx.status(404);
			}
		});

    }

    @Override
    public DivTag view() {
        return div();
    }

	public DivTag GameView(Game game) {
		return div(
			div(
				h1(game.theme),
				h2("Time Limit: " + game.timeLimit),
				h2("Players: " + game.getCurrentPlayers() + "/" + game.maxPlayers)
			)
		);
	}
}
