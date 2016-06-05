package net.askarian.MisterErwin.CTF.util;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryManager {

	public HashMap<String, ItemStack[]> invs = new HashMap<String, ItemStack[]>();
	public HashMap<String, ItemStack[]> Ainvs = new HashMap<String, ItemStack[]>();
	public HashMap<String, Integer> XP = new HashMap<String, Integer>();
	public HashMap<String, Location> locs = new HashMap<String, Location>();

	public void put(Player p) {
		if (this.invs.containsKey(p.getName()))
			this.invs.remove(p.getName());

		this.invs.put(p.getName(), p.getInventory().getContents());
		this.Ainvs.put(p.getName(), p.getInventory().getArmorContents());
		this.XP.put(p.getName(), p.getTotalExperience());
		this.locs.put(p.getName(), p.getLocation());
	}

	public void give(Player p) {
		if (!this.invs.containsKey(p.getName()))
			return;

		p.getInventory().setContents(this.invs.get(p.getName()));
		p.getInventory().setArmorContents(this.Ainvs.get(p.getName()));
		p.setTotalExperience(this.XP.get(p.getName()));
		p.teleport(this.locs.get(p.getName()));
	}

}
