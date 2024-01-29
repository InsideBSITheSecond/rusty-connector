package group.aelysium.rustyconnector.core.mcloader.lib.magic_link.handlers;

import group.aelysium.rustyconnector.core.lib.packets.BuiltInIdentifications;
import group.aelysium.rustyconnector.core.lib.packets.MagicLink;
import group.aelysium.rustyconnector.core.mcloader.central.MCLoaderTinder;
import group.aelysium.rustyconnector.core.mcloader.lib.magic_link.MagicLinkService;
import group.aelysium.rustyconnector.toolkit.core.logger.PluginLogger;
import group.aelysium.rustyconnector.toolkit.core.magic_link.packet.Packet;
import group.aelysium.rustyconnector.toolkit.core.magic_link.packet.PacketIdentification;
import group.aelysium.rustyconnector.toolkit.core.magic_link.packet.PacketListener;
import group.aelysium.rustyconnector.toolkit.mc_loader.central.IMCLoaderTinder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class HandshakeFailureListener extends PacketListener<MagicLink.Handshake.Failure> {
    protected IMCLoaderTinder api;

    public HandshakeFailureListener(IMCLoaderTinder api) {
        this.api = api;
    }

    @Override
    public PacketIdentification target() {
        return BuiltInIdentifications.MAGICLINK_HANDSHAKE_FAIL;
    }

    @Override
    public MagicLink.Handshake.Failure wrap(Packet packet) {
        return new MagicLink.Handshake.Failure(packet);
    }

    @Override
    public void execute(MagicLink.Handshake.Failure packet) {
        PluginLogger logger = api.logger();
        MagicLinkService service = ((MCLoaderTinder) api).services().magicLink();

        logger.send(Component.text(packet.reason(), NamedTextColor.RED));
        logger.send(Component.text("Waiting 1 minute before trying again...", NamedTextColor.GRAY));
        service.setDelay(60);
    }
}
