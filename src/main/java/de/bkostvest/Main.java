package de.bkostvest;

import io.javalin.Javalin;
import org.slf4j.*;

public class Main {
    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        var router = new Router();
        var app = Javalin.create();

        router.setRoutes(app);
        app.start(5000);
    }
}