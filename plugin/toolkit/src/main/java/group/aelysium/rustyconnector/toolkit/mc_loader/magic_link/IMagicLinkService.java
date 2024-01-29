package group.aelysium.rustyconnector.toolkit.mc_loader.magic_link;

import group.aelysium.rustyconnector.toolkit.core.magic_link.ICoreMagicLinkService;
import group.aelysium.rustyconnector.toolkit.mc_loader.central.ICoreServiceHandler;
import group.aelysium.rustyconnector.toolkit.mc_loader.central.IMCLoaderFlame;

public interface IMagicLinkService extends ICoreMagicLinkService {
    /**
     * Set the ping delay for this upcoming ping.
     * @param delay The delay to set.
     */
    void setDelay(int delay);

    /**
     * Starts the heartbeat that this server's magic link uses.
     */
    void startHeartbeat(IMCLoaderFlame<? extends ICoreServiceHandler> api);
}
