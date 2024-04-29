package group.aelysium.rustyconnector.plugin.paper.lib.magic_link.handlers;

import group.aelysium.rustyconnector.core.lib.packets.GenericPacket;
import group.aelysium.rustyconnector.core.lib.packets.PacketHandler;
import group.aelysium.rustyconnector.core.lib.packets.variants.ServerPingResponsePacket;
import group.aelysium.rustyconnector.plugin.paper.PluginLogger;
import group.aelysium.rustyconnector.plugin.paper.central.Tinder;
import group.aelysium.rustyconnector.plugin.paper.lib.magic_link.MagicLinkService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class MagicLink_PingResponseHandler extends PacketHandler {
    @Override
    public void execute(GenericPacket genericPacket) {
        ServerPingResponsePacket packet = (ServerPingResponsePacket) genericPacket;

        Tinder api = Tinder.get();
        PluginLogger logger = api.logger();
        MagicLinkService service = api.services().magicLink();

        if(packet.status() == ServerPingResponsePacket.PingResponseStatus.ACCEPTED) {
            logger.send(PlainTextComponentSerializer.plainText().serialize(
                    Component.text(packet.message(), packet.color())));

            if(packet.pingInterval().isPresent()) {
                service.setUpcomingPingDelay(packet.pingInterval().get());
            } else {
                logger.send(PlainTextComponentSerializer.plainText().serialize(
                        Component.text("No ping interval was given during registration! Defaulting to 15 seconds!", NamedTextColor.YELLOW)));
                service.setUpcomingPingDelay(15);
            }

            service.setStatus(MagicLinkService.Status.CONNECTED);
        }

        if(packet.status() == ServerPingResponsePacket.PingResponseStatus.DENIED) {
            logger.send(PlainTextComponentSerializer.plainText().serialize(
                    Component.text(packet.message(), packet.color())));
            logger.send(PlainTextComponentSerializer.plainText().serialize(
                    Component.text("Waiting 1 minute before trying again...", NamedTextColor.GRAY)));
            service.setUpcomingPingDelay(60);
            service.setStatus(MagicLinkService.Status.SEARCHING);
        }
    }
}
