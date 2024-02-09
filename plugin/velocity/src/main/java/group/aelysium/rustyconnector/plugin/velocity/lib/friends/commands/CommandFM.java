package group.aelysium.rustyconnector.plugin.velocity.lib.friends.commands;

import group.aelysium.rustyconnector.core.lib.ReplyableCommand;
import group.aelysium.rustyconnector.plugin.velocity.central.Tinder;
import group.aelysium.rustyconnector.plugin.velocity.lib.friends.FriendsService;
import group.aelysium.rustyconnector.plugin.velocity.lib.lang.ProxyLang;
import group.aelysium.rustyconnector.plugin.velocity.lib.players.Player;
import group.aelysium.rustyconnector.toolkit.velocity.player.IPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;

import java.util.ArrayList;
import java.util.List;

public final class CommandFM extends ReplyableCommand {
    @Command("fm")
    @Command("fm <username>")
    @Permission("rustyconnector.command.fm")
    public void rmefahhsobbrqxrg(com.velocitypowered.api.proxy.Player player) {
        reply(player, ProxyLang.USAGE+": /fm <username> <message>");
    }
    @Command("fm <username> <message>")
    @Permission("rustyconnector.command.fm")
    public void etwoeerdssfpdpem(com.velocitypowered.api.proxy.Player source, @Argument(value = "username") String username, @Argument(value = "message") String message) {
        try {
            Tinder api = Tinder.get();

            if(source.getUsername().equals(username)) {
                error(source, api.lang().resolver().getRaw("proxy.friends.messaging.no_self_messaging"));
                return;
            }

            Player target = new IPlayer.UsernameReference(username).get();
            Player sender = Player.from(source);

            if (!target.online()) {
                error(source, "The player you're trying to message isn't online!");
                return;
            }

            FriendsService friendsService = api.services().friends().orElseThrow(() -> new RuntimeException("The friends module isn't enabled!"));

            if (!friendsService.areFriends(target, sender)) {
                reply(source, api.lang().resolver().get("proxy.friends.messaging.only_friends"));
                return;
            }

            sender.sendMessage(Component.text("[you -> " + target.username() + "]: " + message, NamedTextColor.GRAY));
            target.sendMessage(Component.text("[" + sender.username() + " -> you]: " + message, NamedTextColor.GRAY).hoverEvent(HoverEvent.showText(ProxyLang.FRIEND_MESSAGING_REPLY)).clickEvent(ClickEvent.suggestCommand("/fm " + sender.username() + " ")));
        } catch (Exception e) {
            error(source, "There was an issue sending that message!");
        }
    }

    @Suggestions("username")
    public Iterable<String> cptjpebkbdkpmogo(com.velocitypowered.api.proxy.Player source) {
        List<String> output = new ArrayList<>();
        try {
            Tinder api = Tinder.get();

            Player player = new Player.Reference(source.getUniqueId()).get();

            FriendsService service = api.services().friends().orElseThrow(() -> new RuntimeException("The friends module isn't enabled!"));

            List<IPlayer> friends = service.findFriends(player).orElseThrow(() -> new RuntimeException(player.username()+" has no friends!"));

            friends.forEach(friend -> {
                try {
                    output.add(friend.username());
                } catch (Exception ignore) {}
            });
        } catch (Exception e) {
            output.clear();
            output.add("There was an issue finding your friends!");
        }
        return output;
    }
}