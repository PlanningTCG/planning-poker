package de.bkostvest.common;


public class Htmx {
	public static String GetAndReplace(String path, String target) {
		return "hx-get='" + path + "' hx-target='" + target + "' hx-push-url=true";
	}

	public static String PostAndReplace(String path, String target) {
		return "hx-post='" + path + "' hx-target='" + target + "' hx-push-url=true";
	}

	public static String PostAndRemove(String path, String target) {
		return "hx-post='" + path + "' hx-target='" + target + "'";
	}

	public static String PostOnly(String path) {
		return "hx-post='" + path + "'hx-swap='none'";
	}
}
