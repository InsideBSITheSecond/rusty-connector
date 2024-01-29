package group.aelysium.rustyconnector.toolkit.velocity.magic_link;

import group.aelysium.rustyconnector.toolkit.core.magic_link.ICoreMagicLinkService;

import java.util.Optional;

public interface IMagicLinkService extends ICoreMagicLinkService {
    /**
     * Fetches a Magic Link MCLoader Config based on a name.
     * `name` is considered to be the name of the file found in `magic_configs` on the Proxy, minus the file extension.
     * @param name The name to look for.
     */
    Optional<MagicLinkMCLoaderSettings> magicConfig(String name);

    record MagicLinkMCLoaderSettings(
            String family,
            int weight,
            int soft_cap,
            int hard_cap
    ) {};
}
