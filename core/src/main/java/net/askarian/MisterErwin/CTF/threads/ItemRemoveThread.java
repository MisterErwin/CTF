package net.askarian.MisterErwin.CTF.threads;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemRemoveThread implements Runnable {

	private Player p;
	private ItemStack is;

	public ItemRemoveThread(Player player, ItemStack items) {
		p = player;
		is = items;
	}

	@Override
	public void run() {
		p.getInventory().removeItem(is);
	}
}
