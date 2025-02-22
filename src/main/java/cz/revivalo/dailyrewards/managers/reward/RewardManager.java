package cz.revivalo.dailyrewards.managers.reward;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.configuration.data.DataManager;
import cz.revivalo.dailyrewards.configuration.enums.Config;
import cz.revivalo.dailyrewards.configuration.enums.Lang;
import cz.revivalo.dailyrewards.managers.cooldown.Cooldown;
import cz.revivalo.dailyrewards.managers.cooldown.CooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.stream.Collectors;

public class RewardManager {

	public void autoClaim(final Player player, Collection<RewardType> rewardTypes) {
		if (Config.CHECK_FOR_FULL_INVENTORY.asBoolean() && player.getInventory().firstEmpty() == -1) {
			player.sendMessage(Lang.FULL_INVENTORY_MESSAGE.asColoredString());
			return;
		}
		final String formattedRewards = rewardTypes.stream()
				.map(this::getRewardsPlaceholder)
				.collect(Collectors.joining(", "));

		rewardTypes.forEach(rewardType -> this.claim(player, rewardType, false, false));
		Lang.sendListToPlayer(player, Lang.AUTO_CLAIMED_NOTIFICATION
				.asReplacedList(new HashMap<String, String>() {{put("%rewards%", String.format(formattedRewards));}}));
	}

	@SuppressWarnings("deprecation")
	public void claim(final Player player, RewardType type, boolean fromCommand, boolean announce) {
		if (!type.isEnabled()){
			player.sendMessage(Lang.DISABLED_REWARD.asColoredString());
			return;
		}
		if (!player.hasPermission("dailyreward." + type)) {
			if (!fromCommand) return;
			player.sendMessage(Lang.PERMISSION_MESSAGE.asColoredString());
			return;
		}
		if (Config.CHECK_FOR_FULL_INVENTORY.asBoolean() && player.getInventory().firstEmpty() == -1) {
			player.sendMessage(Lang.FULL_INVENTORY_MESSAGE.asColoredString());
			return;
		}

		final Cooldown cooldown = CooldownManager.getCooldown(player, type);
		if (cooldown.isClaimable()) {
			final String typeName = type.toString().toUpperCase();
			final Collection<String> rewardCommands = Config.valueOf(String.format("%s%s_REWARDS", typeName, DailyRewards.isPremium(player, type)))
					.asReplacedList(new HashMap<String, String>(){{put("%player%", player.getName());}});

			if (rewardCommands.size() == 0) {
				player.sendMessage(Lang.REWARDS_IS_NOT_SET.asColoredString());
			} else {
				final ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();
				rewardCommands.forEach(command -> Bukkit.dispatchCommand(consoleSender, command));
			}

			CooldownManager.setCooldown(player, type);
			if (announce) {
				player.playSound(player.getLocation(), Sound.valueOf(Config.valueOf(
						String.format("%s_SOUND", typeName)).asUppercase()), 1F, 1F);

				player.sendTitle(
						Lang.valueOf(String.format("%s_TITLE", typeName)).asColoredString(),
						Lang.valueOf(String.format("%s_SUBTITLE", typeName)).asColoredString());

				if (Config.ANNOUNCE_ENABLED.asBoolean()) {
					Bukkit.broadcastMessage(Lang.valueOf(String.format("%s%s_COLLECTED", typeName, DailyRewards.isPremium(player, type)))
							.asPlaceholderReplacedText(player)
							.replace("%player%", player.getName()));
				}
			}
			if (!fromCommand) player.closeInventory();

		} else {
			if (fromCommand) {
				player.sendMessage(Lang.COOLDOWN_MESSAGE.asColoredString()
						.replace("%type%", getRewardsPlaceholder(type))
						.replace("%time%", cooldown.getFormat(type.getCooldownFormat())));
				return;
			}
			player.playSound(player.getLocation(),
					Sound.valueOf(Config.UNAVAILABLE_REWARD_SOUND
							.asString()
							.toUpperCase(Locale.ENGLISH)), 1F, 1F);
		}
	}

	public String resetPlayer(final OfflinePlayer player, String type) {
		if (!player.isOnline() && !player.hasPlayedBefore()) return Lang.UNAVAILABLE_PLAYER.asColoredString();
		if (type.equalsIgnoreCase("all")) {
			DataManager.setValues(player.getUniqueId(),
					new HashMap<String, Object>(){{
						put(RewardType.DAILY.toString(), 0L);
						put(RewardType.WEEKLY.toString(), 0L);
						put(RewardType.MONTHLY.toString(), 0L);
					}}
			);//RewardType.DAILY, 0L, RewardType.WEEKLY, 0L, RewardType.MONTHLY, 0L);
		} else {
			try {
				RewardType rewardType = RewardType.valueOf(type.toUpperCase(Locale.ENGLISH));
				DataManager.setValues(player.getUniqueId(), new HashMap<String, Object>() {{
					put(type, 0L);
				}});
			} catch (IllegalArgumentException ex) {
				return Lang.INCOMPLETE_REWARD_RESET.asColoredString();
			}
		}

		return Lang.REWARD_RESET.asColoredString().replace("%type%", type).replace("%player%", player.getName());
	}

	private String getRewardsPlaceholder(final RewardType reward) {
		switch (reward) {
			case DAILY:
				return Config.DAILY_PLACEHOLDER.asString();
			case WEEKLY:
				return Config.WEEKLY_PLACEHOLDER.asString();
		}
		return Config.MONTHLY_PLACEHOLDER.asString();
	}
}
