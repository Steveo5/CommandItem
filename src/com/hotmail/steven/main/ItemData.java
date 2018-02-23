package com.hotmail.steven.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ItemData {

	private static CommandItem plugin;
	private static FileConfiguration cfg;
	private static File cfgFile;
	
	/**
	 * Must be called before any config methods work0
	 * @param plugin
	 */
	public static void initialize(CommandItem plugin)
	{
		ItemData.plugin = plugin;
		
		cfgFile = new File(plugin.getDataFolder() + File.separator + "items.yml");
		
		if(!cfgFile.exists())
		{
			try {
				cfgFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		cfg = YamlConfiguration.loadConfiguration(cfgFile);
	}
	
	public static void save()
	{
		try {
			cfg.save(cfgFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void addItem(Item item)
	{
		if(cfg.isConfigurationSection("items." + item.getName()))
		{
			cfg.set("items." + item.getName(), null);
		}
		
		cfg.createSection("items." + item.getName());
		
		ConfigurationSection section = cfg.getConfigurationSection("items." + item.getName());
		section.set("item", item.getItem());
		List<String> commands = new ArrayList<String>();
		// Add command and arg together
		for(String command : item.getCommands())
		{
			String[] parts = command.split("\\|");
			commands.add(parts[0] + "|" + parts[1]);
		}
		section.set("commands", commands);
		section.set("options", item.getOptions());
		
		save();
	}
	
	public static void removeItem(String name)
	{
		if(cfg.isConfigurationSection("items." + name))
		{
			cfg.set("items." + name, null);
			save();
		}
	}
	
	public static List<Item> getCommandItems()
	{
		List<Item> items = new ArrayList<Item>();
		if(cfg.isConfigurationSection("items"))
		{
			for(String itemSection : cfg.getConfigurationSection("items").getValues(false).keySet())
			{
				System.out.println(itemSection);
				ConfigurationSection section = cfg.getConfigurationSection("items." + itemSection);
				List<String> options = new ArrayList<String>();
				if(section.isList("options"))
				{
					options = section.getStringList("options");
				}
				
				Item item = new Item(section.getItemStack("item"), itemSection, options);
				for(String command : section.getStringList("commands"))
				{
					String[] parts = command.split("\\|");
					item.addCommand(parts[0], parts[1]);
				}
				
				items.add(item);
			}
		}
		return items;
	}
	
}
