package net.askarian.MisterErwin.CTF.API.events;

import java.util.HashMap;

public class PlayerScore {

	Integer deaths, kills, killstreak;

	HashMap<Long, Score.ScoreType> scores = new HashMap<Long, Score.ScoreType>();

	String name;

	public PlayerScore(String name, Integer deaths, Integer kills,
			Integer killstreak, HashMap<Long, Score.ScoreType> scores) {
		this.name = name;
		this.deaths = deaths;
		this.kills = kills;
		this.killstreak = killstreak;
		this.scores = scores;
	}

	public HashMap<Long, Score.ScoreType> getScores() {
		return this.scores;
	}

	public void setScores(HashMap<Long, Score.ScoreType> s) {
		this.scores = s;
	}

	public void addScore(Score.ScoreType st) {
		this.scores.put(System.currentTimeMillis(), st);
	}

	public Integer getDeaths() {
		return this.deaths;
	}

	public Integer getKills() {
		return this.deaths;
	}

	public Integer getKS() {
		return this.killstreak;
	}

	public void setDeaths(Integer i) {
		this.deaths = i;
	}

	public void setKills(Integer i) {
		this.kills = i;
	}

	public void setKS(Integer i) {
		this.killstreak = i;
	}
}
