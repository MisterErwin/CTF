package net.askarian.MisterErwin.CTF.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {

	public String giveChatMessage(String message, Map<String, String> con) {
		Pattern p = Pattern.compile("\"([^\"]*)\"");
		Matcher m = p.matcher(message);
		while (m.find()) {
			if (con.containsKey(m.group(1))) {
				message.replaceAll("[" + m.group(1) + "]", con.get(m.group(1))
						.toString());
			}
		}
		return message;
	}
}
