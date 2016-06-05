package net.askarian.MisterErwin.CTF;

import net.askarian.MisterErwin.CTF.API.events.ChangeAwayFromClassEvent;
import net.askarian.MisterErwin.CTF.API.events.ChangeToClassEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public class TeamManager {
	public Map<String, String> TeamA = new HashMap();
	public Map<String, String> TeamB = new HashMap();

	public HashMap<String, String> offlineP = new HashMap();
	private CTF plugin;

	public TeamManager(CTF pl) {
		this.plugin = pl;
	}

	public Team AScoreTeam;
	public Team BScoreTeam;

	public String getTeam(Player p) {
		if (this.TeamA.containsKey(p.getName()))
			return "A";
		if (this.TeamB.containsKey(p.getName()))
			return "B";
		return "";
	}

	public String getTeam(String p) {
		if (this.TeamA.containsKey(p))
			return "A";
		if (this.TeamB.containsKey(p))
			return "B";
		return "";
	}

	public void setTeam(Player p, String team) {
		if (team.equals("A")) {
			this.TeamA.put(p.getName(), plugin.clm.Classes.get(0).getCommand());
			this.AScoreTeam.addPlayer(p);
		} else if (team.equals("B")) {
			this.TeamB.put(p.getName(), plugin.clm.Classes.get(0).getCommand());
			this.BScoreTeam.addPlayer(p);

		} else {
			this.TeamA.put(p.getName(), plugin.clm.Classes.get(0).getCommand());
			this.AScoreTeam.addPlayer(p);

		}
	}

	public String getPClass(Player p) {
		if (this.TeamA.containsKey(p.getName()))
			return this.TeamA.get(p.getName());
		if (this.TeamB.containsKey(p.getName()))
			return this.TeamB.get(p.getName());
		return "";
	}

	public void setClass(Player p, String kit) {
		if (this.TeamA.containsKey(p.getName())) {
			// Call the events
			plugin.getServer().getPluginManager()
					.callEvent(new ChangeAwayFromClassEvent(p));
			plugin.getServer().getPluginManager()
					.callEvent(new ChangeToClassEvent(p));
			this.TeamA.put(p.getName(), kit);
		} else if (this.TeamB.containsKey(p.getName())) {
			// Call the events
			plugin.getServer().getPluginManager()
					.callEvent(new ChangeAwayFromClassEvent(p));
			plugin.getServer().getPluginManager()
					.callEvent(new ChangeToClassEvent(p));
			this.TeamB.put(p.getName(), kit);
		}
	}

	public void JoinTeam(Player p) {
		if ((this.TeamA.containsKey(p.getName()))
				|| (this.TeamB.containsKey(p.getName()))) {
			return;
		}
		// if (this.offlineP.containsKey(p.getName()) && false)
		// {
		// String oteam = (String)this.offlineP.get(p.getName());
		// if (oteam == "A") {
		// if (this.TeamA.size() < this.TeamB.size() - 5)
		// setTeam(p, "A");
		// else
		// setTeam(p, "B");
		// }
		// else if (this.TeamA.size() < this.TeamB.size() - 5)
		// setTeam(p, "A");
		// else {
		// setTeam(p, "B");
		// }
		//
		// }
		else if (this.TeamA.size() < this.TeamB.size()) {
			setTeam(p, "A");
		} else {
			setTeam(p, "B");
		}
		p.getInventory().clear();
		if (plugin.Game.running) {
			if (plugin.tm.getTeam(p).equals("A")) {
				p.teleport(plugin.Game.SpawnA);
			} else if (plugin.tm.getTeam(p).equals("B")) {
				p.teleport(plugin.Game.SpawnB);
			}
		} else if (plugin.Game.idle) {
			p.teleport(plugin.Game.Spawn);
		} else
			p.setHealth(0);

		p.setScoreboard(plugin.cm.sboard);
	}

	public void LeaveTeam(Player p, boolean onquit) {
		if (Boolean.valueOf(plugin.conm.get("auto"))  && !onquit) {
			plugin.cm.SendMessage(p, ChatColor.RED
					+ "You can't leave the game!");
			return;
		}

		p.setScoreboard(plugin.cm.smanager.getNewScoreboard()); // manager.getNewScoreboard()
																// will return a
																// blank
																// scoreboard

		String team = "";
		if (this.TeamA.containsKey(p.getName())) {
			team = "A";
			this.TeamA.remove(p.getName());
			this.AScoreTeam.removePlayer(p);

		} else if (this.TeamB.containsKey(p.getName())) {
			team = "B";
			this.TeamB.remove(p.getName());
			this.BScoreTeam.removePlayer(p);

		}
		if (this.plugin.Game.waitingPlayers.containsKey(p.getName()))
			this.plugin.Game.waitingPlayers.remove(p.getName());

		plugin.cm.allupdate();

		this.offlineP.put(p.getName(), team);
		p.getInventory().clear();
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);

		p.setLevel(0);
		p.setExp(0);
		p.sendMessage(plugin.cm.clearChat());
		p.sendMessage(plugin.cm.clearChat());
		p.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());

		ChangeAwayFromClassEvent e = new ChangeAwayFromClassEvent(p);

		plugin.InvM.give(p);

		p.setDisplayName(ChatColor.stripColor(p.getDisplayName()));
	}

	public void PlayerJoin(Player p) {
		if (this.offlineP.containsKey(p.getName())) {
			this.plugin.cm.SendMessage(p, ChatColor.YELLOW + "Welcome back");
			JoinTeam(p);
		}
	}

	public boolean isinGame(Player p) {
		return  (this.getTeam(p).equals("A") || this.getTeam(p).equals("B"));
	}

	public boolean isinGameWorld(Player p) {
		return (plugin.Game.Spawn != null
				&& p.getWorld() == plugin.Game.SpawnA.getWorld());
	}

	public boolean hasFlag(Player p) {
		return this.plugin.Game.FlagAHolder instanceof Player && this.plugin.Game.FlagAHolder == p || (this.plugin.Game.FlagBHolder instanceof Player && this.plugin.Game.FlagBHolder == p);
	}
}
