package de.bkostvest.controller;

import de.bkostvest.Main;
import io.javalin.Javalin;
import io.javalin.http.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

public class ResourceController {
    private String htmx = "";
    private String styles = "";
    private String htmxSSE = "";

    public ResourceController() {
        try {
            this.htmx = getResourceFromFilePath("/htmx.js");
            this.htmxSSE = getResourceFromFilePath("/htmx-ext-sse.js");
            this.styles = getResourceFromFilePath("/styles.css");

        } catch (Exception e) {
            Main.logger.error("Failed loading files in ResourceController");
        }
    }

	public void setRoutes(Javalin app) {
		app.get("/htmx.js", this::getHtmx);
		app.get("/htmx-ext-sse.js", this::getHtmxSSE);
		app.get("/styles.css", this::getStyles);
	}

	private String getResourceFromFilePath(String filePath) throws IOException {
		System.out.println("Loading resource from: " + filePath);
		return Files.readString(Path.of(Objects.requireNonNull(this.getClass().getResource(filePath)).getFile()));
	}

    public void getHtmx(Context ctx) {
        ctx.contentType(ContentType.JAVASCRIPT);
        ctx.result(htmx);
    }

    public void getHtmxSSE(Context ctx) {
        ctx.contentType(ContentType.JAVASCRIPT);
        ctx.result(htmxSSE);
    }

    public void getStyles(Context ctx) {
        ctx.contentType(ContentType.CSS);
        ctx.result(styles);
    }
}
