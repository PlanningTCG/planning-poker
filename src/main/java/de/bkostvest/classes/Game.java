package de.bkostvest.classes;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import jakarta.servlet.http.HttpSession;

public class Game  {
	public String theme;
	public int timeLimit;
	public int maxPlayers;
	public ConcurrentLinkedQueue<HttpSession> playerSessions = new ConcurrentLinkedQueue<HttpSession>();

	public String joinCode;

	public Game(String theme, int timeLimit, int maxPlayers) {
		this.theme = theme;
		this.timeLimit = timeLimit;
		this.maxPlayers = maxPlayers;
		this.joinCode = generateJoinCode();
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
}
