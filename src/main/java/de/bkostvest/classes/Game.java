package de.bkostvest.classes;

import static j2html.TagCreator.div;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.p;

import io.javalin.http.sse.SseClient;
import j2html.tags.ContainerTag;
import j2html.tags.specialized.*;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.StructuredTaskScope.Subtask.State;

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
	private ConcurrentLinkedQueue<SseClient> clients = new ConcurrentLinkedQueue<SseClient>();
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
		return random.toString();
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

	public void startGame() {
		state = GameState.STARTED;

		new Timer()
			.scheduleAtFixedRate(
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
								UpdateClientsView(generateEndScreen());
							}
						}
					}
				},
				1000,
				1000
			);
	}

	public DivTag generateEndScreen() {
		// Calculate average
		if (playerVotes.isEmpty()) {
			return div(h2("Game Finished!"), p("No votes were cast."));
		}
		return div(h2("Game Finished!"), p("No votes were cast."));

		// double average = playerVotes
		// 	.values()
		// 	.stream()
		// 	.mapToInt(Integer::intValue)
		// 	.average()
		// 	.orElse(0.0);

		// // Build vote distribution
		// java.util.Map<Integer, Long> voteCounts = playerVotes
		// 	.values()
		// 	.stream()
		// 	.collect(
		// 		java.util.stream.Collectors.groupingBy(
		// 			v -> v,
		// 			java.util.stream.Collectors.counting()
		// 		)
		// 	);

		// // Build the end screen
		// return div(
		// 	h2("Game Finished!"),
		// 	h3(
		// 		"Average Vote: " + String.format("%.2f", average)
		// 	),
		// 	h3("Vote Distribution:"),
		// 	ul(
		// 		voteCounts
		// 			.entrySet()
		// 			.stream()
		// 			.sorted(java.util.Map.Entry.comparingByKey())
		// 			.map(entry ->
		// 				j2html.TagCreator.li(
		// 					entry.getKey() +
		// 					": " +
		// 					entry.getValue() +
		// 					" vote(s)"
		// 				)
		// 			)
		// 			.toArray(j2html.tags.ContainerTag[]::new)
		// 	),
		// 	h3("Votes:"),
		// 	ul(
		// 		playerVotes
		// 			.entrySet()
		// 			.stream()
		// 			.map(entry ->
		// 				j2html.TagCreator.li(
		// 					"Player: " +
		// 					entry.getKey().getId() +
		// 					" → " +
		// 					entry.getValue()
		// 				)
		// 			)
		// 			.toArray(j2html.tags.ContainerTag[]::new)
		// 	),
		// );
	}
}
