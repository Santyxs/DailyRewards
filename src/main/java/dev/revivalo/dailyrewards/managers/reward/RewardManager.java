package dev.revivalo.dailyrewards.managers.reward;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.api.events.AutoClaimEvent;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.reward.actions.AutoClaimAction;
import dev.revivalo.dailyrewards.user.User;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RewardManager {
    private final Set<Reward> rewards;

    public RewardManager() {
        this.rewards = new HashSet<>();
        loadRewards();
    }

    public boolean processAutoClaimForUser(User user) {
        if (!user.hasEnabledAutoClaim()) {
            return false;
        }

        if (user.getAvailableRewards().isEmpty()) {
            return false;
        }

        AutoClaimEvent autoClaimEvent = new AutoClaimEvent(user.getPlayer(), user.getAvailableRewards());
        Bukkit.getPluginManager().callEvent(autoClaimEvent);


        if (autoClaimEvent.isCancelled()) {
            return false;
        }

        Bukkit.getScheduler().runTaskLater(DailyRewardsPlugin.get(), () ->
                new AutoClaimAction()
                        .preCheck(user.getPlayer(), user.getAvailableRewards()), Config.JOIN_AUTO_CLAIM_DELAY.asInt() * 20L);
        return true;

    }

    public void loadRewards() {
        rewards.clear();
        if (Config.DAILY_ENABLED.asBoolean())
            rewards.add(new Reward(RewardType.DAILY,
                    Config.DAILY_AVAILABLE_AFTER_FIRST_JOIN,
                    Config.DAILY_COOLDOWN_FORMAT,
                    Config.DAILY_COOLDOWN,
                    Config.DAILY_COOLDOWN_FORMAT,
                    Config.DAILY_POSITION,
                    Config.DAILY_SOUND,
                    Lang.DAILY_TITLE,
                    Lang.DAILY_SUBTITLE,
                    Lang.DAILY_COLLECTED,
                    Lang.DAILY_PREMIUM_COLLECTED,
                    Config.DAILY_AVAILABLE_ITEM,
                    Config.DAILY_UNAVAILABLE_ITEM,
                    Lang.DAILY_DISPLAY_NAME_AVAILABLE,
                    Lang.DAILY_PREMIUM_DISPLAY_NAME_AVAILABLE,
                    Lang.DAILY_DISPLAY_NAME_UNAVAILABLE,
                    Lang.DAILY_PREMIUM_DISPLAY_NAME_UNAVAILABLE,
                    Lang.DAILY_AVAILABLE_LORE,
                    Lang.DAILY_AVAILABLE_PREMIUM_LORE,
                    Lang.DAILY_UNAVAILABLE_LORE,
                    Lang.DAILY_PREMIUM_UNAVAILABLE_LORE,
                    Config.DAILY_REWARDS,
                    Config.DAILY_PREMIUM_REWARDS));
        if (Config.WEEKLY_ENABLED.asBoolean())
            rewards.add(new Reward(RewardType.WEEKLY,
                    Config.WEEKLY_AVAILABLE_AFTER_FIRST_JOIN,
                    Config.WEEKLY_COOLDOWN_FORMAT,
                    Config.WEEKLY_COOLDOWN,
                    Config.WEEKLY_COOLDOWN_FORMAT,
                    Config.WEEKLY_POSITION,
                    Config.WEEKLY_SOUND,
                    Lang.WEEKLY_TITLE,
                    Lang.WEEKLY_SUBTITLE,
                    Lang.WEEKLY_COLLECTED,
                    Lang.WEEKLY_PREMIUM_COLLECTED,
                    Config.WEEKLY_AVAILABLE_ITEM,
                    Config.WEEKLY_UNAVAILABLE_ITEM,
                    Lang.WEEKLY_DISPLAY_NAME_AVAILABLE,
                    Lang.WEEKLY_PREMIUM_DISPLAY_NAME_AVAILABLE,
                    Lang.WEEKLY_DISPLAY_NAME_UNAVAILABLE,
                    Lang.WEEKLY_PREMIUM_DISPLAY_NAME_UNAVAILABLE,
                    Lang.WEEKLY_AVAILABLE_LORE,
                    Lang.WEEKLY_AVAILABLE_PREMIUM_LORE,
                    Lang.WEEKLY_UNAVAILABLE_LORE,
                    Lang.WEEKLY_PREMIUM_UNAVAILABLE_LORE,
                    Config.WEEKLY_REWARDS,
                    Config.WEEKLY_PREMIUM_REWARDS));
        if (Config.MONTHLY_ENABLED.asBoolean())
            rewards.add(new Reward(
                    RewardType.MONTHLY,
                    Config.MONTHLY_AVAILABLE_AFTER_FIRST_JOIN,
                    Config.MONTHLY_COOLDOWN_FORMAT,
                    Config.MONTHLY_COOLDOWN,
                    Config.MONTHLY_COOLDOWN_FORMAT,
                    Config.MONTHLY_POSITION,
                    Config.MONTHLY_SOUND,
                    Lang.MONTHLY_TITLE,
                    Lang.MONTHLY_SUBTITLE,
                    Lang.MONTHLY_COLLECTED,
                    Lang.MONTHLY_PREMIUM_COLLECTED,
                    Config.MONTHLY_AVAILABLE_ITEM,
                    Config.MONTHLY_UNAVAILABLE_ITEM,
                    Lang.MONTHLY_DISPLAY_NAME_AVAILABLE,
                    Lang.MONTHLY_PREMIUM_DISPLAY_NAME_AVAILABLE,
                    Lang.MONTHLY_DISPLAY_NAME_UNAVAILABLE,
                    Lang.MONTHLY_PREMIUM_DISPLAY_NAME_UNAVAILABLE,
                    Lang.MONTHLY_AVAILABLE_LORE,
                    Lang.MONTHLY_AVAILABLE_PREMIUM_LORE,
                    Lang.MONTHLY_UNAVAILABLE_LORE,
                    Lang.MONTHLY_PREMIUM_UNAVAILABLE_LORE,
                    Config.MONTHLY_REWARDS,
                    Config.MONTHLY_PREMIUM_REWARDS));
    }

    public Optional<Reward> getRewardByType(RewardType rewardType) {
        return rewards.stream().filter(reward -> reward.getRewardType() == rewardType).findFirst();
    }

    public Set<Reward> getRewards() {
        return rewards;
    }
}