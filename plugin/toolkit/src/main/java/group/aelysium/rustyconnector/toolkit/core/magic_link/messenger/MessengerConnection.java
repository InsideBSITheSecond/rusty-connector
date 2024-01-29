package group.aelysium.rustyconnector.toolkit.core.magic_link.messenger;

import group.aelysium.rustyconnector.toolkit.core.magic_link.packet.Packet;
import group.aelysium.rustyconnector.toolkit.core.magic_link.packet.PacketListener;
import group.aelysium.rustyconnector.toolkit.core.magic_link.packet.PacketPublisher;

public abstract class MessengerConnection extends PacketPublisher {
    /**
     * Register a listener to handle particular packets.
     * @param listener The listener to use.
     */
    public abstract  <TPacketListener extends PacketListener<? extends Packet.Wrapper>> void listen(TPacketListener listener);
}
