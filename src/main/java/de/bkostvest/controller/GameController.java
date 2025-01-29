package de.bkostvest.controller;

import de.bkostvest.classes.Game;
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

			Optional<Game> foundGame = CreateGameController.Gamelist.stream().filter(game -> game.joinCode.equals(joinCode)).findFirst();

			if (foundGame.isPresent()) {
				Game game = foundGame.get();
				game.addPlayer();
				this.get(ctx, GameView(game).render());
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
				h2("Players: " + game.currentPlayers + "/" + game.maxPlayers)
			)
		);
	}
}
