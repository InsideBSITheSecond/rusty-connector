package group.aelysium.rustyconnector.plugin.velocity.lib.friends.commands;

import group.aelysium.rustyconnector.core.lib.ReplyableCommand;
import group.aelysium.rustyconnector.core.lib.lang.LanguageResolver;
import group.aelysium.rustyconnector.plugin.velocity.central.Tinder;
import group.aelysium.rustyconnector.plugin.velocity.lib.friends.FriendsService;
import group.aelysium.rustyconnector.plugin.velocity.lib.lang.ProxyLang;
import group.aelysium.rustyconnector.plugin.velocity.lib.players.Player;
import group.aelysium.rustyconnector.toolkit.velocity.player.IPlayer;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;

import java.util.ArrayList;
import java.util.List;

public final class CommandUnFriend extends ReplyableCommand {
    @Command("unfriend")
    @Permission("rustyconnector.command.unfriend")
    public void ruwxevdgqannsqbm(com.velocitypowered.api.proxy.Player source) {
        reply(source, ProxyLang.USAGE+": /unfriend <username>");
    }

    @Command("unfriend <username>")
    @Permission("rustyconnector.command.unfriend")
    public void wgqubtagkfabwjgp(com.velocitypowered.api.proxy.Player source, @Argument(value = "username") String username) {
        try {
            Tinder api = Tinder.get();

            if(source.getUsername().equals(username)) {
                error(source, "Are you a dummy? You can't unfriend yourself! (You were never even so much as your own friend to begin with)"); // lulz
                return;
            }

            Player target = new IPlayer.UsernameReference(username).get();
            Player sender = Player.from(source);

            FriendsService service = Tinder.get().services().friends().orElse(null);
            if(service == null) {
                error(source, "The friends module is not enabled!");
                return;
            }

            if (!service.areFriends(target, sender)) {
                reply(source, api.lang().resolver().get("proxy.friends.unfriend.not_friends", LanguageResolver.tagHandler("username", username)));
                return;
            }

            service.removeFriends(sender, target);

            reply(source, api.lang().resolver().get("proxy.friends.unfriend.success", LanguageResolver.tagHandler("username", username)));
        } catch (Exception e) {
            error(source, e.getMessage());
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