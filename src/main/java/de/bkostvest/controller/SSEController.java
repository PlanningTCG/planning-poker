package de.bkostvest.controller;

import static j2html.TagCreator.*;

import io.javalin.Javalin;
import io.javalin.http.*;
import io.javalin.http.sse.SseClient;
import j2html.tags.specialized.*;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import de.bkostvest.common.StaticPartialHtmlController;

public class SSEController extends StaticPartialHtmlController {
	private ConcurrentLinkedQueue<SseClient> clients = new ConcurrentLinkedQueue<SseClient>();
	private ConcurrentLinkedQueue<SSEMessage> messages = new ConcurrentLinkedQueue<SSEMessage>();

	private class SSEMessage {
		public String message;
		public String client;

		public SSEMessage(String message, String client) {
			this.message = message;
			this.client = client;
		}
	}

	public SSEController(Function<DivTag, HtmlTag> replaceMain) {
		super(replaceMain);

		//send heartbeat to each client
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(
			new TimerTask() {
				@Override
				public void run() {
					clients.forEach(client -> {
						client.sendComment("heartbeat");
					});
				}
			}, 1000, 1000
		);
	}

	public void setAllRoutes(Javalin app) {

		app.post("/sse/send", ctx -> {
			ctx.contentType(ContentType.TEXT_HTML).result(MessageSendFormView().render());

			String message = ctx.formParam("message");

			if (message.isEmpty())
				return;

			SSEMessage messageObj = new SSEMessage(message, ctx.req().getSession().getId());
			messages.add(messageObj);


			String generatedView = messageView();
			clients.forEach(client -> {
				client.sendEvent("message", generatedView);
			});

		});

		app.sse("/sse/connect", client -> {

			client.onClose(() -> {
				clients.remove(client);
				System.out.println("Disconnected " + client.toString());
			});
			clients.add(client);
			System.out.println("Connected " + client.toString());

			client.keepAlive();

			client.sendEvent("join", joinView(client));
			client.sendEvent("message", messageView());
		});
	}

	@Override
	public DivTag view() {
		return div();
	}

	public String joinView(SseClient client) {
		int clientsLength = clients.size();
		return div(Integer.toString(clientsLength)).render();
	}

	public String messageView() {
		ArrayList<DivTag> allMessages = new ArrayList<DivTag>();
		messages.forEach(message -> {
			allMessages.add(div(
				b(message.client + ": "),
				div(message.message).withClass("message-content")
			).attr("class='message'"));
		});

		DivTag[] messagesAsArray = new DivTag[allMessages.size()];
		messagesAsArray = allMessages.toArray(messagesAsArray);

		return div(messagesAsArray).withClass("message-container").render();
	}

	public static DivTag MessageSendFormView() {
		return div(
			form(
				input().withClass("message-input").withName("message").attr("required"),
				button("send").attr("hx-post='/sse/send' hx-target='#send-message-form' hx-swap='outerHTML'").withClass("send-msg")
			).attr("class='send-message-form'")
		).withId("send-message-form");
	}

	public static DivTag ChatView() {
		return div(
			div().attr("sse-swap='join'").withClass("user-count"),
			div().attr("sse-swap='message'").withClass("message-list")
		).attr("hx-ext='sse' sse-connect='/sse/connect'");
	}
}
