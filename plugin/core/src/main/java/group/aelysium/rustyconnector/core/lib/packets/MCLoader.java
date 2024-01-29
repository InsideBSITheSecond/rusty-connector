package group.aelysium.rustyconnector.core.lib.packets;

import group.aelysium.rustyconnector.core.mcloader.central.MCLoaderFlame;
import group.aelysium.rustyconnector.toolkit.core.magic_link.packet.Packet;

public interface MCLoader {
    class Lock extends Packet.Wrapper {
        public Lock(Packet packet) {
            super(packet);
        }

        public static Packet build(MCLoaderFlame flame) {
            return flame.services().magicLink().packetManager().newPacketBuilder()
                    .identification(BuiltInIdentifications.LOCK_SERVER)
                    .sendTo(Packet.Target.allAvailableProxies());
        }
    }
    class Unlock extends Packet.Wrapper {
        public Unlock(Packet packet) {
            super(packet);
        }

        public static Packet build(MCLoaderFlame flame) {
            return flame.services().magicLink().packetManager().newPacketBuilder()
                    .identification(BuiltInIdentifications.UNLOCK_SERVER)
                    .sendTo(Packet.Target.allAvailableProxies());
        }
    }
}
