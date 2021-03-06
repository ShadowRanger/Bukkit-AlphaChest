package net.sradonia.bukkit.alphachest.commands;

import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import net.sradonia.bukkit.alphachest.AlphaChest;
import net.sradonia.bukkit.alphachest.core.Teller.Type;
import net.sradonia.bukkit.alphachest.utils.BukkitUtil;

public class ChestCommand implements CommandExecutor {

    private final AlphaChest plugin;

    public ChestCommand(AlphaChest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("chest")) {
            // Make sure the sender is a player
            if (!(sender instanceof Player)) {
                plugin.getTeller().tell(sender, Type.ERROR, "Only players are able to open chests.");
                return true;
            }

            Player player = (Player) sender;

            // Prevent opening of the chest in Creative Mode
            if (player.getGameMode() == GameMode.CREATIVE && !player.hasPermission("alphachest.chest.creativeMode")) {
                plugin.getTeller().tell(sender, Type.ERROR, "You are not allowed to open your chest in Creative Mode!");
                return true;
            }

            switch (args.length) {
                case 0:
                    // Open the player's own chest
                    if (player.hasPermission("alphachest.chest")) {
                        Inventory chest = plugin.getChestManager().getChest(player.getUniqueId());
                        player.openInventory(chest);
                    } else {
                        plugin.getTeller().tell(sender, Type.ERROR, "You are not allowed to use this command.");
                    }

                    return true;
                case 1:
                    // Open someone else's chest
                    if (player.hasPermission("alphachest.admin")) {
                        OfflinePlayer target = BukkitUtil.getOfflinePlayer(args[0]);

                        if (target == null) {
                            plugin.getTeller().tell(player, Type.ERROR, String.format("Chest for %s not found", args[0]));
                        } else {
                            Inventory chest = plugin.getChestManager().getChest(target.getUniqueId());
                            player.openInventory(chest);
                        }
                    } else {
                        plugin.getTeller().tell(player, Type.ERROR, "You are not allowed to open other user's chests.");
                    }

                    return true;
            }

            return false;
        }

        return false;
    }
}
