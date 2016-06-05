
import net.askarian.MisterErwin.CTF.API.Addons.AnimationManagerTemplate;
import net.askarian.MisterErwin.CTF.CTF;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class AnimationManager extends AnimationManagerTemplate {

	/*
	 * The default Animation-Manager!
	 * 
	 * Until now it animates only the flag carrier, 'll add the flag in base
	 * animation later!!!
	 * 
	 * ~MisterErwin
	 */

	public HashMap<Integer, Item> AItems = new HashMap<Integer, Item>();
	public Integer AID = 1;

	public HashMap<Integer, Item> BItems = new HashMap<Integer, Item>();
	public Integer BID = 1;

	private CTF plugin;

	public void run() {

		// cancel if game isn't running
		if (!plugin.Game.running)
			return;

		if (plugin.Game.FlagAHolder instanceof Player) {

			if (this.AItems.containsKey(this.AID)) {
				this.AItems.get(this.AID).remove();
				this.AItems.remove(this.AID);
			}

			// ANIMATE PLAYER A
			Location pl = ((Player) plugin.Game.FlagAHolder).getLocation().add(
					0, 1.9, 0);

			Entity e = plugin.Game.SpawnA.getWorld()
					.dropItem(
							pl,
							new ItemStack(Material.WOOL, 1, DyeColor.RED
									.getWoolData()));
			e.setVelocity(new Vector(0, 0.37, 0));

			this.AItems.put(this.AID, (Item) e);

			this.AID++;

			// ///////////////////////////////////////////////////////////////////////////////////

			Location pl1 = pl.clone();
			pl1.setYaw(pl1.getYaw() + 90);

			e = plugin.Game.SpawnA.getWorld()
					.dropItem(
							pl1,
							new ItemStack(Material.WOOL, 1, DyeColor.RED
									.getWoolData()));
			e.setVelocity(new Vector(0, 0.37, 0));

			this.AItems.put(this.AID, (Item) e);

			this.AID++;

			// /////////////////////////////////////////////////////////////////////////////////////

			pl1 = pl.clone();
			pl1.setYaw(pl1.getYaw() - 90);

			e = plugin.Game.SpawnA.getWorld()
					.dropItem(
							pl1,
							new ItemStack(Material.WOOL, 1, DyeColor.RED
									.getWoolData()));
			e.setVelocity(new Vector(0, 0.37, 0));

			this.AItems.put(this.AID, (Item) e);

			// //////////////////////////////////////////////////////////////////////////////////////

			// REMOVE ITEMS
			if (this.AItems.containsKey(this.AID - 3)) {
				this.AItems.get(this.AID - 3).remove();
				this.AItems.remove(this.AID - 3);
			}
			if (this.AItems.containsKey(this.AID - 4)) {
				this.AItems.get(this.AID - 4).remove();
				this.AItems.remove(this.AID - 4);
			}
			if (this.AItems.containsKey(this.AID - 5)) {
				this.AItems.get(this.AID - 5).remove();
				this.AItems.remove(this.AID - 5);
			}

			this.AID++;

		} else {
			Iterator<Entry<Integer, Item>> it = this.AItems.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, Item> pairs = it.next();
				if (pairs.getValue() instanceof Item)
					pairs.getValue().remove();
				it.remove(); // avoids a ConcurrentModificationException
			}
		}

		// /////////////
		// BBBBBBBBBBBBBBBBBBBBB
		// ////////////

		if (plugin.Game.FlagBHolder instanceof Player) {

			if (this.BItems.containsKey(this.BID)) {
				this.BItems.get(this.BID).remove();
				this.BItems.remove(this.BID);
			}
			// ANIMATE PLAYER B
			Location pl = ((Player) plugin.Game.FlagBHolder).getLocation().add(
					0, 1.9, 0);

			Entity e = plugin.Game.SpawnB.getWorld()
					.dropItem(
							pl,
							new ItemStack(Material.WOOL, 1, DyeColor.BLUE
									.getWoolData()));
			e.setVelocity(new Vector(0, 0.37, 0));

			((Item) e).setPickupDelay(99999);

			this.BItems.put(this.BID, (Item) e);

			this.BID++;

			// ///////////////////////////////////////////////////////////////////////////////////

			Location pl1 = pl.clone();
			pl1.setYaw(pl1.getYaw() + 90);

			e = plugin.Game.SpawnB.getWorld()
					.dropItem(
							pl1,
							new ItemStack(Material.WOOL, 1, DyeColor.BLUE
									.getWoolData()));
			e.setVelocity(new Vector(0, 0.37, 0));

			this.BItems.put(this.BID, (Item) e);

			this.BID++;

			// /////////////////////////////////////////////////////////////////////////////////////

			pl1 = pl.clone();
			pl1.setYaw(pl1.getYaw() - 90);

			e = plugin.Game.SpawnB.getWorld()
					.dropItem(
							pl1,
							new ItemStack(Material.WOOL, 1, DyeColor.BLUE
									.getWoolData()));
			e.setVelocity(new Vector(0, 0.37, 0));

			((Item) e).setPickupDelay(99999);

			this.BItems.put(this.BID, (Item) e);

			// //////////////////////////////////////////////////////////////////////////////////////

			// REMOVE ITEMS
			if (this.BItems.containsKey(this.BID - 3)) {
				// plugin.log.info(AID + " - > " + AID-8);
				this.BItems.get(this.BID - 3).remove();
				this.BItems.remove(this.BID);
			}
			if (this.BItems.containsKey(this.BID - 4)) {
				// plugin.log.info(AID + " - > " + AID-8);
				this.BItems.get(this.BID - 4).remove();
				this.BItems.remove(this.BID - 4);
			}
			if (this.BItems.containsKey(this.BID - 5)) {
				// plugin.log.info(AID + " - > " + AID-8);
				this.BItems.get(this.BID - 5).remove();
				this.BItems.remove(this.BID - 5);
			}

			this.BID++;

		} else {
			Iterator<Entry<Integer, Item>> it = this.BItems.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, Item> pairs = it.next();
				if (pairs.getValue() instanceof Item)
					pairs.getValue().remove();
				it.remove(); // avoids a ConcurrentModificationException
			}
		}

	}

	public boolean isinList(Item i) {
		if (AItems.containsValue(i))
			return true;

		if (BItems.containsValue(i))
			return true;

		return false;

	}

	@Override
	public long getPeriod() {
		return 5L;
	}

	@Override
	public String getName() {
		return "Default Animation Manager";
	}

	@Override
	public void enable(CTF plugin) {
		this.plugin = plugin;

	}

	// @EventHandler (priority=EventPriority.LOWEST, ignoreCancelled=true)
	// public void onPlayerPickupItem(PlayerPickupItemEvent event) {
	// if (!plugin.tm.isinGame(event.getPlayer()))
	// return;
	//
	// if (event.getItem().getItemStack().getType() != Material.WOOL){
	// return;
	// }
	//
	// Item i = event.getItem();
	//
	//
	// if (this.isinList(i))
	// event.setCancelled(true);
	//
	// }
}
