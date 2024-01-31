package group.aelysium.rustyconnector.plugin.velocity.central;

import com.velocitypowered.api.proxy.ConsoleCommandSource;
import group.aelysium.rustyconnector.core.lib.ReplyableCommand;
import group.aelysium.rustyconnector.core.lib.cache.CacheableMessage;
import group.aelysium.rustyconnector.plugin.velocity.lib.family.Family;
import group.aelysium.rustyconnector.plugin.velocity.lib.family.ranked_family.RankedFamily;
import group.aelysium.rustyconnector.plugin.velocity.lib.players.Player;
import group.aelysium.rustyconnector.toolkit.velocity.connection.ConnectionResult;
import group.aelysium.rustyconnector.toolkit.velocity.connection.PlayerConnectable;
import group.aelysium.rustyconnector.toolkit.velocity.player.IPlayer;
import group.aelysium.rustyconnector.plugin.velocity.lib.family.scalar_family.ScalarFamily;
import group.aelysium.rustyconnector.plugin.velocity.lib.family.static_family.StaticFamily;
import group.aelysium.rustyconnector.plugin.velocity.lib.lang.ProxyLang;
import group.aelysium.rustyconnector.plugin.velocity.lib.server.MCLoader;
import group.aelysium.rustyconnector.core.lib.cache.MessageCacheService;
import net.kyori.adventure.text.Component;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class CommandRusty extends ReplyableCommand {
    @Command("rc")
    public void hizfafjjszjivcys(ConsoleCommandSource sender) {
        reply(sender, ProxyLang.RC_ROOT_USAGE);
    }
    @Command("rc debug")
    public void stiuzzsqhudcamko(ConsoleCommandSource sender) {
        try {
            Flame flame = Tinder.get().flame();

            flame.bootLog().forEach(sender::sendMessage);
        } catch (Exception e) {
            error(sender, "There was an issue fetching the debug log!");
        }
    }
    @Command("rc reload")
    public void nglbwcmuvchdjaon(ConsoleCommandSource sender) {
        try {
            reply(sender, "Reloading the proxy...");
            Tinder.get().rekindle();
            reply(sender, "Done reloading!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Command("rc message")
    public void yckarhhyoblbmbdl(ConsoleCommandSource sender) {
        reply(sender, ProxyLang.RC_MESSAGE_ROOT_USAGE);
    }

    @Command("rc message list")
    public void pfnommtocuemordk(ConsoleCommandSource sender) {
        try {
            Flame flame = Tinder.get().flame();
            MessageCacheService messageCacheService = flame.services().messageCache();

            if(messageCacheService.size() > 10) {
                int numberOfPages = Math.floorDiv(messageCacheService.size(),10) + 1;

                List<CacheableMessage> messagesPage = messageCacheService.fetchMessagesPage(1);

                reply(sender, ProxyLang.RC_MESSAGE_PAGE.build(messagesPage, 1, numberOfPages));

                return;
            }

            List<CacheableMessage> messages = messageCacheService.messages();

            reply(sender, ProxyLang.RC_MESSAGE_PAGE.build(messages, 1, 1));
        } catch (Exception e) {
            error(sender, "There was an issue getting those messages!\n"+e.getMessage());
        }
    }

    @Command("rc message list <page>")
    public void evyfrpitotgxtbmf(ConsoleCommandSource sender, @Argument(value = "page") int page) {
        try {
            Flame flame = Tinder.get().flame();
            MessageCacheService messageCacheService = flame.services().messageCache();

            List<CacheableMessage> messages = messageCacheService.fetchMessagesPage(page);

            int numberOfPages = Math.floorDiv(messageCacheService.size(),10) + 1;

            reply(sender, ProxyLang.RC_MESSAGE_PAGE.build(messages, page, numberOfPages));
        } catch (Exception e) {
            error(sender, "There was an issue getting those messages!\n"+e.getMessage());
        }
    }

    @Command("rc message get")
    public void scfjnwbsynzbksyh(ConsoleCommandSource sender) {
        reply(sender, ProxyLang.RC_MESSAGE_GET_USAGE);
    }
    @Command("rc message get <snowflake>")
    public void nidbtmkngikxlzyo(ConsoleCommandSource sender, @Argument(value = "snowflake") long snowflake) {
        try {
            Flame flame = Tinder.get().flame();
            MessageCacheService messageCacheService = flame.services().messageCache();

            CacheableMessage message = messageCacheService.findMessage(snowflake);

            reply(sender, message.toString());
        } catch (Exception e) {
            error(sender, "There was an issue getting that message!\n"+e.getMessage());
        }
    }

    @Command("send")
    public void acmednrmiufxxviz(ConsoleCommandSource sender) {
        reply(sender, ProxyLang.RC_SEND_USAGE);
    }
    @Command("send <username>")
    @Command("send <username> server")
    public void gfkywqpzlgnohrxn(ConsoleCommandSource sender, @Argument(value = "username") String username) {
        reply(sender, ProxyLang.RC_SEND_USAGE);
    }
    @Command("send <username> <family_name>")
    public void qxeafgbinengqytu(ConsoleCommandSource sender, @Argument(value = "username") String username, @Argument(value = "family_name") String family_name) {
        try {
            // Uses this first so that we can start by checking if the player is online.
            Player player = new IPlayer.UsernameReference(username).get();
            if (!player.online()) {
                reply(sender, ProxyLang.RC_SEND_NO_PLAYER.build(username));
                return;
            }

            Family family = new Family.Reference(family_name).get();

            PlayerConnectable.Request request = family.connect(player);
            ConnectionResult result = request.result().get(30, TimeUnit.SECONDS);

            if(result.connected()) return;

            reply(sender, result.message());
        } catch (NoSuchElementException e) {
            reply(sender, ProxyLang.RC_SEND_NO_FAMILY.build(family_name));
        } catch (Exception e) {
            error(sender, "There was an issue using that command! "+e.getMessage());
        }
    }
    @Command("send <username> server <server_uuid>")
    public void mlavtgbdguegwcwi(ConsoleCommandSource sender, @Argument(value = "username") String username, @Argument(value = "server_uuid") String server_uuid) {
        try {
            // Uses this first so that we can start by checking if the player is online.
            Player player = new IPlayer.UsernameReference(username).get();
            if (!player.online()) {
                reply(sender, ProxyLang.RC_SEND_NO_PLAYER.build(username));
                return;
            }

            MCLoader server;
            try {
                server = new MCLoader.Reference(UUID.fromString(server_uuid)).get();
            } catch (Exception ignore) {
                reply(sender, ProxyLang.RC_SEND_NO_SERVER.build(server_uuid.toString()));
                return;
            }

            server.connect(player);
        } catch (Exception e) {
            error(sender, "There was an issue using that command! "+e.getMessage());
        }
    }

    private void printFamilyPage(ConsoleCommandSource sender, Family family, boolean showLocked) {
        if(family instanceof ScalarFamily)
            reply(sender, ProxyLang.RC_SCALAR_FAMILY_INFO.build((ScalarFamily) family, showLocked));
        if(family instanceof StaticFamily)
            reply(sender, ProxyLang.RC_STATIC_FAMILY_INFO.build((StaticFamily) family, showLocked));
        if(family instanceof RankedFamily)
            reply(sender, ProxyLang.RC_RANKED_FAMILY_INFO.build((RankedFamily) family, showLocked));
    }
    @Command("family")
    public void tdrdolhxvcjhaskb(ConsoleCommandSource sender) {
        reply(sender, ProxyLang.RC_FAMILY.build());
    }
    @Command("family <name>")
    public void mfndwqqzuiqmesyn(ConsoleCommandSource sender, @Argument(value = "name") String name) {
        Family family = new Family.Reference(name).get();

        printFamilyPage(sender, family, false);
    }

    @Command("family <name> resetIndex")
    public void kriytbuihowvzeoh(ConsoleCommandSource sender, @Argument(value = "name") String name) {
        try {
            Family family = new Family.Reference(name).get();

            family.loadBalancer().resetIndex();

            printFamilyPage(sender, family, false);
        } catch (Exception e) {
            error(sender, "Something prevented us from reseting the index on that family!\n"+e.getMessage());
        }
    }
    @Command("family <name> sort")
    public void rwgdcisxtfobunei(ConsoleCommandSource sender, @Argument(value = "name") String name) {
        try {
            Family family = new Family.Reference(name).get();

            family.balance();

            printFamilyPage(sender, family, false);
        } catch (Exception e) {
            error(sender, "Something prevented us from reseting the index on that family!\n"+e.getMessage());
        }
    }
    @Command("family <name> lockedServers")
    public void anwgfuuvjedsbisz(ConsoleCommandSource sender, @Argument(value = "name") String name) {
        try {
            Family family = new Family.Reference(name).get();

            printFamilyPage(sender, family, true);
        } catch (Exception e) {
            error(sender, "Something prevented us from reseting the index on that family!\n"+e.getMessage());
        }
    }
    @Command("family <name> players")
    public void myjchiprqzluchxe(ConsoleCommandSource sender, @Argument(value = "name") String name) {
        try {
            Family family = new Family.Reference(name).get();

            StringBuilder playerNames = new StringBuilder();
            List<com.velocitypowered.api.proxy.Player> players = family.players();
            com.velocitypowered.api.proxy.Player lastPlayer = players.get(players.size() - 1);
            for (com.velocitypowered.api.proxy.Player player : players) {
                if(player.equals(lastPlayer)) {
                    playerNames.append(player.getUsername());
                    break;
                }
                playerNames.append(player.getUsername()).append(", ");
            }

            reply(sender, Component.text(playerNames.toString()));
        } catch (Exception e) {
            error(sender, "Something prevented us from reseting the index on that family!\n"+e.getMessage());
        }
    }



    @Command("mcloaders")
    public void spxjakngfzwwpmma(ConsoleCommandSource sender) {
    }
    @Command("mcloaders locked")
    public void pgbospdypzqqqtar(ConsoleCommandSource sender) {
    }
    @Command("mcloaders stale")
    public void mlpdnlxutozqqdxd(ConsoleCommandSource sender) {
    }



    @Command("players")
    public void keqqribeadpmsenk(ConsoleCommandSource sender) {
    }
    @Command("players <uuid_or_username>")
    public void frocvrumnznwjpit(ConsoleCommandSource sender, @Argument(value="uuid_or_username") String uuid_or_username) {
    }


    @Command("friends <uuid_or_username>")
    @Command("players <uuid_or_username> friends")
    public void qrpfgflganrtrbku(ConsoleCommandSource sender, @Argument(value="uuid_or_username") String uuid_or_username) {
    }
    @Command("friends <uuid_or_username> remove <friends_uuid_or_username>")
    @Command("players <uuid_or_username> friends remove <friends_uuid_or_username>")
    public void yvwjruscvpreuuqy(ConsoleCommandSource sender, @Argument(value="uuid_or_username") String uuid_or_username, @Argument(value="friends_uuid_or_username") String friends_uuid_or_username) {
    }
    @Command("friends <uuid_or_username> add <friends_uuid_or_username>")
    @Command("players <uuid_or_username> friends add <friends_uuid_or_username>")
    public void bklyqsjdkydebjvf(ConsoleCommandSource sender, @Argument(value="uuid_or_username") String uuid_or_username, @Argument(value="friends_uuid_or_username") String friends_uuid_or_username) {
    }



    @Command("parties")
    public void vdskprafxmuqwtpl(ConsoleCommandSource sender) {
    }
    @Command("parties <uuid>")
    public void dwgrvlrqvszehfjb(ConsoleCommandSource sender, @Argument(value="uuid") String uuid) {
    }
    @Command("parties searchFor player <uuid_or_username>")
    @Command("players <uuid_or_username> party")
    public void rlpwwcqgxapxodje(ConsoleCommandSource sender, @Argument(value="uuid_or_username") String uuid_or_username) {
    }
    @Command("parties searchFor mcloader <uuid>")
    @Command("mcloader <uuid> parties")
    public void elpovlwixcohqdao(ConsoleCommandSource sender, @Argument(value="uuid") String uuid) {
    }
    @Command("parties searchFor family <name>")
    @Command("families <name> parties")
    public void tdwtipumkqpueobv(ConsoleCommandSource sender, @Argument(value="name") String name) {
    }
}