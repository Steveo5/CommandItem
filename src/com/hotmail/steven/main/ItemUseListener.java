package com.hotmail.steven.main;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ItemUseListener implements Listener {

	private CommandItem plugin;
	
	public ItemUseListener(CommandItem plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent evt)
	{
		if(evt.getPlayer().getInventory().getItemInMainHand() != null && evt.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR)
		{
			ItemStack handItem = evt.getPlayer().getInventory().getItemInMainHand();
			if(plugin.hasCommandItem(handItem))
			{
				Item item = plugin.getCommandItem(handItem);
				if(item.isOneTime())
				{
					item.getItem().setAmount(1);
					evt.getPlayer().getInventory().removeItem(item.getItem());
				}
		
				item.execute(evt.getPlayer());
				evt.setCancelled(true);
			}
		}
	}
	
}
