package de.bkostvest.classes;

import static j2html.TagCreator.button;
import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.span;

import io.javalin.http.sse.SseClient;
import j2html.tags.specialized.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import java.util.Arrays;

import de.bkostvest.common.Htmx;



public class Game {

	public static enum GameState {
		LOBBY,
		STARTED,
		FINISHED,
	}

	public String theme;
	public int timeLimit;
	public int maxPlayers;
	public ConcurrentLinkedQueue<HttpSession> playerSessions = new ConcurrentLinkedQueue<HttpSession>();
	public ConcurrentLinkedQueue<SseClient> clients = new ConcurrentLinkedQueue<SseClient>();
	private Timer timer;
	public HttpSession creator;
	public ConcurrentHashMap<HttpSession, Integer> playerVotes = new ConcurrentHashMap<HttpSession, Integer>();
	public GameState state = GameState.LOBBY;

	public String joinCode;

	public Game( String theme, int timeLimit, int maxPlayers, HttpSession creator) {
		this.theme = theme;
		this.timeLimit = timeLimit;
		this.maxPlayers = maxPlayers;
		this.creator = creator;
		this.joinCode = generateJoinCode();

		timer = new Timer();
		timer.scheduleAtFixedRate(
			new TimerTask() {
				@Override
				public void run() {
					clients.forEach(client -> {
						client.sendComment("heartbeat");
					});
				}
			},
			1000,
			1000
		);
	}

	public String generateJoinCode() {
		//temp use of UUID
		UUID random = UUID.randomUUID();
		return random.toString().substring(0, 5).toUpperCase();
	}

	public boolean isFull() {
		return getCurrentPlayers() >= maxPlayers;
	}

	public int getRemainingPlayers() {
		return maxPlayers - getCurrentPlayers();
	}

	public int getCurrentPlayers() {
		return playerSessions.size();
	}

	public void addPlayer(HttpSession httpSession) {
		//check if session is already in game
		if (playerSessions.contains(httpSession)) {
			return;
		}

		playerSessions.add(httpSession);
	}

	public void addClient(SseClient client) {
		clients.add(client);
	}

	public void removeClient(SseClient client) {
		clients.remove(client);
	}

	public void UpdateClientsView(DivTag divTag) {
		clients.forEach(client -> {
			client.sendEvent("bump", divTag.render());
		});
	}

	public void UpdateOneClientView(HttpSession session, DivTag divTag) {
		clients.forEach(client -> {
			if (client.ctx().req().getSession() == session) {
				client.sendEvent("bump", divTag.render());
			}
		});
	}

	public void startGame() {
		state = GameState.STARTED;

		Timer currTimer = new Timer();

		currTimer.scheduleAtFixedRate(
				new TimerTask() {
					@Override
					public void run() {
						if (state == GameState.STARTED) {
							if (timeLimit >= 0) {
								clients.forEach(client -> {
									client.sendEvent(
										"time",
										String.valueOf(timeLimit)
									);
								});
								timeLimit--;
							} else {
								state = GameState.FINISHED;
								clients.forEach(client -> {
									client.sendEvent(
										"bump",
										StartedGameView(client.ctx().req().getSession()).render()
									);
								});
								currTimer.cancel();
							}
						}
					}
				},
				1000,
				1000
			);
	}

	public DivTag GameView(HttpSession currentUser) {
		DivTag admin = div();

		if (currentUser != null && this.creator.equals(currentUser)) {
			String url = "/game/" + this.joinCode + "/start";
			admin = div(
				button("Start Game").withClass("basic-button").attr(Htmx.PostAndRemove(url, "#startBtn"))
			).withId("startBtn");
		}

		String sseUrl = "/game/" + this.joinCode + "/sse/connect";
		return div(
			//hx-ext="sse" sse-connect="/chatroom" sse-swap="message",)
			div(
				GenerateGameHeader(currentUser)
			).attr("sse-swap", "bump"),
			div(
				admin
			)
		).attr("hx-ext", "sse").attr("sse-connect", sseUrl);
	}

	public List<Integer> GenerateFibbonacciAsList() {
		Integer[] fibonacci = new Integer[10];
		fibonacci[0] = 0;
		fibonacci[1] = 1;
		fibonacci[2] = 2;
		for (int i = 3; i < 10; i++) {
		    fibonacci[i] = fibonacci[i - 1] + fibonacci[i - 2];
		}
		List<Integer> fibonacciList = Arrays.asList(fibonacci);

		return fibonacciList;
	}

	public DivTag StartedGameView(HttpSession currentUser) {
		DivTag returnDiv = div(GenerateGameHeader(currentUser));
		if (state != GameState.LOBBY) {
			returnDiv = div(
				div(
					GenerateGameHeader(currentUser),
					div(
						each(GenerateFibbonacciAsList(), number -> GenerateCard(currentUser, number))
					).withClass("d-flex space-around")
				)
			);
		} else {
			returnDiv = div(
				div(
					GenerateGameHeader(currentUser)
				)
			);
		}

		return returnDiv;
	}

	public DivTag GenerateGameHeader(HttpSession currentUser) {
		DivTag timeDiv = div();

		if (state == GameState.STARTED) {
			timeDiv = div(
				h2(
					span("Time Limit: "),
					span(String.valueOf(this.timeLimit)).attr("sse-swap", "time")
				)
			);

		} else if (state == GameState.FINISHED) {
			timeDiv = div(
				h2("Game Finished!")
			);
		}

		return div(
			h2(this.theme),
			timeDiv,
			h2(
				span("Players: " ),
			 	span(String.valueOf(getCurrentPlayers())).attr("sse-swap", "players"),
				span("/" + this.maxPlayers)
			)
		).withClass("d-flex space-around");
	}

	public DivTag GenerateCard(HttpSession currentUser, Integer number) {

		DivTag retrunDiv = div();
		if (state == GameState.STARTED) {
			String postUrl = "/game/" + this.joinCode + "/click/" + number;
			Integer playerClickedValue = this.playerVotes.get(currentUser);
			Boolean clicked = playerClickedValue != null && playerClickedValue == number ? true : false;
			String clickedClass = clicked ? "clicked-card" : "";


			retrunDiv = div(
				number.toString()
			).withClass("card cursor-pointer " + clickedClass)
			.attr(Htmx.PostOnly(postUrl));


		} else if (state == GameState.FINISHED) {
			long clicked = this.playerVotes.values().stream()
               .filter(e -> e == number)
               .count();

			retrunDiv = div(
				number.toString() + " (" + clicked + ")"
			).withClass("card");
		}

		return retrunDiv;
	}

}
