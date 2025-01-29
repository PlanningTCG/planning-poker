package de.bkostvest.classes;

import java.util.UUID;

public class Game  {
	public String theme;
	public int timeLimit;
	public int maxPlayers;
	public int currentPlayers = 0;
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
		return currentPlayers >= maxPlayers;
	}

	public int getRemainingPlayers() {
		return maxPlayers - currentPlayers;
	}

	public void addPlayer() {
		currentPlayers++;
	}
}
