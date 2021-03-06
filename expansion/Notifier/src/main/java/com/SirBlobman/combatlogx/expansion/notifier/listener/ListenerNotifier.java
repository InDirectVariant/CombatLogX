package com.SirBlobman.combatlogx.expansion.notifier.listener;

import com.SirBlobman.combatlogx.api.event.PlayerCombatTimerChangeEvent;
import com.SirBlobman.combatlogx.api.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.api.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.expansion.notifier.Notifier;
import com.SirBlobman.combatlogx.expansion.notifier.utility.ActionBarManager;
import com.SirBlobman.combatlogx.expansion.notifier.utility.BossBarManager;
import com.SirBlobman.combatlogx.expansion.notifier.utility.MVdWHandler;
import com.SirBlobman.combatlogx.expansion.notifier.utility.TitleManagerHandler;
import com.SirBlobman.combatlogx.expansion.notifier.utility.scoreboard.ScoreboardHandler;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ListenerNotifier implements Listener {
    private final Notifier expansion;
    public ListenerNotifier(Notifier expansion) {
        this.expansion = expansion;
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onTimerChange(PlayerCombatTimerChangeEvent e) {
        Player player = e.getPlayer();
        if(!ScoreboardHandler.isDisabled(player)) ScoreboardHandler.updateScoreboard(this.expansion, player);
        if(!ActionBarManager.isDisabled(player)) ActionBarManager.updateActionBar(this.expansion, player);
        if(!BossBarManager.isDisabled(player)) BossBarManager.updateBossBar(this.expansion, player);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();

        FileConfiguration config = this.expansion.getConfig("mvdw.yml");
        if(config.getBoolean("FeatherBoard.enabled")) {
            String trigger = config.getString("FeatherBoard.trigger");
            MVdWHandler.enableTrigger("FeatherBoard", trigger, player);
        }

        if(config.getBoolean("AnimatedNames.enabled")) {
            String trigger = config.getString("AnimatedNames.trigger");
            MVdWHandler.enableTrigger("AnimatedNames", trigger, player);
        }

        TitleManagerHandler.disableScoreboard(this.expansion, player);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onUntag(PlayerUntagEvent e) {
        Player player = e.getPlayer();
        Runnable task = () -> {
            if(!ScoreboardHandler.isDisabled(player)) ScoreboardHandler.disableScoreboard(this.expansion, player);
            if(!ActionBarManager.isDisabled(player)) ActionBarManager.removeActionBar(this.expansion, player);
            if(!BossBarManager.isDisabled(player)) BossBarManager.removeBossBar(this.expansion, player, false);

            FileConfiguration config = this.expansion.getConfig("mvdw.yml");
            if(config.getBoolean("FeatherBoard.enabled")) {
                String trigger = config.getString("FeatherBoard.trigger");
                MVdWHandler.disableTrigger("FeatherBoard", trigger, player);
            }

            if(config.getBoolean("AnimatedNames.enabled")) {
                String trigger = config.getString("AnimatedNames.trigger");
                MVdWHandler.disableTrigger("AnimatedNames", trigger, player);
            }

            TitleManagerHandler.restoreScoreboard(this.expansion, player);
        };
        Bukkit.getScheduler().runTaskLater(this.expansion.getPlugin().getPlugin(), task, 1L);
    }
}