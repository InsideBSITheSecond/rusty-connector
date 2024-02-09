package group.aelysium.rustyconnector.plugin.velocity.lib.dynamic_teleport.hub.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.server.ServerInfo;
import group.aelysium.rustyconnector.core.lib.ReplyableCommand;
import group.aelysium.rustyconnector.plugin.velocity.lib.players.Player;
import group.aelysium.rustyconnector.toolkit.velocity.family.scalar_family.IRootFamily;
import group.aelysium.rustyconnector.toolkit.velocity.util.DependencyInjector;
import group.aelysium.rustyconnector.plugin.velocity.PluginLogger;
import group.aelysium.rustyconnector.plugin.velocity.central.Tinder;
import group.aelysium.rustyconnector.plugin.velocity.lib.dynamic_teleport.hub.HubService;
import group.aelysium.rustyconnector.plugin.velocity.lib.family.FamilyService;
import group.aelysium.rustyconnector.plugin.velocity.lib.family.scalar_family.RootFamily;
import group.aelysium.rustyconnector.plugin.velocity.lib.family.Family;
import group.aelysium.rustyconnector.plugin.velocity.lib.lang.ProxyLang;
import group.aelysium.rustyconnector.plugin.velocity.lib.server.MCLoader;
import group.aelysium.rustyconnector.plugin.velocity.lib.server.ServerService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.incendo.cloud.annotations.Permission;

public class CommandHub extends ReplyableCommand {
    @org.incendo.cloud.annotations.Command("hub")
    @Permission("rustyconnector.command.hub")
    public void asvejmuflcgxstne(com.velocitypowered.api.proxy.Player source) {
        try {
            HubService hubService = Tinder.get().services().dynamicTeleport().orElseThrow().services().hub().orElse(null);
            if(hubService == null) {
                reply(source, ProxyLang.UNKNOWN_COMMAND);
                return;
            }

            Player player = Player.from(source);

            Family family = player.server().orElseThrow().family();
            IRootFamily rootFamily = Tinder.get().services().family().rootFamily();

            if (!hubService.isEnabled(family.id())) {
                reply(source, ProxyLang.UNKNOWN_COMMAND);
                return;
            }

            if (!family.metadata().canBeAParentFamily())
                // Attempt to connect to root family if the family isn't allowed to be a parent family.
                try {
                    rootFamily.connect(player);
                    return;
                } catch (RuntimeException err) {
                    Tinder.get().logger().send(Component.text("Failed to connect player to parent family " + rootFamily.id() + "!", NamedTextColor.RED));
                    reply(source, ProxyLang.HUB_CONNECTION_FAILED);
                    return;
                }

            try {
                Family parent = family.parent();

                if (parent != null) {
                    parent.connect(player);
                    return;
                }

                rootFamily.connect(player);
            } catch (RuntimeException err) {
                Tinder.get().logger().send(Component.text("Failed to connect player to parent family " + rootFamily.id() + "!", NamedTextColor.RED));
                reply(source, ProxyLang.HUB_CONNECTION_FAILED);
                return;
            }
        } catch (Exception e) {
            reply(source, ProxyLang.INTERNAL_ERROR);
        }
    }
}