import net.askarian.MisterErwin.CTF.API.CtFClass;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class chemist extends CtFClass implements Listener {

	private String version = "1.0";
	private String name = "Chemist-Class";
	private String description = "";
	private String command = "chemist";
	private List<String> authors = new ArrayList<String>();
	private ItemStack icon = new ItemStack(Material.POTION, 1, (short) 16428);

	@Override
	public void disable() {
		PotionSplashEvent.getHandlerList().unregister(this);

	}

	@Override
	public void enable() {
		authors.add("MisterErwin");
		try {
			/* Register the Listener? */
			super.plugin.getServer().getPluginManager()
					.registerEvents(this, super.plugin);
		} catch (Exception exe) {
			System.out.println("Enabling of " + this.getClass().getSimpleName()
					+ " has failed due to " + exe.getLocalizedMessage());
		}

	}

	@Override
	public List<String> getAuthors() {
		return authors;
	}

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public ItemStack getIcon() {
		return this.icon;
	}

	@Override
	public boolean givekit(Player p) {
		PlayerInventory inv = p.getInventory();
		ItemStack item;

		// Compass
		item = new ItemStack(Material.COMPASS);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(ChatColor.AQUA + "Enemy Flag Locator");
		ArrayList<String> al = new ArrayList<String>();
		al.add(ChatColor.YELLOW + "Locate your enemy flag");
		item.setItemMeta(im);
		im.setLore(al);
		inv.setItem(8, item);

		// Rüstung
		// Helm
		item = new ItemStack(298, 1);
		inv.setHelmet(item);
		// Brust
		item = new ItemStack(315, 1);
		inv.setChestplate(item);
		// Hose
		item = new ItemStack(317, 1);
		inv.setLeggings(item);
		// Schuhe
		item = new ItemStack(301, 1);
		inv.setBoots(item);
		// Items
		// Schwert
		item = new ItemStack(267, 1);
		inv.addItem(item);
		// Splash Potions //16384
		// 20x Instant Damage II
		item = new ItemStack(Material.POTION, 20, (short) 16428);
		inv.addItem(item);
		// 10x Poison II (0:16)
		item = new ItemStack(Material.POTION, 10, (short) 16420);
		inv.addItem(item);
		// 2x Slowness (3:00)
		item = new ItemStack(Material.POTION, 2, (short) 16458);
		inv.addItem(item);
		// 4x Fire / (2:15)
		item = new ItemStack(Material.POTION, 4, (short) 16387);
		inv.addItem(item);
		// 10x instant Health II
		item = new ItemStack(Material.POTION, 10, (short) 16437);
		inv.addItem(item);
		// 5x Regeneration II
		item = new ItemStack(Material.POTION, 5, (short) 16417);
		inv.addItem(item);
		// 2x Speed II (1:07)
		item = new ItemStack(Material.POTION, 2, (short) 16418);
		inv.addItem(item);
		// 2x Strength II (1:07)
		item = new ItemStack(Material.POTION, 2, (short) 16425);
		inv.addItem(item);
		// 2x Weakness (3:00)
		item = new ItemStack(Material.POTION, 2, (short) 16472);
		inv.addItem(item);

		return true;
	}

	// Listener

	private Integer[] goodPotions = { 1, // SPEED
			3, // Fast_DIGGING
			5, // INCREASE_DAMAGE
			6, // HEAL
			8, // JUMP
			10, // REGENERATION
			11, // DAMAGE Resistance
			12, // FIRE_RESISTANCE
			13, // WATHER_BREATHING
			14, // INVISIBILITY
			16 // NIGHT_VISION

	};

	private Integer[] badPotions = { 2, // SLOW
			4, // SLOW_DIGGING
			7, // HARM
			9, // CONFUSION
			15, // BLINDNESS
			17, // HUNGER
			18, // WEAKNESS
			19 // POISON

	};

	@EventHandler
	public void onPotionSplash(PotionSplashEvent event) {
		if (super.plugin.Game.Spawn == null
				|| event.getEntity().getWorld() != super.plugin.Game.Spawn
						.getWorld())
			return;
		Projectile sp = event.getEntity();
		Player shooter = (Player) sp.getShooter();
		if (!(shooter instanceof Player))
			return;
		if (!super.plugin.tm.getPClass(shooter).equalsIgnoreCase("chemist"))
			return;
		for (PotionEffect pe : event.getPotion().getEffects()) {

			if (Arrays.asList(badPotions).contains(pe.getType().getId())) {
				// bad
				for (LivingEntity e : event.getAffectedEntities()) {
					if (!(e instanceof Player))
						continue;
					Player p = (Player) e;
					if (p == shooter
							|| super.plugin.tm.getTeam(p.getName())
									.equalsIgnoreCase(
											super.plugin.tm.getTeam(shooter
													.getName())))
						continue; // Same Team/Player
					else
						p.addPotionEffect(pe);
				}
			} else if (Arrays.asList(goodPotions)
					.contains(pe.getType().getId())) {
				// good
				for (LivingEntity e : event.getAffectedEntities()) {
					if (!(e instanceof Player))
						continue;
					Player p = (Player) e;
					if (!super.plugin.tm.getTeam(p.getName()).equalsIgnoreCase(
							super.plugin.tm.getTeam(shooter.getName())))
						continue; // Not Same Team
					else
						p.addPotionEffect(pe);
				}
			} else
				shooter.sendMessage("Unknown Potion: ---> "
						+ pe.getType().getId());
		}

		event.setCancelled(true);

	}

	@Override
	public boolean saytime(Player p, int time) {
		p.setLevel(time);
		return true;
	}

}
