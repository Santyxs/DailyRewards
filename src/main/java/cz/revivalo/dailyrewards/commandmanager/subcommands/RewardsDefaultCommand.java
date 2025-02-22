package cz.revivalo.dailyrewards.commandmanager.subcommands;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.commandmanager.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RewardsDefaultCommand implements SubCommand {
    @Override
    public String getName() {
        return "default";
    }

    @Override
    public String getDescription() {
        return "Opens a menu with rewards";
    }

    @Override
    public String getSyntax() {
        return "/rewards";
    }

    @Override
    public String getPermission() {
        return "dailyreward.use";
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, int index, String[] args) {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        DailyRewards.getMenuManager().openRewardsMenu((Player) sender);
    }
}
