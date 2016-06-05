package net.askarian.MisterErwin.CTF.listener;

import java.util.HashMap;

import net.askarian.MisterErwin.CTF.CTF;

import net.askarian.MisterErwin.CTF.CTF;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class SpongeListener implements Listener {

	private HashMap<String, String> shotedplayer = new HashMap<String, String>();
	private HashMap<String, Object[]> shotedplayer2 = new HashMap<String, Object[]>();

	private CTF plugin;

	public SpongeListener(CTF ctf) {
		plugin = ctf;
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if (!plugin.Game.idle && !plugin.Game.running)
			return;
		if (!(event.getEntity() instanceof Player))
			return;
		Player p = (Player) event.getEntity();
		if (!(event.getCause() == DamageCause.FALL))
			return;
		if (!plugin.tm.isinGameWorld(p))
			return;
		if (shotedplayer.containsKey(p.getName())) {
			shotedplayer.remove(p.getName());
			event.setDamage(0);
			event.setCancelled(true);
		}

		if (shotedplayer2.containsKey(p.getName())) {
			shotedplayer2.remove(p.getName());
			event.setDamage(0);
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerMoveonBlock(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (!plugin.tm.isinGameWorld(p))
			return;
		if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SPONGE
				&& p.getLocation().getBlock().getRelative(BlockFace.DOWN)
						.getRelative(BlockFace.DOWN).getType() == Material.SIGN_POST)
			shotsign(p);
		else if (p.getLocation().getBlock().getRelative(BlockFace.DOWN)
				.getType() == Material.SPONGE)
			shot(p);

	}

	private void shotsign(Player p) {

		Location loc = new Location(p.getWorld(), 0, 0, 0);

		Sign s = (Sign) p.getLocation().getBlock().getRelative(BlockFace.DOWN)
				.getRelative(BlockFace.DOWN).getState();

		Double speed = 1.0;

		if (s.getLine(0).contains("+")) {
			if (!s.getLine(0).trim().split("\\+")[0]
					.equalsIgnoreCase("CTF_Sponge"))
				return;
			try {
				speed = Double.parseDouble(s.getLine(0).trim().split("\\+")[1]
						.trim());
			} catch (NumberFormatException e) {
				return;
			}
		} else {
			if (!s.getLine(0).trim().equalsIgnoreCase("CTF_Sponge"))
				return;
		}

		try {
			loc.setX(Float.parseFloat(s.getLine(1).trim()));
			loc.setY(Float.parseFloat(s.getLine(2).trim()));
			loc.setZ(Float.parseFloat(s.getLine(3).trim()));

		} catch (NumberFormatException e) {
			return;
		}

		Object[] o = { loc, speed };

		shotedplayer2.put(p.getName(), o);

		if (speed < 1)
			p.setAllowFlight(true);

		Vector dir = ((Location) this.shotedplayer2.get(p.getName())[0])
				.toVector().subtract(p.getLocation().toVector()).normalize();
		p.setVelocity(dir.multiply(speed));

	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();

		if (this.shotedplayer2.containsKey(p.getName())) {

			if (p.getLocation().distance(
					(Location) this.shotedplayer2.get(p.getName())[0]) < 1) {

				p.setVelocity(new Vector(0, 0, 0));

				p.setFallDistance(0);

				event.setCancelled(true);

				p.setSprinting(false);
				if (p.getGameMode() != GameMode.CREATIVE)
					p.setAllowFlight(false);

				this.shotedplayer2.remove(p.getName());
				if (nofalldamage(p.getLocation().getBlock()
						.getRelative(BlockFace.DOWN).getType())
						|| nofalldamage(p.getLocation().getBlock()
								.getRelative(BlockFace.DOWN)
								.getRelative(BlockFace.DOWN).getType())
						|| nofalldamage(p.getLocation().getBlock()
								.getRelative(BlockFace.DOWN)
								.getRelative(BlockFace.DOWN)
								.getRelative(BlockFace.DOWN).getType())) {
				} else
					this.shotedplayer.put(p.getName(), "---");

				return;
			}

			Double speed = (Double) this.shotedplayer2.get(p.getName())[1];

			if (p.getLocation().distance(
					(Location) this.shotedplayer2.get(p.getName())[0]) < 10) {

				if (speed > 1) {
					speed *= 0.5;
					if (speed < 1)
						speed = 1.0;
				}
			}

			Vector dir = ((Location) this.shotedplayer2.get(p.getName())[0])
					.toVector().subtract(p.getLocation().toVector())
					.normalize();

			p.setVelocity(dir.multiply(speed));
			p.setFallDistance(0);
		}

	}

	private boolean nofalldamage(Material m) {
		if (m == Material.AIR || m == Material.WATER || m == Material.SOUL_SAND)
			return false;

		return true;
	}

	private void shot(Player p) {

		// ///////////////////////////////////////////
		Block sb = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
		Block ub = sb.getRelative(BlockFace.DOWN);

		Block tmp;
		double h = 0;
		double v = 0;
		int max_p = 25;
		int max = 0;

		String dir = "up";
		Block db = ub; // h power <> ub == vpower
		// X-
		// -X launching
		/*
		 * if (sb.getRelative(BlockFace.NORTH).getType() == Material.SPONGE &&
		 * !checkotherdirectionsforSponge(ub, BlockFace.NORTH)) { dir = "NORTH";
		 * } else if (ub.getRelative(BlockFace.EAST).getType() ==
		 * Material.SPONGE&& !checkotherdirectionsforSponge(ub, BlockFace.EAST))
		 * { dir = "EAST"; } else if (ub.getRelative(BlockFace.SOUTH).getType()
		 * == Material.SPONGE&& !checkotherdirectionsforSponge(ub,
		 * BlockFace.SOUTH)) { dir = "SOUTH"; } else if
		 * (ub.getRelative(BlockFace.WEST).getType() == Material.SPONGE&&
		 * !checkotherdirectionsforSponge(ub, BlockFace.WEST)) { dir = "WEST"; }
		 * else
		 */if (sb.getRelative(BlockFace.NORTH).getType() == Material.SPONGE
				&& ub.getRelative(BlockFace.NORTH).getType() == Material.SPONGE)
			dir = "up";
		else if (sb.getRelative(BlockFace.NORTH_EAST).getType() == Material.SPONGE
				&& ub.getRelative(BlockFace.NORTH_EAST).getType() == Material.SPONGE)
			dir = "up";
		else if (sb.getRelative(BlockFace.EAST).getType() == Material.SPONGE
				&& ub.getRelative(BlockFace.EAST).getType() == Material.SPONGE)
			dir = "up";
		else if (sb.getRelative(BlockFace.SOUTH_EAST).getType() == Material.SPONGE
				&& ub.getRelative(BlockFace.SOUTH_EAST).getType() == Material.SPONGE)
			dir = "up";
		else if (sb.getRelative(BlockFace.SOUTH).getType() == Material.SPONGE
				&& ub.getRelative(BlockFace.SOUTH).getType() == Material.SPONGE)
			dir = "up";
		else if (sb.getRelative(BlockFace.SOUTH_WEST).getType() == Material.SPONGE
				&& ub.getRelative(BlockFace.SOUTH_WEST).getType() == Material.SPONGE)
			dir = "up";
		else if (sb.getRelative(BlockFace.WEST).getType() == Material.SPONGE
				&& ub.getRelative(BlockFace.WEST).getType() == Material.SPONGE)
			dir = "up";
		else if (sb.getRelative(BlockFace.NORTH_WEST).getType() == Material.SPONGE
				&& ub.getRelative(BlockFace.NORTH_WEST).getType() == Material.SPONGE)
			dir = "up";
		// Other directions?
		else if (ub.getRelative(BlockFace.NORTH).getType() == Material.SPONGE) {
			dir = "NORTH";
			db = ub.getRelative(BlockFace.NORTH);
		} else if (ub.getRelative(BlockFace.EAST).getType() == Material.SPONGE) {
			dir = "EAST";
			db = ub.getRelative(BlockFace.EAST);
		} else if (ub.getRelative(BlockFace.SOUTH).getType() == Material.SPONGE) {
			dir = "SOUTH";
			db = ub.getRelative(BlockFace.SOUTH);
		} else if (ub.getRelative(BlockFace.WEST).getType() == Material.SPONGE) {
			dir = "WEST";
			db = ub.getRelative(BlockFace.WEST);
		} else if (ub.getRelative(BlockFace.NORTH_EAST).getType() == Material.SPONGE) {
			dir = "NORTH_EAST";
			db = ub.getRelative(BlockFace.NORTH_EAST);
		} else if (ub.getRelative(BlockFace.SOUTH_EAST).getType() == Material.SPONGE) {
			dir = "SOUTH_EAST";
			db = ub.getRelative(BlockFace.SOUTH_EAST);
		} else if (ub.getRelative(BlockFace.SOUTH_WEST).getType() == Material.SPONGE) {
			dir = "SOUTH_WEST";
			db = ub.getRelative(BlockFace.SOUTH_WEST);
		} else if (ub.getRelative(BlockFace.NORTH_WEST).getType() == Material.SPONGE) {
			dir = "NORTH_WEST";
			db = ub.getRelative(BlockFace.NORTH_WEST);
		}

		if (dir.equalsIgnoreCase("up")) {
			tmp = ub;
			max = max_p;
			while (max > 0) {
				tmp = tmp.getRelative(BlockFace.DOWN);
				if (tmp.getType() == Material.SPONGE) {
					v = v + plugin.Game.vboost;
					max--;
				} else if (max == max_p) {
					v = plugin.Game.vboost;
					max = -9;
				} else
					max = -99;
			}
			p.setVelocity(new Vector(0, v, 0));
			shotedplayer.put(p.getName(), "up");
		} else {
			// Horizontal
			tmp = db;
			max = max_p;
			while (max > 0) {
				tmp = tmp.getRelative(BlockFace.DOWN);
				if (tmp.getType() == Material.SPONGE) {
					h = h + plugin.Game.hboost;
					max--;
				} else
					max = -99;

			}
			// Vertical
			tmp = ub;
			max = max_p;
			while (max > 0) {
				tmp = tmp.getRelative(BlockFace.DOWN);
				if (tmp.getType() == Material.SPONGE) {
					v = v + plugin.Game.vboost;
					max--;
				} else if (max == max_p) {
					v = plugin.Game.vboost;
					max = -9;
				} else
					max = -99;
			}
			// Direction
			double x = 0;
			double z = 0;

			if (h < 0.1 && plugin.Game.hboost > 0.1)
				h = plugin.Game.hboost / 2;
			else if (h < 0.1)
				h = 5 / 2;

			// if (dir.equalsIgnoreCase("NORTH"))
			if (dir.equalsIgnoreCase("West"))
				x = h;
			// else if (dir.equalsIgnoreCase("EAST"))
			else if (dir.equalsIgnoreCase("NORTH"))
				z = h;
			// else if (dir.equalsIgnoreCase("SOUTH"))
			else if (dir.equalsIgnoreCase("EAST"))
				x = -h;
			// else if (dir.equalsIgnoreCase("WEST"))
			else if (dir.equalsIgnoreCase("SOUTH"))
				z = -h;

			p.setVelocity(new Vector(x, v, z).multiply(1.1));
			shotedplayer.put(p.getName(), "---");

		}
	}

	private boolean checkotherdirectionsforSponge(Block b, BlockFace bf) {
		if (bf != BlockFace.NORTH)
			if (b.getRelative(BlockFace.NORTH).getType() == Material.SPONGE)
				return true;
		if (bf != BlockFace.EAST)
			if (b.getRelative(BlockFace.EAST).getType() == Material.SPONGE)
				return true;
		if (bf != BlockFace.SOUTH)
			if (b.getRelative(BlockFace.SOUTH).getType() == Material.SPONGE)
				return true;
		if (bf != BlockFace.WEST)
			if (b.getRelative(BlockFace.WEST).getType() == Material.SPONGE)
				return true;
		return false;
	}

}
