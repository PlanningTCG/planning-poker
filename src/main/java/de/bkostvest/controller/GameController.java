package de.bkostvest.controller;

import de.bkostvest.classes.Game;
import de.bkostvest.classes.GameList;
import de.bkostvest.common.StaticPartialHtmlController;
import io.javalin.Javalin;
import j2html.tags.specialized.*;
import jakarta.servlet.http.HttpSession;

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
				foundGame.addPlayer(ctx.req().getSession());
				System.out.println(ctx.req().getSession());

				this.get(ctx, foundGame.GameView(ctx.req().getSession()).render());
			} else {
				ctx.status(404);
			}
		});


        app.post("/game/{joinCode}/start", (ctx) -> {
			String joinCode = ctx.pathParam("joinCode");

			Game foundGame = GameList.getGameByJoinCode(joinCode);

			if (foundGame != null) {
				if (foundGame.creator.equals(ctx.req().getSession())) {
					foundGame.startGame();
					foundGame.clients.forEach(client -> {
						client.sendEvent("bump", foundGame.StartedGameView(client.ctx().req().getSession()).render());
					});
				}

				ctx.status(200);
			} else {
				ctx.status(404);
			}
		});


        app.sse("/game/{joinCode}/sse/connect", (client) -> {
        	String joinCode = client.ctx().pathParam("joinCode");


         	Game foundGame = GameList.getGameByJoinCode(joinCode);

			if (foundGame != null) {
				HttpSession currSessinon = client.ctx().req().getSession();

				foundGame.addClient(client);
				client.onClose(() -> {
					foundGame.removeClient(client);
					System.out.println("Disconnected " + client.toString());
				});

				client.keepAlive();
			} else {

			}
		});

        app.post("/game/{joinCode}/click/{number}", (ctx) -> {
			String joinCode = ctx.pathParam("joinCode");
			Integer number = Integer.parseInt(ctx.pathParam("number"));

			Game foundGame = GameList.getGameByJoinCode(joinCode);

			if (foundGame != null) {
				HttpSession currSessinon = ctx.req().getSession();

				foundGame.playerVotes.remove(currSessinon);
				foundGame.playerVotes.put(currSessinon, number);
				foundGame.UpdateOneClientView(currSessinon, foundGame.StartedGameView(currSessinon));
				ctx.status(200);
			} else {
				ctx.status(404);
			}
		});
    }

    @Override
    public DivTag view() {
        return div();
    }

}
