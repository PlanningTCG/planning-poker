package de.bkostvest.controller;

import de.bkostvest.classes.Game;
import de.bkostvest.classes.GameList;
import de.bkostvest.classes.Game.GameState;
import de.bkostvest.common.Htmx;
import de.bkostvest.common.StaticPartialHtmlController;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import j2html.tags.ContainerTag;
import j2html.tags.specialized.*;
import jakarta.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
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
				System.out.println(ctx.req().getSession());

				this.get(ctx, GameView(foundGame, ctx.req().getSession()).render());
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
					foundGame.UpdateClientsView(StartedGameView(foundGame));
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
				foundGame.addClient(client);
				client.onClose(() -> {
					foundGame.removeClient(client);
					System.out.println("Disconnected " + client.toString());
				});

				if (foundGame.state == Game.GameState.STARTED) {
					client.sendEvent("bump", StartedGameView(foundGame));
				}

				client.keepAlive();
			} else {

			}
		});

        app.post("/game/{joinCode}/click/{number}", (ctx) -> {
			String joinCode = ctx.pathParam("joinCode");
			Integer number = Integer.parseInt(ctx.pathParam("number"));

			Game foundGame = GameList.getGameByJoinCode(joinCode);

			if (foundGame != null) {

				//remove player vote
				foundGame.playerVotes.remove(ctx.req().getSession());
				foundGame.playerVotes.put(ctx.req().getSession(), number);
				foundGame.UpdateClientsView(StartedGameView(foundGame));


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

	public DivTag GameView(Game game, HttpSession currentUser) {
		DivTag admin = div();

		if (currentUser != null && game.creator.equals(currentUser)) {
			String url = "/game/" + game.joinCode + "/start";
			admin = div(
				button("Start Game").withClass("basic-button").attr(Htmx.PostAndRemove(url, "#startBtn"))
			).withId("startBtn");
		}

		String sseUrl = "/game/" + game.joinCode + "/sse/connect";
		return div(
			div(
				h1(game.theme),
				h2("Time Limit: " + game.timeLimit),
				h2("Players: " + game.getCurrentPlayers() + "/" + game.maxPlayers),
				admin
			),
			//hx-ext="sse" sse-connect="/chatroom" sse-swap="message",)
			div().attr("hx-ext", "sse").attr("sse-connect", sseUrl).attr("sse-swap", "bump")
		);
	}

	public DivTag StartedGameView(Game game) {
		Integer[] fibonacci = new Integer[10];
		fibonacci[0] = 0;
		fibonacci[1] = 1;
		fibonacci[2] = 2;
		for (int i = 3; i < 10; i++) {
		    fibonacci[i] = fibonacci[i - 1] + fibonacci[i - 2];
		}
		List<Integer> fibonacciList = Arrays.asList(fibonacci);


		return div(
			div(
				div(
					h2(game.theme),
					div(
						h2("Time Limit: "),
						h2(String.valueOf(game.timeLimit)).attr("sse-swap", "time")
					),
					h2("Players: " + game.getCurrentPlayers() + "/" + game.maxPlayers)
				).withClass("d-flex space-around"),
				div(each(fibonacciList, number -> GenerateCard(game, number))).withClass("d-flex space-around")
			)
		);
	}

	public DivTag GenerateCard(Game game, Integer number) {
		String postUrl = "/game/" + game.joinCode + "/click/" + number;
		long clicked = game.playerVotes.values().stream()
               .filter(e -> e == number)
               .count();


		return div(
			number.toString() + " (" + clicked + ")"
		).withClass("card")
		.attr("value", number)
		.attr(Htmx.PostOnly(postUrl));
	}
}
