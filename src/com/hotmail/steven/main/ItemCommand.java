package com.hotmail.steven.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.badbones69.blockparticles.api.BlockParticles;
import me.badbones69.blockparticles.api.Particles;
import net.md_5.bungee.api.ChatColor;

public class ItemCommand implements CommandExecutor {

	private CommandItem plugin;
	
	public ItemCommand(CommandItem plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length > 0 && !args[0].equals("help"))
		{
			String command = args[0];
			
			if(command.equalsIgnoreCase("list"))
			{
				List<Item> commandItems = plugin.getCommandItems();
				StringBuilder builder = new StringBuilder();
				builder.append("-= &a&lShowing &e&l" + commandItems.size() + " &a&litems -=");
				for(Item item : commandItems)
				{
					builder.append("\n&3- " + item.getName());
				}
				sender.sendMessage(StringUtil.color(builder.toString()));
			} else if(command.equalsIgnoreCase("create"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					ItemStack handItem = p.getInventory().getItemInMainHand();
					// Check if the player is holding an actual item
					if(handItem == null || handItem.getType() == Material.AIR)
					{
						p.sendMessage(ChatColor.RED + "You must be holding an item!");
					} else
					{
						if(args.length > 1)
						{
							if(plugin.hasCommandItem(args[1].toLowerCase()))
							{
								p.sendMessage(ChatColor.RED + "Command item already exists!");
							} else
							{
								List<String> options = new ArrayList<String>();
								// Get the options from command string
								for(int i=2;i<args.length;i+=2)
								{
									try
									{
										String option = args[i];
										String value = args[i+1];
										
										if(option.equalsIgnoreCase("-p"))
										{
											Particles.valueOf(value.toUpperCase());
											options.add("particle" + "|" + value.toUpperCase());
										} else if(option.equalsIgnoreCase("-s"))
										{
											Sound sound = Sound.valueOf(value.toUpperCase());
											options.add("sound" + "|" + value.toUpperCase());
										}
									} catch(Exception e)
									{
										sender.sendMessage(ChatColor.RED + "Entered invalid option");
									}
								}
								Item item = new Item(handItem, args[1].toLowerCase(), options);
								plugin.addCommandItem(item);
								p.sendMessage(StringUtil.color("&aCommand item created successfully"));
							}
						} else
						{
							p.sendMessage(ChatColor.RED + "You must enter a name!");
						}
					}
				} else
				{
					sender.sendMessage(ChatColor.RED + "Only players can execute this command");
				}
			} else if(command.equalsIgnoreCase("addcmd"))
			{
				if(args.length > 3)
				{
					String name = args[1];
					String arg = args[2];
					String stringCommand = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
					
					if(!arg.equals("-p") && !arg.equals("-o") && !arg.equals("-c"))
					{
						sender.sendMessage(ChatColor.RED + "Invalid argument use -c, -o or -p");
					} else if(!plugin.hasCommandItem(name))
					{
						sender.sendMessage(ChatColor.RED + "Unknown command item");
					} else
					{
						Item item = plugin.getCommandItem(name);
						item.addCommand(stringCommand, arg);
						sender.sendMessage(StringUtil.color("&aCommand item updated with new command"));
						ItemData.addItem(item);
					}
					
				} else
				{
					sender.sendMessage(ChatColor.RED + "/citem addcommand <name> <-c|-o|-p> <command>");
				}
			} else if(command.equalsIgnoreCase("remove"))
			{
				if(args.length > 1)
				{
					if(plugin.hasCommandItem(args[1]))
					{
						plugin.removeCommandItem(args[1]);
						sender.sendMessage(StringUtil.color("&aCommand item removed"));
					} else
					{
						sender.sendMessage(ChatColor.RED + "Unknown command item");
					}
				} else
				{
					sender.sendMessage(ChatColor.RED + "You must enter a name!");
				}
			} else if(command.equalsIgnoreCase("give"))
			{
				if(args.length > 1)
				{
					Player receiver = null;
					if(args.length > 2)
					{
						receiver = Bukkit.getPlayer(args[1]);
						if(receiver == null)
						{
							sender.sendMessage(ChatColor.RED + "You have entered an invalid name");
							return true;
						}
					} else
					{
						if(!(sender instanceof Player))
						{
							sender.sendMessage(ChatColor.RED + "Only players can execute this command");
						} else
						{
							receiver = (Player)sender;
						}
					}
					
					if(args.length > 2)
					{
						if(plugin.hasCommandItem(args[2]))
						{
							Item item = plugin.getCommandItem(args[2]);
							int amount = 1;
							
							if(args.length > 3)
							{
								try
								{
									amount = Integer.parseInt(args[3]);
								} catch(NumberFormatException e)
								{
									sender.sendMessage(ChatColor.RED + "You have entered an invalid amount");
								}
							}
							
							ItemStack give = item.getItem().clone();
							give.setAmount(amount);
							receiver.getInventory().addItem(give);
						} else
						{
							sender.sendMessage(ChatColor.RED + "You have entered an invalid name");
						}
					} else
					{
						sender.sendMessage(ChatColor.RED + "You must enter a item name");
					}
				} else
				{
					sender.sendMessage(ChatColor.RED + "You must enter a name!");
				}
			} else if(command.equalsIgnoreCase("addtitle"))
			{
				if(plugin.hasCommandItem(args[1]))
				{
					Item item = plugin.getCommandItem(args[1]);
					item.addOption("title", String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
					sender.sendMessage(StringUtil.color("&aItem will now display a title"));
				} else
				{
					sender.sendMessage(ChatColor.RED + "You have entered an invalid name");
				}
			} else if(command.equalsIgnoreCase("particles"))
			{
				for(Particles particle : Particles.values())
				{
					sender.sendMessage(particle.name());
				}
			}
		} else
		{
			StringBuilder commands = new StringBuilder();
			commands.append("&3&l-= Command Item =-");
			commands.append("\n&a/citem list - &elist all command items");
			commands.append("\n&a/citem create <name> [-p <particle>] [-s <sound>] - &ecreate a command item from your hand item");
			commands.append("\n&a/citem addtitle <name> <title> - &eAdds a title when used");
			commands.append("\n&a/citem addcmd <name> <-c|-o|-p> <command> - &eadds a command to a command item, commands are run in order as op");
			//commands.append("\n&a/citem setoption <name> [-p <particle>]|[-s <sound>] - &eset an option for this item");
			commands.append("\n&a/citem particles - &elist all particle types");
			commands.append("\n&a/citem remove <name> - &eremove a command item");
			commands.append("\n&a/citem give <name> <player> [<amount>] - &egives a player some command items");
			
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', commands.toString()));
		}
		
		return true;
	}

}
