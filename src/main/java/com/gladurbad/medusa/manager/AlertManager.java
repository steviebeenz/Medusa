package com.gladurbad.medusa.manager;

import com.gladurbad.medusa.Config;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.playerdata.PlayerData;
import com.gladurbad.medusa.util.ChatUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

public class AlertManager {

    public static boolean verbose(PlayerData data, Check check) {
        TextComponent alertMessage = new TextComponent(ChatUtil.color("&8[&2Medusa&8] &a%player% &2- &a%check% (Type %type%) %dev% &8[&cVL: %vl%&8]")
                .replace("%player%", data.getPlayer().getName())
                .replace("%check%", check.getCheckInfo().name())
                .replace("%type%", check.getCheckInfo().type())
                .replace("%dev%", check.getCheckInfo().dev() ? ChatUtil.color("&c(Dev)") : "")
                .replace("%vl%", "" + check.getVl()));
        alertMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + data.getPlayer().getName()));
        alertMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtil.color("&cClick to teleport.")).create()));

        if(check.getVl() > Config.VL_TO_ALERT) {
            ChatUtil.sendVerbose(alertMessage);
        }

        if(check.getVl() > check.getMaxVL() && !Config.TESTMODE && data.getPlayer().isOnline()) {
            if(!check.getPunishCommand().isEmpty()) {
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), check.getPunishCommand()
                        .replace("%player%", data.getPlayer().getName())
                        .replace("%prefix%", Config.PREFIX));
            }
        } //broken rn.

        return true;
    }
}
