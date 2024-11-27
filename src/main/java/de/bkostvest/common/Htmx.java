package de.bkostvest.common;


public class Htmx {
	public static String GetAndReplace(String path, String target) {
		return "hx-get='" + path + "' hx-target='" + target + "' hx-push-url=true";
	}
}
