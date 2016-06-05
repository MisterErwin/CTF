package net.askarian.MisterErwin.CTF;

import net.askarian.MisterErwin.CTF.API.CtFClass;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Arrays;
import java.util.HashMap;

public class ChatManager {
	private CTF plugin;
	private String[] messagesTA = new String[5];
	private String[] messagesTB = new String[5];

	public enum ChatMode {
		HUD, CHAT, SCOREBOARD, CHATBOARD
	}

	public boolean showstats = false;

	private String[] premessages = new String[5];

	private HashMap<String, String> lastMessages = new HashMap<String, String>();
	private HashMap<String, String> modes = new HashMap<String, String>();

	public String broadcasterMessage = "Welcome ... but I'm empty!";

	public ChatMode cm = ChatMode.HUD;

	public ScoreboardManager smanager;
	public Scoreboard sboard;
	public Objective sobjective;

	public Score ACaptures, BCaptures, timeleft, winningTeam, time2start;
	private int board;
	private int boardtime = 15;

	public ChatManager(CTF pl) {
		this.plugin = pl;

		this.smanager = Bukkit.getScoreboardManager();
		sboard = smanager.getNewScoreboard();

		plugin.tm.AScoreTeam = sboard.registerNewTeam("RED");
		plugin.tm.BScoreTeam = sboard.registerNewTeam("BLUE");

		plugin.tm.AScoreTeam.setCanSeeFriendlyInvisibles(true);
		plugin.tm.AScoreTeam.setCanSeeFriendlyInvisibles(true);

		// Start with the scoreboard!
		this.sobjective = sboard.registerNewObjective(ChatColor.GOLD
				+ "~~~CTF~~~", "dummy");

		// Setting where to display the scoreboard/objective (either SIDEBAR,
		// PLAYER_LIST or BELOW_NAME)
		sobjective.setDisplaySlot(DisplaySlot.SIDEBAR);

		// Setting the display name of the scoreboard/objective
		sobjective.setDisplayName(ChatColor.GOLD + "~~~CTF~~~");

		if (this.cm == ChatMode.CHATBOARD)
			plugin.log.info("Set update time to: " + this.boardtime
					+ " seconds!");
		// this.boardtime = Integer.parseInt(plugin.conm.get("broadcastertime"))
		// ;
	}

	public void SendMessage(Player p, String message) {
		if (p == null || !p.isOnline())
			return;
		if (this.cm == ChatMode.HUD) {
			this.lastMessages.put(p.getName(), message);
			update(p);
		} else if (this.cm == ChatMode.CHAT || this.cm == ChatMode.SCOREBOARD
				|| this.cm == ChatMode.CHATBOARD) {
			p.sendMessage(message);
		}
	}

	public String GetMessage(Player p) {
		if (p == null || !p.isOnline())
			return "";
		return (this.lastMessages.get(p.getName()) != null) ? this.lastMessages
				.get(p.getName()) : "";
	}

	public boolean PlayerChat(Player p, String message) {
		if (p == null || !p.isOnline())
			return false;
		if (this.cm == ChatMode.HUD) {
			if (plugin.Game.running) {
				String m = message;
				if (m.startsWith("!"))
					m = m.replaceFirst("!", "");
				if (this.plugin.tm.getTeam(p).equals("A")) {
					this.messagesTA[0] = this.messagesTA[1];
					this.messagesTA[1] = this.messagesTA[2];
					this.messagesTA[2] = this.messagesTA[3];
					this.messagesTA[3] = this.messagesTA[4];
					this.messagesTA[4] = "<" + p.getName() + "> " + m;
					if (message.startsWith("!")) {
						// write in B
						this.messagesTB[0] = this.messagesTB[1];
						this.messagesTB[1] = this.messagesTB[2];
						this.messagesTB[2] = this.messagesTB[3];
						this.messagesTB[3] = this.messagesTB[4];
						this.messagesTB[4] = "<" + ChatColor.RED + p.getName()
								+ ChatColor.WHITE + "> " + m;
					}
				}
				if (this.plugin.tm.getTeam(p).equals("B")) {
					this.messagesTB[0] = this.messagesTB[1];
					this.messagesTB[1] = this.messagesTB[2];
					this.messagesTB[2] = this.messagesTB[3];
					this.messagesTB[3] = this.messagesTB[4];
					this.messagesTB[4] = "<" + p.getName() + "> " + m;
					if (message.startsWith("!")) {
						this.messagesTA[0] = this.messagesTA[1];
						this.messagesTA[1] = this.messagesTA[2];
						this.messagesTA[2] = this.messagesTA[3];
						this.messagesTA[3] = this.messagesTA[4];
						this.messagesTA[4] = "<" + ChatColor.BLUE + p.getName()
								+ ChatColor.WHITE + "> " + m;
					}
				}
			} else if (plugin.Game.idle || plugin.Game.after) {
				// this.messagesTB[0] = this.messagesTB[1];
				// this.messagesTB[1] = this.messagesTB[2];
				// this.messagesTB[2] = "<" + p.getName() + "> " + message;
				this.premessages[0] = this.premessages[1];
				this.premessages[1] = this.premessages[2];
				this.premessages[2] = this.premessages[3];
				this.premessages[3] = this.premessages[4];
				this.premessages[4] = "<" + p.getName() + "> " + message;
				plugin.log.info("Added to premessages " + message);
			}
			allupdate();
			return true;
		} else if (this.cm == ChatMode.CHAT || this.cm == ChatMode.SCOREBOARD
				|| this.cm == ChatMode.CHATBOARD) {
			return false;

		}

		return false;
	}

	public void allupdate() {
		if (this.plugin.Game.running) {
			Teamupdate("A");
			Teamupdate("B");
		} else if (this.plugin.Game.idle || plugin.Game.after) {
			for (Player p : this.plugin.getServer().getOnlinePlayers()) {
				if ((this.plugin.tm.getTeam(p).equals("A") || this.plugin.tm
						.getTeam(p).equals("B")) && plugin.Game.after)
					postupdate(p);
				else if (this.plugin.Game.waitingPlayers.containsKey(p
						.getName()))
					preupdate(p);

			}
		}

	}

	public void Teamupdate(String team) {
		if (!this.plugin.Game.running)
			return;
		if ((!team.equals("A")) && (!team.equals("B")))
			return;
		if (team.equals("A"))
			for (String pn : plugin.tm.TeamA.keySet())
				if (plugin.Game.after)
					postupdate(plugin.getServer().getPlayerExact(pn));
				else
					update(plugin.getServer().getPlayerExact(pn));
		else if (team.equals("B"))
			for (String pn : plugin.tm.TeamB.keySet())
				if (plugin.Game.after)
					postupdate(plugin.getServer().getPlayerExact(pn));
				else
					update(plugin.getServer().getPlayerExact(pn));
		// for (Player p : this.plugin.getServer().getOnlinePlayers())
		// if ((this.plugin.tm.getTeam(p) == "A" ||this.plugin.tm.getTeam(p) ==
		// "B") && plugin.Game.after)
		// postupdate(p);
		// else if (this.plugin.tm.getTeam(p.getName()) == team)
		// update(p);
	}

	public void preupdate(Player p) {
		if (this.plugin.Game.running) {
			update(p);
			return;
		}

		if (this.plugin.Game.after) {
			postupdate(p);
			return;
		}
		if (p == null || !p.isOnline())
			return;
		if (this.cm == ChatMode.HUD) {

			p.sendMessage(this.clearChat());

			getClasses(p);

			String sc = (plugin.Game.waitingPlayers.get(p.getName()) != null) ? plugin.Game.waitingPlayers
					.get(p.getName()) : "Not yet choosen!";

			p.sendMessage(ChatColor.GOLD + "Starting Class: "
					+ ChatColor.LIGHT_PURPLE + sc);

			p.sendMessage(ChatColor.WHITE + ChatColor.STRIKETHROUGH.toString()
					+ StringUtils.repeat(" ", 80));

			p.sendMessage(GetMessage(p));

			p.sendMessage(ChatColor.WHITE + getPreChat(5));

			p.sendMessage(ChatColor.YELLOW
					+ "Game begins: "
					+ ((plugin.Game.premessage.equals("")) ? plugin.Game.time2start
							: plugin.Game.premessage));

		} else if (this.cm == ChatMode.SCOREBOARD
				|| this.cm == ChatMode.CHATBOARD) {
			if (this.time2start == null) {
				this.time2start = sobjective.getScore(Bukkit
						.getOfflinePlayer(ChatColor.GREEN + "Game starts:")); // Get
																				// a
																				// fake
																				// offline
																				// player
			}
			this.time2start.setScore(plugin.Game.time2start);

			if (this.timeleft != null) {
				sboard.resetScores(timeleft.getPlayer());
				this.timeleft = null;
			}

			if (this.ACaptures != null) {
				sboard.resetScores(ACaptures.getPlayer());
				this.ACaptures = null;
			}

			if (this.BCaptures != null) {
				sboard.resetScores(BCaptures.getPlayer());
				this.BCaptures = null;
			}

			if (this.winningTeam != null) {
				sboard.resetScores(winningTeam.getPlayer());
				this.winningTeam = null;
			}

			p.setScoreboard(plugin.cm.sboard);

		}

	}

	public void postupdate(Player p) {
		if (this.plugin.Game.running) {
			update(p);
			return;
		}
		if (this.plugin.Game.idle) {
			preupdate(p);
			return;
		}

		if (p == null || !p.isOnline())
			return;

		// plugin.log.info("POST!");

		if (this.cm == ChatMode.HUD) {

			p.sendMessage(this.clearChat());

			p.sendMessage(ChatColor.WHITE + ChatColor.STRIKETHROUGH.toString()
					+ StringUtils.repeat(" ", 80));

			p.sendMessage(ChatColor.WHITE + getPreChat(3));

			p.sendMessage(ChatColor.YELLOW + "Next Game begins in: "
					+ plugin.Game.time2leave);

			p.sendMessage(ChatColor.GREEN + "Vote for the next Map with /vote");

		} else if (this.cm == ChatMode.SCOREBOARD
				|| this.cm == ChatMode.CHATBOARD) {
			// plugin.log.info("Update!");

			if (this.ACaptures == null) {
				this.ACaptures = sobjective.getScore(Bukkit
						.getOfflinePlayer(ChatColor.RED + "Team RED:")); // Get
																			// a
																			// fake
																			// offline
																			// player
			}
			this.ACaptures.setScore(plugin.Game.TeamACaptures);

			if (this.BCaptures == null) {
				this.BCaptures = sobjective.getScore(Bukkit
						.getOfflinePlayer(ChatColor.BLUE + "Team BLUE:")); // Get
																			// a
																			// fake
																			// offline
																			// player
			}
			this.BCaptures.setScore(plugin.Game.TeamBCaptures);

			if (this.timeleft == null) {
				this.timeleft = sobjective.getScore(Bukkit
						.getOfflinePlayer(ChatColor.GREEN + "Time left:")); // Get
																			// a
																			// fake
																			// offline
																			// player
			}
			this.timeleft.setScore(plugin.Game.time2leave);

			if (this.winningTeam != null) {
				sboard.resetScores(winningTeam.getPlayer());
				this.winningTeam = null;
			}

			if (this.time2start != null) {
				sboard.resetScores(time2start.getPlayer());
				this.time2start = null;
			}

			p.setScoreboard(plugin.cm.sboard);
		}

	}

	public void update(Player p) {
		if (p == null || this.plugin == null)
			return;

		if (this.plugin.Game.idle) {
			preupdate(p);
			return;
		}

		if (this.plugin.Game.after) {
			postupdate(p);
			return;
		}

		if (this.cm == ChatMode.HUD) {

			if (p == null || !p.isOnline()) {
				plugin.tm.LeaveTeam(p, true);
				return;
			}

			p.sendMessage(this.clearChat());

			if (this.modes.containsKey(p.getName())) {
				if (this.modes.get(p.getName()).equals("classes")) {
					getClasses(p);
					p.sendMessage(plugin.trans.get("backwith/class"));
					return;
				}
			}

			p.sendMessage(this.broadcasterMessage);

			p.sendMessage(this.plugin.conm.get("TeamNameA") + ": "
					+ ChatColor.WHITE + plugin.Game.TeamACaptures + "/"
					+ plugin.Game.MaxCaptures + " captures | Flag -> "
					+ getFlag("A") + " | " + getPlayers("A") + " players");

			p.sendMessage(this.plugin.conm.get("TeamNameB") + ": "
					+ ChatColor.WHITE + plugin.Game.TeamBCaptures + "/"
					+ plugin.Game.MaxCaptures + " captures | Flag -> "
					+ getFlag("B") + " | " + getPlayers("B") + " players");

			p.sendMessage(ChatColor.GREEN + "Kills: " + ChatColor.WHITE
					+ plugin.tracker.getKills(p.getName()) + " ("
					+ plugin.tracker.getKillsinrow(p.getName()) + " in a row)"
					+ ChatColor.YELLOW + " | " + ChatColor.GREEN + "Deaths: "
					+ ChatColor.WHITE + plugin.tracker.getDeaths(p.getName()));

			if (plugin.tm.getTeam(p).equals("A"))
				p.sendMessage(ChatColor.RED
						+ ChatColor.STRIKETHROUGH.toString()
						+ StringUtils.repeat(" ", 80));
			else if (plugin.tm.getTeam(p).equals("B"))
				p.sendMessage(ChatColor.BLUE
						+ ChatColor.STRIKETHROUGH.toString()
						+ StringUtils.repeat(" ", 80));
			else
				p.sendMessage(ChatColor.LIGHT_PURPLE
						+ ChatColor.STRIKETHROUGH.toString()
						+ StringUtils.repeat(" ", 80));

			p.sendMessage(ChatColor.WHITE + getChat(p));

			p.sendMessage((ChatColor.YELLOW
					+ this.lastMessages.get(p.getName()) != null)
					&& (this.lastMessages.containsKey(p.getName())) ? this.lastMessages
					.get(p.getName()) + " "
					: "");
		} else if (this.cm == ChatMode.CHAT
				|| (this.cm == ChatMode.CHATBOARD && this.board > this.boardtime)) {
			this.board = 0;
			if (this.showstats && p != null) {
				p.sendMessage(this.broadcasterMessage);
				p.sendMessage(this.plugin.conm.get("TeamNameA") + ": "
						+ ChatColor.WHITE + plugin.Game.TeamACaptures + "/"
						+ plugin.Game.MaxCaptures + " captures | Flag -> "
						+ getFlag("A") + " | " + getPlayers("A") + " players");

				p.sendMessage(this.plugin.conm.get("TeamNameB") + ": "
						+ ChatColor.WHITE + plugin.Game.TeamBCaptures + "/"
						+ plugin.Game.MaxCaptures + " captures | Flag -> "
						+ getFlag("B") + " | " + getPlayers("B") + " players");
				p.sendMessage(ChatColor.GREEN + "Kills: " + ChatColor.WHITE
						+ plugin.tracker.getKills(p.getName()) + " ("
						+ plugin.tracker.getKillsinrow(p.getName())
						+ " in a row)" + ChatColor.YELLOW + " | "
						+ ChatColor.GREEN + "Deaths: " + ChatColor.WHITE
						+ plugin.tracker.getDeaths(p.getName()));

				if (plugin.tm.getTeam(p).equals("A")) {
					p.sendMessage(ChatColor.RED + "---You are in Team RED---");
				} else if (plugin.tm.getTeam(p).equals("B")) {
					p.sendMessage(ChatColor.BLUE + "---You are in Team BLUE---");

				}
			}
		}
		if (this.cm == ChatMode.SCOREBOARD || this.cm == ChatMode.CHATBOARD) {
			this.board++;
			if (this.ACaptures == null) {
				this.ACaptures = sobjective.getScore(Bukkit
						.getOfflinePlayer(ChatColor.RED + "Team RED:")); // Get
																			// a
																			// fake
																			// offline
																			// player
			}
			this.ACaptures.setScore(plugin.Game.TeamACaptures);

			if (this.BCaptures == null) {
				this.BCaptures = sobjective.getScore(Bukkit
						.getOfflinePlayer(ChatColor.BLUE + "Team BLUE:")); // Get
																			// a
																			// fake
																			// offline
																			// player
			}
			this.BCaptures.setScore(plugin.Game.TeamBCaptures);

			if (this.timeleft == null) {
				this.timeleft = sobjective.getScore(Bukkit
						.getOfflinePlayer(ChatColor.GREEN + "Time left:")); // Get
																			// a
																			// fake
																			// offline
																			// player
			}
			this.timeleft.setScore(plugin.Game.time2play);

			if (this.winningTeam != null) {
				sboard.resetScores(winningTeam.getPlayer());
				this.winningTeam = null;
			}

			if (this.time2start != null) {
				sboard.resetScores(time2start.getPlayer());
				this.time2start = null;
			}

			p.setScoreboard(plugin.cm.sboard);

		}
	}

	private String getFlag(String team) {
		if (team.equals("A")) {
			if (((this.plugin.Game.FlagAHolder instanceof String))
					&& (this.plugin.Game.FlagAHolder == "Home"))
				return "[Home]";
			if (((this.plugin.Game.FlagAHolder instanceof String))
					&& (this.plugin.Game.FlagAHolder == "reset"))
				return "['ll reset in " + plugin.Game.FlagAReset + "]";
			if ((this.plugin.Game.FlagAHolder instanceof Player))
				return ChatColor.WHITE + "[" + ChatColor.BLUE
						+ ((Player) this.plugin.Game.FlagAHolder).getName()
						+ ChatColor.WHITE + "]";
			if ((this.plugin.Game.FlagAHolder instanceof Location))
				return ChatColor.WHITE + ""
						+ ((Location) this.plugin.Game.FlagAHolder).getX()
						+ " ; "
						+ ((Location) this.plugin.Game.FlagAHolder).getY()
						+ " ; "
						+ ((Location) this.plugin.Game.FlagAHolder).getZ();
		} else if (team.equals("B")) {
			if (((this.plugin.Game.FlagBHolder instanceof String))
					&& (this.plugin.Game.FlagBHolder == "Home"))
				return "[Home]";
			if (((this.plugin.Game.FlagBHolder instanceof String))
					&& (this.plugin.Game.FlagBHolder == "reset"))
				return "['ll reset in " + plugin.Game.FlagBReset + "]";
			if ((this.plugin.Game.FlagBHolder instanceof Player))
				return ChatColor.WHITE + "[" + ChatColor.RED
						+ ((Player) this.plugin.Game.FlagBHolder).getName()
						+ ChatColor.WHITE + "]";
			if ((this.plugin.Game.FlagBHolder instanceof Location)) {
				return ChatColor.WHITE + ""
						+ ((Location) this.plugin.Game.FlagBHolder).getX()
						+ " ; "
						+ ((Location) this.plugin.Game.FlagBHolder).getY()
						+ " ; "
						+ ((Location) this.plugin.Game.FlagBHolder).getZ();
			}
		}
		return "???";
	}

	private int getPlayers(String team) {
		if (team.equals("A"))
			return this.plugin.tm.TeamA.size();
		if (team.equals("B"))
			return this.plugin.tm.TeamB.size();
		return 0;
	}

	private String getChat(Player p) {
		String ret = "";
		if (this.plugin.tm.getTeam(p).equals("A")) {
			// ret = ret
			// + (this.messagesTA[0] != null ? this.messagesTA[0] + "\n"
			// : "");
			ret = ret
					+ (this.messagesTA[1] != null ? this.messagesTA[1] + "\n"
							: "");
			ret = ret
					+ (this.messagesTA[2] != null ? this.messagesTA[2] + "\n"
							: "");
			ret = ret
					+ (this.messagesTA[3] != null ? this.messagesTA[3] + "\n"
							: "");
			ret = ret
					+ (this.messagesTA[4] != null ? this.messagesTA[4] + "\n"
							: "");
		} else if (this.plugin.tm.getTeam(p).equals("B")) {
			// ret = ret
			// + (this.messagesTB[0] != null ? this.messagesTB[0] + "\n"
			// : "");
			ret = ret
					+ (this.messagesTB[1] != null ? this.messagesTB[1] + "\n"
							: "");
			ret = ret
					+ (this.messagesTB[2] != null ? this.messagesTB[2] + "\n"
							: "");
			ret = ret
					+ (this.messagesTB[3] != null ? this.messagesTB[3] + "\n"
							: "");
			ret = ret
					+ (this.messagesTB[4] != null ? this.messagesTB[4] + "\n"
							: "");
		}
		return ret;
	}

	private String getPreChat(int lines) {
		String ret = "";
		if (lines < 1)
			return ret + "Why zero lines?";
		ret = ret
				+ ((this.premessages[0] != null) ? this.premessages[0] + "\n"
						: "");
		if (lines == 1)
			return ret;
		ret = ret
				+ ((this.premessages[1] != null) ? this.premessages[1] + "\n"
						: "");
		if (lines == 2)
			return ret;
		ret = ret
				+ ((this.premessages[2] != null) ? this.premessages[2] + "\n"
						: "");
		if (lines == 3)
			return ret;
		ret = ret
				+ ((this.premessages[3] != null) ? this.premessages[3] + "\n"
						: "");
		if (lines == 4)
			return ret;
		ret = ret
				+ ((this.premessages[4] != null) ? this.premessages[4] + "\n"
						: "");
		return ret;
	}

	public void reset() {
		this.lastMessages.clear();
		Arrays.fill(this.messagesTA, null);
		Arrays.fill(this.messagesTB, null);
		allupdate();
	}

	public void allMessage(String message) {
		for (Player p : this.plugin.getServer().getOnlinePlayers())
			SendMessage(p, message);
	}

	public void changemode(Player p, String mode) {
		if (this.cm != ChatMode.HUD)
			return;
		if (p == null || !p.isOnline())
			return;
		if (mode.equals("classes") && !this.modes.get(p.getName()).equals(mode)) {
			if (this.modes.containsKey(p.getName()))
				this.modes.remove(p.getName());
			this.modes.put(p.getName(), mode);
		} else {
			if (this.modes.containsKey(p.getName()))
				this.modes.remove(p.getName());
			this.modes.put(p.getName(), "");
		}

	}

	public void getClasses(Player p) {
		if (p == null || !p.isOnline())
			return;
		String cli = "";
		String av = "";
		String pr = "";
		if (this.plugin.Game.freeclasses != null)
			for (CtFClass ctfcl : this.plugin.clm.Classes) {
				cli = ctfcl.getCommand();
				String perm = "CTF." + cli;

				if (plugin.Game.disallowedclasses.contains(cli))
					continue;

				if (plugin.Game.freeclasses.contains(cli)) {
					av = av + "*" + cli + ", ";
					continue;
				}
				if (p.hasPermission(perm)) {
					av = av + "*" + cli + ", ";
					continue;
				}
				pr = pr + "*" + cli + ", ";
			}
		p.sendMessage(plugin.trans.get("avaibleclasses"));

		p.sendMessage(ChatColor.WHITE + av);

		if (!pr.equals("")) {
			p.sendMessage(plugin.trans.get("premiumclasses"));

			p.sendMessage(ChatColor.GREEN + pr);
		}
	}

	public String clearChat() {
		String m = "";
		for (int c = 0; c < 15; c++) {
			m = m + " \n ";
		}

		return m;
	}

}
