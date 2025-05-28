package de.bkostvest;

import de.bkostvest.controller.*;
import io.javalin.Javalin;

public class Router {
    public void setRoutes(Javalin app) {
        var rootController = new RootController();
        app.get("/", rootController::get);

        var resourceController = new ResourceController();
        resourceController.setRoutes(app);

        var homeController = new HomeController(rootController::replaceMain, "/home");
        homeController.setRoutes(app);

        var createGameController = new CreateGameController(rootController::replaceMain, "/create");
        createGameController.setAllRoutes(app);

        var joinGameController = new JoinGameController(rootController::replaceMain, "/join");
        joinGameController.setAllRoutes(app);

        var gameController = new GameController(rootController::replaceMain, "/game");
        gameController.setAllRoutes(app);
    }
}
