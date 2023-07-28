package group.aelysium.rustyconnector.plugin.velocity.central;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.velocitypowered.api.scheduler.Scheduler;
import group.aelysium.rustyconnector.core.central.PluginAPI;
import group.aelysium.rustyconnector.plugin.velocity.PluginLogger;
import group.aelysium.rustyconnector.plugin.velocity.VelocityRustyConnector;
import group.aelysium.rustyconnector.plugin.velocity.config.DefaultConfig;
import group.aelysium.rustyconnector.plugin.velocity.lib.database.RedisSubscriber;
import group.aelysium.rustyconnector.plugin.velocity.lib.dynamic_teleport.DynamicTeleportService;
import group.aelysium.rustyconnector.plugin.velocity.lib.family.FamilyService;
import group.aelysium.rustyconnector.plugin.velocity.lib.family.bases.PlayerFocusedServerFamily;
import group.aelysium.rustyconnector.plugin.velocity.lib.friends.FriendsService;
import group.aelysium.rustyconnector.plugin.velocity.lib.parties.PartyService;
import group.aelysium.rustyconnector.plugin.velocity.lib.players.PlayerService;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SyncFailedException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.sql.SQLException;

public class VelocityAPI extends PluginAPI<Scheduler> {
    private static VelocityAPI instance;
    public static VelocityAPI get() {
        return instance;
    }

    private String version;
    private final VelocityRustyConnector plugin;
    private final ProxyServer server;
    private Processor processor = null;
    private final Path dataFolder;
    private final PluginLogger pluginLogger;

    public VelocityAPI(VelocityRustyConnector plugin, ProxyServer server, Logger logger, @DataDirectory Path dataFolder) {
        instance = this;

        try {
            InputStream stream = getResourceAsStream("velocity-plugin.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            JsonObject json = new Gson().fromJson(reader, JsonObject.class);

            stream.close();
            reader.close();
            this.version = json.get("version").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            this.version = null;
        }

        this.plugin = plugin;
        this.server = server;
        this.pluginLogger = new PluginLogger(logger);
        this.dataFolder = dataFolder;
    }

    @Override
    public InputStream getResourceAsStream(String filename)  {
        return getClass().getClassLoader().getResourceAsStream(filename);
    }

    @Override
    public String version() {
        return this.version;
    }

    @Override
    public Scheduler scheduler() {
        return getServer().getScheduler();
    }

    @Override
    public PluginLogger logger() {
        return this.pluginLogger;
    }

    @Override
    public String dataFolder() {
        return String.valueOf(this.dataFolder);
    }

    public void killServices() {
        this.processor.kill();
    }

    public void reloadServices() {
        this.processor.kill();
        this.processor = null;

        VelocityRustyConnector.getLifecycle().loadConfigs();
    }

    public void configureProcessor(DefaultConfig config) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, SQLException {
        PluginLogger logger = VelocityAPI.get().logger();
        if(this.processor != null) throw new IllegalAccessException("Attempted to configure the processor while it's already running!");
        this.processor = Processor.init(config);
        this.processor.services().redisService().start(RedisSubscriber.class);
        this.processor.services().magicLinkService().startHeartbeat();

        try {
            FamilyService familyService = this.processor.services().familyService();

            familyService.dump().forEach(baseServerFamily -> {
                try {
                    ((PlayerFocusedServerFamily) baseServerFamily).resolveParent();
                } catch (Exception e) {
                    logger.log("There was an issue resolving the parent for "+baseServerFamily.getName()+". "+e.getMessage());
                }
            });
        } catch (Exception ignore) {}
        try {
            FriendsService friendsService = Processor.Initializer.buildFriendsService().orElseThrow();

            this.processor.services().add(friendsService);

            friendsService.initCommand();
        } catch (Exception ignore) {}
        try {
            PlayerService playerService = Processor.Initializer.buildPlayerService().orElseThrow();

            this.processor.services().add(playerService);
        } catch (Exception ignore) {}
        try {
            PartyService partyService = Processor.Initializer.buildPartyService().orElseThrow();

            this.processor.services().add(partyService);

            partyService.initCommand();
        } catch (Exception ignore) {}
        try {
            DynamicTeleportService dynamicTeleportService = Processor.Initializer.buildDynamicTeleportService().orElseThrow();

            this.processor.services().add(dynamicTeleportService);

            try {
                dynamicTeleportService.services().tpaService().orElseThrow()
                                      .services().tpaCleaningService().startHeartbeat();
                dynamicTeleportService.services().tpaService().orElseThrow().initCommand();
            } catch (Exception ignore) {}

            try {
                dynamicTeleportService.services().hubService().orElseThrow().initCommand();
            } catch (Exception ignore) {}

            try {
                dynamicTeleportService.services().anchorService().orElseThrow().initCommands();
            } catch (Exception ignore) {}
        } catch (Exception ignore) {}
    }

    public ProcessorServiceHandler services() {
        return this.processor.services();
    }

    /**
     * Get the velocity server
     */
    public ProxyServer getServer() {
        return this.server;
    }

    /**
     * Registers a server with this proxy.` A server with this name should not already exist.
     *
     * @param serverInfo the server to register
     * @return the newly registered server
     */
    public RegisteredServer registerServer(ServerInfo serverInfo) {
        return getServer().registerServer(serverInfo);
    }

    /**
     * Unregisters this server from the proxy.
     *
     * @param serverInfo the server to unregister
     */
    public void unregisterServer(ServerInfo serverInfo) {
        getServer().unregisterServer(serverInfo);
    }

    /**
     * Attempt to access the plugin instance directly.
     * @return The plugin instance.
     * @throws SyncFailedException If the plugin is currently running.
     */
    public VelocityRustyConnector accessPlugin() throws SyncFailedException {
        if(VelocityRustyConnector.getLifecycle().isRunning()) throw new SyncFailedException("You can't get the plugin instance while the plugin is running!");
        return this.plugin;
    }
}
