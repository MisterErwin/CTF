package net.askarian.MisterErwin.CTF;

import net.askarian.MisterErwin.CTF.API.events.CTFMatchEndEvent;
import net.askarian.MisterErwin.CTF.API.events.PlayerScore;
import net.askarian.MisterErwin.CTF.API.events.Score.ScoreType;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Tracker {

	private CTF plugin;

	public HashMap<String, Integer> Kills = new HashMap<String, Integer>();

	public HashMap<String, Integer> Killsrow = new HashMap<String, Integer>();

	public HashMap<String, Integer> Deaths = new HashMap<String, Integer>();

	public HashMap<String, Integer> KillStreak = new HashMap<String, Integer>();

	public HashMap<String, PlayerScore> scores = new HashMap<String, PlayerScore>();

	public Tracker(CTF ctf) {
		plugin = ctf;
	}

	public void addKill(Player p) {
		if (p == null)
			return;
		int before = 0;
		if (Kills.containsKey(p.getName())) {
			before = Kills.get(p.getName());
			Kills.remove(p.getName());
		}

		if (!this.KillStreak.containsKey(p.getName()))
			this.KillStreak.put(p.getName(), 0);

		int i = 1;
		if (Killsrow.containsKey(p.getName())) {
			i = Killsrow.get(p.getName()) + 1;
			Killsrow.remove(p.getName());

		}

		Killsrow.put(p.getName(), i);

		if (Killsrow.get(p.getName()) > KillStreak.get(p.getName())) {
			this.KillStreak.remove(p.getName());
			this.KillStreak.put(p.getName(), Killsrow.get(p.getName()));

			this.addScore(p.getName(), ScoreType.KillStreak);
		}

		Kills.put(p.getName(), before + 1);

		this.addScore(p.getName(), ScoreType.Kill);

	}

	public void addDeath(Player p) {
		int before = 0;

		if (p == null || p.getName() == null)
			return;

		if (Deaths.containsKey(p.getName())) {
			before = Deaths.get(p.getName());
			Deaths.remove(p.getName());
		}

		// KillsRow reset

		if (Killsrow.containsKey(p.getName())) {
			Killsrow.remove(p.getName());
			Killsrow.put(p.getName(), 0);
		}

		Deaths.put(p.getName(), before + 1);

		this.addScore(p.getName(), ScoreType.Death);

	}

	public void reset() {
		this.Deaths.clear();
		this.Kills.clear();
		this.Killsrow.clear();
		this.KillStreak.clear();
	}

	public Integer getKills(String playername) {
		if (this.Kills.containsKey(playername))
			return this.Kills.get(playername);
		return 0;
	}

	public Integer getKillsinrow(String playername) {
		if (this.Killsrow.containsKey(playername))
			return this.Killsrow.get(playername);
		return 0;
	}

	public Integer getKillStreak(String playername) {
		if (this.KillStreak.containsKey(playername))
			return this.KillStreak.get(playername);
		return 0;
	}

	public Integer getDeaths(String playername) {
		if (this.Deaths.containsKey(playername))
			return this.Deaths.get(playername);
		return 0;
	}

	public void win(String team) {

		// ArrayList<Score.ScoreType> al = new ArrayList<Score.ScoreType>();
		PlayerScore ps;

		if (team.equals("A")) {
			for (String pn : this.plugin.tm.TeamA.keySet()) {

				this.addScore(pn, ScoreType.Win);

			}

			for (String pn : this.plugin.tm.TeamB.keySet()) {
				this.addScore(pn, ScoreType.Lose);

			}
		} else if (team.equals("B")) {

			for (String pn : this.plugin.tm.TeamA.keySet()) {
				this.addScore(pn, ScoreType.Lose);

			}

			for (String pn : this.plugin.tm.TeamB.keySet()) {

				this.addScore(pn, ScoreType.Win);

			}
		} else if (team.equals("")) {
			for (String pn : this.plugin.tm.TeamA.keySet()) {

				this.addScore(pn, ScoreType.Patt);

			}

			for (String pn : this.plugin.tm.TeamB.keySet()) {

				this.addScore(pn, ScoreType.Patt);

			}
		}

		// Add generall scores to every player:
		for (String pn : this.plugin.tm.TeamA.keySet()) {

			if (!this.scores.containsKey(pn))
				this.scores.put(pn, new PlayerScore(pn, this.getDeaths(pn),
						this.getKills(pn), this.getKillStreak(pn),
						new HashMap<Long, ScoreType>()));
			else {
				PlayerScore tps = this.scores.get(pn);
				this.scores.remove(pn);
				tps.setDeaths(this.getDeaths(pn));
				tps.setKills(this.getKills(pn));
				tps.setKS(this.getKillStreak(pn));

				this.scores.put(pn, tps);

			}

		}

		for (String pn : this.plugin.tm.TeamB.keySet()) {

			if (!this.scores.containsKey(pn))
				this.scores.put(pn, new PlayerScore(pn, this.getDeaths(pn),
						this.getKills(pn), this.getKillStreak(pn),
						new HashMap<Long, ScoreType>()));
			else {
				PlayerScore tps = this.scores.get(pn);
				this.scores.remove(pn);
				tps.setDeaths(this.getDeaths(pn));
				tps.setKills(this.getKills(pn));
				tps.setKS(this.getKillStreak(pn));

				this.scores.put(pn, tps);
			}

		}

		plugin.getServer()
				.getPluginManager()
				.callEvent(
						new CTFMatchEndEvent(
								plugin.Game.Spawn.getWorld().getName(),
								this.scores));

		this.scores.clear();

	}

	public void addScore(String pn, ScoreType type) {
		if (!this.scores.containsKey(pn))
			this.scores.put(pn,
					new PlayerScore(pn, this.getDeaths(pn), this.getKills(pn),
							this.getKillStreak(pn),
							new HashMap<Long, ScoreType>()));

		PlayerScore ps = this.scores.get(pn);
		ps.addScore(type);

		this.scores.remove(pn);
		this.scores.put(pn, ps);
	}

}
