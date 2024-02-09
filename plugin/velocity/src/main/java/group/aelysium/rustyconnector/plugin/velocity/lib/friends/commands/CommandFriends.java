package group.aelysium.rustyconnector.plugin.velocity.lib.friends.commands;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import group.aelysium.rustyconnector.core.lib.ReplyableCommand;
import group.aelysium.rustyconnector.core.lib.lang.LanguageResolver;
import group.aelysium.rustyconnector.plugin.velocity.central.Tinder;
import group.aelysium.rustyconnector.plugin.velocity.lib.friends.FriendsService;
import group.aelysium.rustyconnector.plugin.velocity.lib.lang.ProxyLang;
import group.aelysium.rustyconnector.plugin.velocity.lib.players.Player;
import group.aelysium.rustyconnector.toolkit.velocity.friends.IFriendRequest;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CommandFriends extends ReplyableCommand {
    @Command("friends")
    @Permission("rustyconnector.command.friends")
    public void wirskzgyamkblpkj(com.velocitypowered.api.proxy.Player source) {
        reply(source, ProxyLang.FRIENDS_BOARD.build(Player.from(source)));
    }

    @Command("friends add <olgjmrvrrzwjzhaf>")
    @Permission("rustyconnector.command.friends")
    public void sqhudomlxcxsehho(com.velocitypowered.api.proxy.Player source, @Argument(value = "olgjmrvrrzwjzhaf") String username) {
        try {
            Player sender = Player.from(source);
            Player target = new Player.UsernameReference(username).get();

            FriendsService service = Tinder.get().services().friends().orElseThrow(() -> new RuntimeException("The friends module isn't enabled!"));

            if (service.areFriends(sender, target)) {
                reply(source, ProxyLang.FRIEND_REQUEST_ALREADY_FRIENDS.build(username));
                return;
            }

            service.sendRequest(sender, username);
        } catch (Exception e) {
            error(source, "There was an internal error sending a friend request to that player!");
        }
    }
    @Suggestions("olgjmrvrrzwjzhaf")
    public List<String> olgjmrvrrzwjzhaf(com.velocitypowered.api.proxy.Player source) {
        List<String> output = new ArrayList<>();
        try {
            RegisteredServer server = source.getCurrentServer().orElseThrow().getServer();

            server.getPlayersConnected().forEach(localPlayer -> {
                if(localPlayer.equals(source)) return;

                output.add(localPlayer.getUsername());
            });
        } catch (Exception ignored) {
            output.clear();
        }

        return output;
    }

    private Optional<IFriendRequest> getFriendRequest(Player source, Player remote) {
        if (source == null) return Optional.empty();
        if (remote == null) return Optional.empty();

        FriendsService service = Tinder.get().services().friends().orElseThrow(() -> new RuntimeException("The friends module isn't enabled!"));

        return service.findRequest(source, remote);
    }

    @Command("friends requests <xxhcehvgrwbnxzsp> accept")
    @Permission("rustyconnector.command.friends")
    public void cfpezweijfdtejhi(com.velocitypowered.api.proxy.Player source, @Argument(value = "xxhcehvgrwbnxzsp") String username) {
        try {
            Player remotePlayer = new Player.UsernameReference(username).get();
            Player player = Player.from(source);

            if (remotePlayer == null) {
                error(source, Tinder.get().lang().resolver().getRaw("core.no_player", LanguageResolver.tagHandler("username", username)));
                return;
            }

            IFriendRequest request = getFriendRequest(player, remotePlayer).orElse(null);
            if(request == null) {
                error(source, "You don't have any friend requests from that player!");
                return;
            }

            request.accept();
        } catch (IllegalStateException e) {
            error(source, e.getMessage());
        } catch (Exception e) {
            error(source, "There was an internal error sending a friend request to that player!");
        }
    }
    @Command("friends requests <xxhcehvgrwbnxzsp> ignore")
    @Permission("rustyconnector.command.friends")
    public void ghyneamrflwffmkg(com.velocitypowered.api.proxy.Player source, @Argument(value = "xxhcehvgrwbnxzsp") String username) {
        try {
            Player remotePlayer = new Player.UsernameReference(username).get();
            Player player = Player.from(source);

            if (remotePlayer == null) {
                error(source, Tinder.get().lang().resolver().getRaw("core.no_player", LanguageResolver.tagHandler("username", username)));
                return;
            }

            IFriendRequest request = getFriendRequest(player, remotePlayer).orElse(null);
            if(request == null) {
                error(source, "You don't have any friend requests from that player!");
                return;
            }

            try {
                request.ignore();
            } catch (Exception ignore) {
                FriendsService service = Tinder.get().services().friends().orElseThrow(() -> new RuntimeException("The friends module isn't enabled!"));
                service.closeInvite(request);
            }

            reply(source, ProxyLang.FRIEND_REQUEST_IGNORE.build(username));
        } catch (Exception e) {
            error(source, "There was an issue while ignoring that player!");
        }
    }
    @Suggestions("xxhcehvgrwbnxzsp")
    public List<String> xxhcehvgrwbnxzsp(com.velocitypowered.api.proxy.Player source) {
        List<String> output = new ArrayList<>();
        try {
            Player player = Player.from(source);

            FriendsService service = Tinder.get().services().friends().orElseThrow();
            List<IFriendRequest> requests = service.findRequestsToTarget(player);

            if(requests.size() == 0) {
                output.add("You have no pending friend requests!");
                return output;
            }

            requests.forEach(invite -> {
                output.add(invite.sender().username());
            });
        } catch (Exception ignored) {
            output.clear();
            output.add("Unable to fetch your friend requests!");
        }

        return output;
    }
}