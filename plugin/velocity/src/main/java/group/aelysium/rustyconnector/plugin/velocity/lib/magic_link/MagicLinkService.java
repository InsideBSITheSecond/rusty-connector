package group.aelysium.rustyconnector.plugin.velocity.lib.magic_link;

import group.aelysium.rustyconnector.core.lib.magic_link.MagicLinkCore;
import group.aelysium.rustyconnector.core.lib.magic_link.PacketManagerService;
import group.aelysium.rustyconnector.plugin.velocity.central.CoreServiceHandler;
import group.aelysium.rustyconnector.toolkit.core.magic_link.messenger.IMessengerConnector;
import group.aelysium.rustyconnector.toolkit.core.serviceable.ClockService;
import group.aelysium.rustyconnector.plugin.velocity.lib.server.ServerService;
import group.aelysium.rustyconnector.toolkit.velocity.central.VelocityFlame;
import group.aelysium.rustyconnector.toolkit.velocity.magic_link.IMagicLinkService;

import java.util.*;

public class MagicLinkService extends MagicLinkCore implements IMagicLinkService {
    protected ClockService mcloaderSteralizer = new ClockService(1);
    protected final long interval;
    protected Map<String, MagicLinkMCLoaderSettings> settingsMap;

    public MagicLinkService(long interval, IMessengerConnector redisConnector, Map<String, MagicLinkMCLoaderSettings> magicLinkMCLoaderSettingsMap) {
        super(redisConnector);
        this.interval = interval;
        this.settingsMap = magicLinkMCLoaderSettingsMap;
    }

    public void startHeartbeat(ServerService serverService, VelocityFlame<CoreServiceHandler> flame) {
        this.packetManager = new PacketManagerService<>(flame);

        this.mcloaderSteralizer.scheduleRecurring(() -> {
            try {
                // Unregister any stale servers
                // The removing feature of server#unregister is valid because serverService.servers() creates a new list which isn't bound to the underlying list.
                serverService.servers().forEach(server -> {
                    server.decreaseTimeout(3);

                    try {
                        if (server.stale()) server.unregister(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception ignore) {}
        }, 3, 5); // Period of `3` lets us not loop over the servers as many times with a small hit to how quickly stale servers will be unregistered.
    }

    public Optional<MagicLinkMCLoaderSettings> magicConfig(String name) {
        MagicLinkMCLoaderSettings settings = this.settingsMap.get(name);
        if(settings == null) return Optional.empty();
        return Optional.of(settings);
    }

    @Override
    public void kill() {
        super.kill();
        this.mcloaderSteralizer.kill();
    }
}
