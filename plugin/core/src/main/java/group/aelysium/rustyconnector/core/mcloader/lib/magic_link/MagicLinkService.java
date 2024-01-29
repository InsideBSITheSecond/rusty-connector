package group.aelysium.rustyconnector.core.mcloader.lib.magic_link;

import group.aelysium.rustyconnector.core.lib.magic_link.MagicLinkCore;
import group.aelysium.rustyconnector.core.lib.magic_link.PacketManagerService;
import group.aelysium.rustyconnector.core.lib.packets.BuiltInIdentifications;
import group.aelysium.rustyconnector.core.mcloader.central.MCLoaderFlame;
import group.aelysium.rustyconnector.toolkit.core.magic_link.messenger.IMessengerConnector;
import group.aelysium.rustyconnector.toolkit.core.magic_link.packet.Packet;
import group.aelysium.rustyconnector.toolkit.core.magic_link.packet.PacketParameter;
import group.aelysium.rustyconnector.core.lib.packets.MagicLink;
import group.aelysium.rustyconnector.toolkit.mc_loader.central.ICoreServiceHandler;
import group.aelysium.rustyconnector.toolkit.mc_loader.central.IMCLoaderFlame;
import group.aelysium.rustyconnector.toolkit.mc_loader.events.magic_link.DisconnectedEvent;
import group.aelysium.rustyconnector.toolkit.mc_loader.magic_link.IMagicLinkService;
import group.aelysium.rustyconnector.toolkit.core.serviceable.ClockService;
import group.aelysium.rustyconnector.toolkit.mc_loader.server_info.IServerInfoService;
import group.aelysium.rustyconnector.toolkit.velocity.util.LiquidTimestamp;
import group.aelysium.rustyconnector.core.TinderAdapterForCore;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MagicLinkService extends MagicLinkCore implements IMagicLinkService {
    private final ClockService heartbeat = new ClockService(2);
    private final AtomicInteger delay = new AtomicInteger(5);
    private boolean stopPinging = false;

    public MagicLinkService(IMessengerConnector messenger) {
        super(messenger);
    }

    public void setDelay(int delay) {
        this.delay.set(delay);
    }

    private void scheduleNextPing(IMCLoaderFlame<? extends ICoreServiceHandler> flame) {
        IServerInfoService serverInfoService = flame.services().serverInfo();
        this.heartbeat.scheduleDelayed(() -> {
            if(stopPinging) return;

            try {
                this.packetManager().newPacketBuilder()
                        .identification(BuiltInIdentifications.MAGICLINK_HANDSHAKE_PING)
                        .parameter(MagicLink.Handshake.Ping.Parameters.ADDRESS, serverInfoService.address())
                        .parameter(MagicLink.Handshake.Ping.Parameters.DISPLAY_NAME, serverInfoService.displayName())
                        .parameter(MagicLink.Handshake.Ping.Parameters.MAGIC_CONFIG_NAME, serverInfoService.magicConfig())
                        .parameter(MagicLink.Handshake.Ping.Parameters.PLAYER_COUNT, new PacketParameter(serverInfoService.playerCount()))
                        .sendTo(Packet.Target.allAvailableProxies());
            } catch (Exception e) {
                e.printStackTrace();
            }

            MagicLinkService.this.scheduleNextPing(flame);
        }, LiquidTimestamp.from(this.delay.get(), TimeUnit.SECONDS));
    }

    public void startHeartbeat(IMCLoaderFlame<? extends ICoreServiceHandler> flame) {
        this.packetManager = new PacketManagerService<>(flame);

        this.scheduleNextPing(flame);
    }

    @Override
    public void kill() {
        stopPinging = true;

        try {
            MCLoaderFlame flame = TinderAdapterForCore.getTinder().flame();

            this.packetManager().newPacketBuilder()
                    .identification(BuiltInIdentifications.MAGICLINK_HANDSHAKE_DISCONNECT)
                    .sendTo(Packet.Target.allAvailableProxies());

            flame.services().events().fireEvent(new DisconnectedEvent());
        } catch (Exception ignore) {}

        this.heartbeat.kill();
        this.redisConnector.kill();
    }
}
