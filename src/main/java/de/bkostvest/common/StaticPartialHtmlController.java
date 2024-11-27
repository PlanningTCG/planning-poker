package de.bkostvest.common;

import io.javalin.Javalin;
import io.javalin.http.*;
import j2html.tags.specialized.DivTag;
import j2html.tags.specialized.HtmlTag;

import java.util.Objects;
import java.util.function.Function;


public abstract class StaticPartialHtmlController {
    private final String fullView;
    private final String view;
    private final String route;

    public StaticPartialHtmlController(Function<DivTag, HtmlTag> replaceMain, String route) {
        this.fullView = replaceMain.apply(view()).render();
        this.view = view().render();
        this.route = route;
    }

    public StaticPartialHtmlController(Function<DivTag, HtmlTag> replaceMain) {
        this.fullView = replaceMain.apply(view()).render();
        this.view = view().render();
        this.route = "";
    }

    public void getFull(Context ctx) {
        getFull(ctx, fullView);
    }

    public void getFull(Context ctx, String result) {
        ctx.contentType(ContentType.TEXT_HTML).result(result);
    }

    public void setRoutes(Javalin app) {
        setRoutes(app, this.route);
    }

    public void setRoutes(Javalin app, String route) {
        app.get(route, (ctx) -> {
            if (Objects.equals(ctx.header("hx-request"), "true")) {
                get(ctx);
            } else  {
                getFull(ctx);
            }
        });
    }

    public void addView(Javalin app, String route, String view) {
        app.get(route, (ctx) -> {
            if (Objects.equals(ctx.header("hx-request"), "true")) {
                get(ctx, view);
            } else  {
                getFull(ctx, view);
            }
        });
    }

    public void get(Context ctx) {
    	get(ctx, view);
    }

    public void get(Context ctx, String result) {
        ctx.contentType(ContentType.TEXT_HTML).result(result);
    }

    public abstract DivTag view();
}
