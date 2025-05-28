package de.bkostvest.classes;

import java.util.concurrent.ConcurrentLinkedQueue;

public class GameList {
	public static ConcurrentLinkedQueue<Game> Gamelist = new ConcurrentLinkedQueue<Game>();

	public static Game getGameByJoinCode(String joinCode) {
		return Gamelist.stream().filter(game -> game.joinCode.equals(joinCode.toUpperCase())).findFirst().orElse(null);
	}

	public static void addGame(Game game) {
		Gamelist.add(game);
	}
}
