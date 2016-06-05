package net.askarian.MisterErwin.CTF.threads;

import net.askarian.MisterErwin.CTF.API.events.CTFInTickEvent;
import net.askarian.MisterErwin.CTF.API.events.CTFPostTickEvent;
import net.askarian.MisterErwin.CTF.API.events.CTFPreTickEvent;
import net.askarian.MisterErwin.CTF.CTF;
import net.askarian.MisterErwin.CTF.API.events.CTFPostTickEvent;
import net.askarian.MisterErwin.CTF.CTF;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

import java.util.Iterator;

public class lookingtimer implements Runnable {

	private CTF plugin;

	public lookingtimer(CTF pl) {
		plugin = pl;
		reset();
	}

	boolean F2ndA = false, F2ndB = false;

	public void run() {

		if (plugin.getServer().getOnlinePlayers().size() == 0)
			return;
		if (!plugin.Game.idle && !plugin.Game.running && !plugin.Game.after)
			return;

		// try{
		// plugin.log.info(plugin.Game.FlagItemA.toString());
		// plugin.log.info(plugin.Game.FlagItemB.toString());
		// }catch (Exception e) {
		// }

		// Addon Events
		if (plugin.Game.idle)
			plugin.getServer().getPluginManager()
					.callEvent(new CTFPreTickEvent(plugin.Game.time2start));
		else if (plugin.Game.running)
			plugin.getServer().getPluginManager()
					.callEvent(new CTFInTickEvent(plugin.Game.time2play));
		else if (plugin.Game.after)
			plugin.getServer().getPluginManager()
					.callEvent(new CTFPostTickEvent(plugin.Game.time2leave));

		// Exist Flag?
		boolean FlagAexists = false, FlagBexists = false;

		if (plugin.Game.FlagAHolder instanceof String
				&& plugin.Game.FlagAHolder == "Home" && plugin.Game.running) {
			// for (org.bukkit.entity.Entity i :
			// plugin.Game.SpawnA.getWorld().getEntities())
			// if (i instanceof Item && ((Item)i).getUniqueId().toString() ==
			// plugin.Game.FlagItemA.toString()){
			// FlagAexists = true;
			// continue;
			// }
			if (plugin.Game.FlagItemA == null || plugin.Game.FlagItemA.isDead())
				FlagAexists = false;
			else
				FlagAexists = true;
		} else
			FlagAexists = true;

		if (plugin.Game.FlagBHolder instanceof String
				&& plugin.Game.FlagBHolder == "Home" && plugin.Game.running) {
			// for (org.bukkit.entity.Entity i :
			// plugin.Game.SpawnB.getWorld().getEntities())
			// if (i instanceof Item && ((Item)i).getUniqueId().toString() ==
			// plugin.Game.FlagItemB.toString()){
			// FlagBexists = true;
			// continue;
			// }
			if (plugin.Game.FlagItemB == null || plugin.Game.FlagItemB.isDead())
				FlagBexists = false;
			else
				FlagBexists = true;
		} else
			FlagBexists = true;

		// Reset Flag?
		if (plugin.Game.FlagAReset > 0) {
			/*
			 * if (FlagAexists && plugin.Game.FlagAHolder != "reset")
			 * plugin.Game.FlagAReset = 0; else {
			 */
			plugin.Game.FlagAReset--;
			if (plugin.Game.FlagAReset == 0) {
				plugin.cm.allMessage(ChatColor.YELLOW + "The "
						+ plugin.conm.get("TeamNameA") + ChatColor.YELLOW
						+ " flag has been restored!");
				plugin.ctfu.reset("A");
			}
			// }
		}
		// B
		if (plugin.Game.FlagBReset > 0) {
			/*
			 * if (FlagBexists && plugin.Game.FlagBHolder != "reset")
			 * plugin.Game.FlagBReset = 0; else {
			 */
			plugin.Game.FlagBReset--;
			if (plugin.Game.FlagBReset == 0) {
				plugin.cm.allMessage(ChatColor.YELLOW + "The "
						+ plugin.conm.get("TeamNameB") + ChatColor.YELLOW
						+ " flag has been restored!");
				plugin.ctfu.reset("B");
			}
			// }
		}
		// Spawn A
		Iterator<String> it;
		if (plugin.Game.spawnPlayersA != null
				&& plugin.Game.spawnPlayersA.size() > 0) {

			it = plugin.Game.spawnPlayersA.iterator();
			while (it.hasNext()) {
				Player p = plugin.getServer().getPlayerExact(
						it.next().toString());
				if (p == null || !p.isOnline())
					continue;
				if (plugin.tm.getTeam(p) == "A") {
					if (((Damageable) p).getHealth() < ((Damageable) p)
							.getMaxHealth() && !p.isDead())
						p.setHealth(((Damageable) p).getHealth() + 1);
				} else if (plugin.tm.getTeam(p) == "B") {
					if (!p.isDead())
						p.setHealth(0);
				}
			}
		}
		// Spawn B
		if (plugin.Game.spawnPlayersB != null
				&& plugin.Game.spawnPlayersB.size() > 0) {
			it = plugin.Game.spawnPlayersB.iterator();

			while (it.hasNext()) {
				Player p = plugin.getServer().getPlayerExact(
						it.next().toString());
				if (p == null || !p.isOnline())
					continue;
				if (plugin.tm.getTeam(p) == "B") {
					if (((Damageable) p).getHealth() < ((Damageable) p)
							.getMaxHealth() && !p.isDead())
						p.setHealth(((Damageable) p).getHealth() + 1);
				} else if (plugin.tm.getTeam(p) == "A") {
					if (!p.isDead())
						p.setHealth(0);
				}
			}
		}
		// Countdown
		// pre
		if (plugin.Game.idle != true) {
			reset();
		} else {
			if (plugin.Game.waitingPlayers.size() >= Integer
					.valueOf(plugin.conm.get("player2start"))
					&& plugin.Game.buildonly == false) {
				plugin.Game.time2start--;
				plugin.Game.premessage = "";
			} else {
				if (!plugin.Game.buildonly) {
					plugin.Game.premessage = ChatColor.RED
							+ " "
							+ (Integer.valueOf(plugin.conm.get("player2start")) - plugin.Game.waitingPlayers
									.size()) + " more Player!";
					plugin.Game.time2start = plugin.Game.pretime;
				} else {
					if (plugin.Game.time2start * 2 < plugin.Game.pretime) {
						boolean OPinGame = false;

						// Is an OP on the Game?
						for (String pn : plugin.Game.waitingPlayers.keySet()) {
							try {
								if (plugin.getServer().getPlayerExact(pn)
										.hasPermission("CTF.admin") == true) {
									OPinGame = true;
									break;
								}
								continue;
							} catch (Exception e) {
								continue;
							}
						}

						if (OPinGame) {
							plugin.Game.time2start = plugin.Game.pretime;
							plugin.Game.time2start = plugin.Game.pretime;
							plugin.Game.premessage = ChatColor.RED
									+ "This Map is in build-only mode!  Use /CtT next";
						} else {
							plugin.log
									.info(" Starting a new Match, cause no Player has the perm. to set the missing Locations!");
							plugin.comm.next();
						}

					} else
						plugin.Game.time2start--;
				}

			}
		}

		if (plugin.Game.time2start <= 0)
			plugin.Game.startGame();
		// SOUND
		// if (plugin.Game.time2start < 60 && plugin.Game.time2start != 0)
		// plugin.ctfu.playNote(Sound.NOTE_PIANO, 10, 1);
		// if (plugin.Game.time2start < 30 && plugin.Game.time2start != 0)
		// plugin.ctfu.playNote(Sound.NOTE_PLING, 10, 1);
		// while
		if (plugin.Game.running != true) {
			plugin.Game.time2play = plugin.Game.gametime;
		} else {
			plugin.Game.time2play--;
			plugin.ctfu.saytime(plugin.Game.time2play);
		}
		if (plugin.Game.time2play == 60)
			plugin.cm.allMessage(ChatColor.DARK_PURPLE
					+ "Game ends in 60 sec.!");
		if (plugin.Game.time2play == 30)
			plugin.cm.allMessage(ChatColor.DARK_PURPLE
					+ "Game ends in 30 sec.!");
		if (plugin.Game.time2play == 15)
			plugin.cm.allMessage(ChatColor.DARK_PURPLE
					+ "Game ends in 15 sec.!");
		if (plugin.Game.time2play == 10)
			plugin.cm.allMessage(ChatColor.DARK_PURPLE
					+ "Game ends in 10 sec.! - Hurry up!");
		if (plugin.Game.time2play == 5)
			plugin.cm.allMessage(ChatColor.DARK_PURPLE
					+ "Game ends in 5 sec.! - Hurry up!");
		if (plugin.Game.time2play == 4)
			plugin.cm.allMessage(ChatColor.DARK_PURPLE
					+ "Game ends in 4 sec.! - Hurry up!");
		if (plugin.Game.time2play == 3)
			plugin.cm.allMessage(ChatColor.DARK_PURPLE
					+ "Game ends in 3 sec.! - Hurry up!");
		if (plugin.Game.time2play == 2)
			plugin.cm.allMessage(ChatColor.DARK_PURPLE
					+ "Game ends in 2 sec.! - Hurry up!");
		if (plugin.Game.time2play == 1)
			plugin.cm
					.allMessage(ChatColor.DARK_PURPLE + "Game ends in 1 sec.!");
		if (plugin.Game.time2play <= 0)
			plugin.Game.stopCauseCounter();
		// Play Sound
		if (plugin.Game.time2play < 4) {
			if (plugin.Game.time2play < 1)
				plugin.ctfu.playNote(Sound.BLOCK_NOTE_PLING, 9, 1);
			else
				plugin.ctfu.playNote(Sound.BLOCK_NOTE_BASS, 9, 1);
		}

		// after

		if (plugin.Game.time2leave > 0)
			plugin.Game.time2leave--;
		if (plugin.Game.time2leave <= 0 && plugin.Game.after)
			plugin.Game.votenext();

		// Halt time
		if (plugin.Game.holdtime == 1 && plugin.Game.Spawn != null
				&& plugin.Game.Spawn.getWorld() != null) {
			plugin.Game.Spawn.getWorld().setTime(plugin.Game.starttimeticks);
		}

		if (plugin.Game.running) {
			if (!FlagAexists && plugin.Game.FlagAHolder == "Home") {
				if (F2ndA) {
					F2ndA = false;
					plugin.log.info("Flag A 'll reset"
							+ plugin.Game.FlagAHolder);
					plugin.Game.FlagAReset = 8;
					plugin.Game.FlagAHolder = "reset";
					F2ndA = false;
				} else
					F2ndA = true;

			}
			if (!FlagBexists && plugin.Game.FlagBHolder == "Home") {
				if (F2ndB) {
					F2ndA = false;
					plugin.log.info("Flag B 'll reset"
							+ plugin.Game.FlagBHolder);
					plugin.Game.FlagBReset = 8;
					plugin.Game.FlagBHolder = "reset";
					F2ndB = false;
				} else
					F2ndB = true;
			}
		}

	}

	public void reset() {
		plugin.Game.time2start = plugin.Game.pretime;
		plugin.Game.premessage = "";
	}

}
