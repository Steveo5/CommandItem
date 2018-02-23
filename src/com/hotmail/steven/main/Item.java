package com.hotmail.steven.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.badbones69.blockparticles.api.BlockParticles;
import me.badbones69.blockparticles.api.Particles;
import net.md_5.bungee.api.ChatColor;

public class Item {

	private String name;
	private ItemStack item;
	private List<String> commands;
	private boolean oneTime = true;
	private List<String> options;
	
	public Item(ItemStack item, String name, List<String> options)
	{
		this.name = name;
		this.item = item.clone();
		this.item.setAmount(1);
		commands = new ArrayList<String>();
		this.options = new ArrayList<String>(options);
	}
	
	/**
	 * Get the name/id of this item
	 * @return
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Gets the item that executes this command
	 * item
	 * @return
	 */
	public ItemStack getItem()
	{
		return item;
	}
	
	public List<String> getCommands()
	{
		return commands;
	}
	
	/**
	 * 
	 * @param command
	 * @param arg valid args are -p as player, -o as op, -c as console
	 */
	protected void addCommand(String command, String arg)
	{
		commands.add(command + "|" + arg);
	}
	
	/**
	 * Will this item be automatically removed after using
	 * @return
	 */
	public boolean isOneTime()
	{
		return oneTime;
	}
	
	/**
	 * Executes the command for a specific player
	 * Replaces all %player% variables
	 * @param player
	 */
	public void execute(Player player)
	{
		for(String command : commands)
		{
			String[] parts = command.split("\\|");
			if(parts[1].equals("-p"))
			{
				try
				{
				Bukkit.getServer().dispatchCommand(player, parts[0]);
				} catch(Exception e) {}
			} else if(parts[1].equals("-c"))
			{
				// Send the command as console
				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
				Bukkit.dispatchCommand(console, parts[0].replaceAll("%player%", player.getName()));
			} else if(parts[1].equals("-o"))
			{
				player.setOp(true);
				try
				{
				Bukkit.getServer().dispatchCommand(player, parts[0]);
				} catch(Exception e) {}
				player.setOp(false);
			}
		}
		
		if(hasOption("title"))
		{
			player.sendTitle("", StringUtil.color(getOption("title")), 10, 70, 20);
		}
		
		if(hasOption("sound"))
		{
			player.getWorld().playSound(player.getLocation(), Sound.valueOf(getOption("sound")), 10, 10);
		}
		
		if(hasOption("particle"))
		{
			BlockParticles.getInstance().setParticle(Particles.valueOf(getOption("particle")), player.getLocation().add(0, 1, 0), player.getName());
			new BukkitRunnable()
			{

				@Override
				public void run() {
					
					BlockParticles.getInstance().removeParticle(player.getName());
					
				}
				
			}.runTaskLater(CommandItem.instance(), 40L);
		}
	}
	
	/**
	 * Get the options this item has, such as sound when clicked.
	 * Particle etc
	 * 
	 * Options format are in string pairs type|value
	 * @return
	 */
	public List<String> getOptions()
	{ 
		return options;
	}
	
	/**
	 * Gets a specific option
	 * @param name
	 * @return
	 */
	public String getOption(String name)
	{
		for(String option : options)
		{
			String[] parts = option.split("\\|");
			if(parts[0].equalsIgnoreCase(name)) return parts[1];
		}
		
		return "";
	}
	
	/**
	 * Check if an option exists
	 * @param name
	 * @return
	 */
	public boolean hasOption(String name)
	{
		for(String option : options)
		{
			String[] parts = option.split("\\|");
			if(parts[0].equalsIgnoreCase(name)) return true;
		}
		
		return false;
	}
	
	/**
	 * Add an option to this item. String must be in format type|value
	 * @param name
	 * @param value
	 */
	public void addOption(String name, String value)
	{
		Iterator<String> optionItr = options.iterator();
		// Remove any existing entries of the current option
		while(optionItr.hasNext())
		{
			String next = optionItr.next();
			String[] parts = next.split("\\|");
			if(parts[0].equalsIgnoreCase(name)) optionItr.remove();
		}
		
		options.add(name + "|" + value);
		
		ItemData.addItem(this);
	}
	
}
