import net.askarian.MisterErwin.CTF.CTF;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TeleporterPack implements Listener {

	private final CTF plugin;

	private final Player engineer;
	private Location Entrace = null, Exit = null;

	private int entraceID, exitID;

	private int state = 0; // 0 => no exit //1 => active

	public boolean on = true;

	public TeleporterPack(CTF inheritance, Player p) {
		this.plugin = inheritance;
		this.engineer = p;

		try {
			plugin.getServer().getPluginManager().registerEvents(this, plugin);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Player getPlayer() {
		return engineer;
	}

	public Location getExit() {
		return this.Exit;
	}

	public void setEntrace(Player p, Location loc) {
		this.entraceID = loc.getBlock().getTypeId();
		loc.getBlock().setType(Material.WORKBENCH);
		loc.getBlock().getRelative(BlockFace.UP)
				.setType(Material.REDSTONE_TORCH_OFF);
		loc.getBlock().getState().update(true);
		loc.getBlock().getRelative(BlockFace.UP).getState().update(true);
		this.Entrace = loc;
		this.state = 0;
		plugin.cm.SendMessage(p, ChatColor.YELLOW
				+ "You placed the entrace teleporter!");
	}

	public void setExit(Player p, Location loc) {
		this.exitID = loc.getBlock().getTypeId();
		this.Exit = loc;
		loc.getBlock().setType(Material.WORKBENCH);
		this.Exit.getBlock().getRelative(BlockFace.UP)
				.setType(Material.REDSTONE_TORCH_ON);
		loc.getBlock().getRelative(BlockFace.UP)
				.setType(Material.REDSTONE_TORCH_ON);
		if (this.Entrace != null)
			this.Entrace.getBlock().getRelative(BlockFace.UP)
					.setType(Material.REDSTONE_TORCH_ON);
		loc.getBlock().getState().update(true);
		loc.getBlock().getRelative(BlockFace.UP).getState().update(true);
		this.state = 1;

		plugin.cm.SendMessage(p, ChatColor.YELLOW
				+ "You placed the exit of the teleporter!");

	}

	public Integer getEntryBefore() {
		return this.entraceID;
	}

	public Integer getExitBefore() {
		return this.exitID;
	}

	public void onTeleporterStand(Player p, Block tele) {
		if (!plugin.tm.getTeam(p).equals(plugin.tm.getTeam(this.engineer))) {
			plugin.cm
					.SendMessage(p,
							"This teleporter doesn't belongs to your team! - Destroy it!");
			return;
		}
		if (tele.getLocation().distance(this.Entrace) < 1) {
			if (this.state != 1) {
				plugin.cm.SendMessage(p,
						"The exit of this teleporter is missing!");
			} else
				p.teleport(this.Exit.clone().add(0, 1, 0));
		} else if (this.Exit != null
				&& tele.getLocation().distance(this.Exit) < 1) {
			plugin.cm.SendMessage(p, "This is an exit!");
		}
	}

	public void onTeleporterBreak(Player p, Block tele) {
		if (!plugin.tm.getTeam(p).equals(plugin.tm.getTeam(this.engineer))
				|| p.getName().equals(this.engineer.getName())) {
			if (tele.getLocation().distance(this.Entrace) < 1) {
				this.reset();
			}

			if (tele.getLocation().distance(this.Exit) < 1) {
				this.reset();
			}

			plugin.cm.SendMessage(this.engineer, p.getDisplayName()
					+ " destroyed your teleporter!");
		}

	}

	public void reset() {
		if (this.Entrace != null) {
			this.Entrace.getBlock().setTypeId(this.entraceID);
			this.Entrace.getBlock().getRelative(BlockFace.UP).setTypeId(0);
		}

		if (this.Exit != null) {
			this.Exit.getBlock().setTypeId(this.exitID);
			this.Exit.getBlock().getRelative(BlockFace.UP).setTypeId(0);
		}

		this.entraceID = 0;
		this.exitID = 0;

		this.Entrace = null;
		this.Exit = null;

		this.state = 0;
		on = false;
		PlayerMoveEvent.getHandlerList().unregister(this);

	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (this.state != 1)
			return;
		Location l = event.getTo().clone();
		l.setY(l.getY() - 1);
		if (l.getBlock().getType() != Material.WORKBENCH)
			return;
		onTeleporterStand(event.getPlayer(), l.getBlock());
	}
}
