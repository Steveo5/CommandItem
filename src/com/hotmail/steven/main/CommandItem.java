package com.hotmail.steven.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandItem extends JavaPlugin {

	private List<Item> commandItems;
	private static CommandItem plugin;
	
	@Override
	public void onEnable()
	{
		plugin = this;
		this.getCommand("commanditem").setExecutor(new ItemCommand(this));
		this.getServer().getPluginManager().registerEvents(new ItemUseListener(this), this);
		
		commandItems = new ArrayList<Item>();
		ItemData.initialize(this);
		
		this.saveDefaultConfig();
		loadCommandItems();
	}
	
	public static CommandItem instance()
	{
		return plugin;
	}
	
	/**
	 * Get all registered command items
	 * @return
	 */
	public List<Item> getCommandItems()
	{
		return commandItems;
	}
	
	/**
	 * Get a command item
	 * @param name
	 * @return null if no known item exists with that name
	 */
	public Item getCommandItem(String name)
	{
		for(Item item : commandItems)
		{
			if(item.getName().equals(name)) return item;
		}
		
		return null;
	}
	
	/**
	 * Get a commanditem from its itemstack counterpart
	 * @param itemStack
	 * @return null if it doesnt exist
	 */
	public Item getCommandItem(ItemStack itemStack)
	{
		for(Item item : commandItems)
		{
			if(itemStack.isSimilar(item.getItem())) return item;
		}
		
		return null;
	}
	
	/**
	 * Checks if a command item exists
	 * @param name
	 * @return
	 */
	public boolean hasCommandItem(String name)
	{
		for(Item item : commandItems)
		{
			if(item.getName().equals(name)) return true;
		}
		
		return false;
	}
	
	/**
	 * Get whether a commanditem exists with the given itemstack.
	 * Doesn't take into account item amounts
	 * @param item
	 * @return
	 */
	public boolean hasCommandItem(ItemStack itemStack)
	{
		for(Item item : commandItems)
		{
			if(itemStack.isSimilar(item.getItem())) return true;
		}
		
		return false;
	}
	
	/**
	 * Adds a command item to the config and reloads
	 * the command items list
	 * @param item
	 */
	public void addCommandItem(Item item)
	{
		ItemData.addItem(item);
		loadCommandItems();
	}
	
	public void removeCommandItem(String name)
	{
		ItemData.removeItem(name);
		loadCommandItems();
	}
	
	/**
	 * Loads all commanditems from config into an array
	 */
	public void loadCommandItems()
	{
		commandItems.clear();
		commandItems = ItemData.getCommandItems();
	}
	
}
