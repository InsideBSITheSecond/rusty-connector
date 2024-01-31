package group.aelysium.rustyconnector.plugin.fabric.commands;

public final class CommandRusty {
    /*
    public static void create(FabricServerCommandManager<CommandSource> manager) {
        manager.command(messageList());
        manager.command(messageGet());
        manager.command(messageListPage());
        manager.command(send());
        manager.command(unlock());
        manager.command(lock());
    }

    private static void checkForConsole(@NonNull CommandContext<CommandSource> context) {
        Tinder api = Tinder.get();
        if(!context.getSender().equals(api.fabricServer().getCommandSource())) return;

        throw new RuntimeException("This command must be run from the console!");
    }

    private static Command.Builder<CommandSource> messageGet() {
        Tinder api = Tinder.get();
        PluginLogger logger = api.logger();
        final Command.Builder<CommandSource> builder = api.commandManager().commandBuilder("rc", "/rc");

        return builder.literal("message")
                .argument(StaticArgument.of("get"))
                .argument(LongArgument.of("snowflake"), ArgumentDescription.of("Message ID"))
                .handler(context -> {
                    checkForConsole(context);
                    try {
                        final Long snowflake = context.get("snowflake");

                        MessageCacheService messageCacheService = api.services().messageCache();

                        CacheableMessage message = messageCacheService.findMessage(snowflake);

                        MCLoaderLang.RC_MESSAGE_GET_MESSAGE.send(logger, message.getSnowflake(), message.getDate(), message.getContents());
                    } catch (NullPointerException e) {
                        logger.log("That message either doesn't exist or is no-longer available in the cache!");
                    } catch (Exception e) {
                        logger.log("An error stopped us from getting that message!", e);
                    }
                });
    }

    private static Command.Builder<CommandSource> messageList() {
        Tinder api = Tinder.get();
        PluginLogger logger = api.logger();
        final Command.Builder<CommandSource> builder = api.commandManager().commandBuilder("rc", "/rc");

        return builder.literal("message")
                .argument(StaticArgument.of("list"))
                .handler(context -> {
                    checkForConsole(context);
                    MessageCacheService cache = TinderAdapterForCore.getTinder().services().messageCache();
                    try {
                        if(cache.size() > 10) {
                            int numberOfPages = Math.floorDiv(cache.size(),10) + 1;

                            List<CacheableMessage> messagesPage = cache.fetchMessagesPage(1);

                            MCLoaderLang.RC_MESSAGE_PAGE.send(logger,messagesPage,1,numberOfPages);

                            return;
                        }

                        List<CacheableMessage> messages = cache.messages();

                        MCLoaderLang.RC_MESSAGE_PAGE.send(logger,messages,1,1);
                    } catch (Exception e) {
                        logger.send(Component.text("There was an issue getting those messages!\n"+e.getMessage(), NamedTextColor.RED));
                    }
                });
    }

    private static Command.Builder<CommandSource> messageListPage() {
        Tinder api = Tinder.get();
        PluginLogger logger = api.logger();
        final Command.Builder<CommandSource> builder = api.commandManager().commandBuilder("rc", "/rc");

        return builder.literal("message")
                .argument(StaticArgument.of("list"))
                .argument(IntegerArgument.of("page"), ArgumentDescription.of("The page number to fetch."))
                .handler(context -> {
                    checkForConsole(context);
                    MessageCacheService cache = TinderAdapterForCore.getTinder().services().messageCache();
                    try {
                        final Integer page = context.get("page");

                        List<CacheableMessage> messages = cache.fetchMessagesPage(page);

                        int numberOfPages = Math.floorDiv(cache.size(),10) + 1;

                        MCLoaderLang.RC_MESSAGE_PAGE.send(logger,messages,page,numberOfPages);
                    } catch (Exception e) {
                        logger.send(Component.text("There was an issue getting those messages!\n"+e.getMessage(), NamedTextColor.RED));
                    }
                });
    }

    private static Command.Builder<CommandSource> send() {
        Tinder api = Tinder.get();
        PluginLogger logger = api.logger();
        final Command.Builder<CommandSource> builder = api.commandManager().commandBuilder("rc", "/rc");

        return builder.literal("send")
                .argument(StringArgument.of("username"), ArgumentDescription.of("Username"))
                .argument(StringArgument.of("family-name"), ArgumentDescription.of("Family Name"))
                .handler(context -> {
                    checkForConsole(context);
                    try {
                        final String username = context.get("username");
                        final String familyName = context.get("family-name");

                        UUID playerUUID = Tinder.get().getPlayerUUID(username);

                        Packet message = api.services().packetBuilder().newBuilder()
                                .identification(BuiltInIdentifications.SEND_PLAYER)
                                .sendingToProxy()
                                .parameter(SendPlayerPacket.Parameters.TARGET_FAMILY_NAME, familyName)
                                .parameter(SendPlayerPacket.Parameters.PLAYER_UUID, playerUUID.toString())
                                .build();

                        api.services().magicLink().connection().orElseThrow().publish(message);
                    } catch (NullPointerException e) {
                        MCLoaderLang.RC_SEND_USAGE.send(logger);
                    } catch (Exception e) {
                        logger.log("An error stopped us from processing the request!", e);
                    }
                });
    }

    private static Command.Builder<CommandSource> unlock() {
        Tinder api = Tinder.get();
        PluginLogger logger = api.logger();

        final Command.Builder<CommandSource> builder = api.commandManager().commandBuilder("rc", "/rc");

        return builder.literal("unlock")
                .handler(context -> {
                    try {
                        api.services().magicLink().connection().orElseThrow().publish(MCLoader.Unlock.build(api.flame()));
                        logger.log("Unlocking server.");
                    } catch (NullPointerException e) {
                        MCLoaderLang.RC_SEND_USAGE.send(logger);
                    } catch (Exception e) {
                        logger.log("An error stopped us from processing the request!", e);
                    }
                });
    }

    private static Command.Builder<CommandSource> lock() {
        Tinder api = Tinder.get();
        PluginLogger logger = api.logger();

        final Command.Builder<CommandSource> builder = api.commandManager().commandBuilder("rc", "/rc");

        return builder.literal("lock")
                .handler(context -> {
                    try {
                        api.services().magicLink().connection().orElseThrow().publish(MCLoader.Lock.build(api.flame()));
                        logger.log("Locking server.");
                    } catch (NullPointerException e) {
                        MCLoaderLang.RC_SEND_USAGE.send(logger);
                    } catch (Exception e) {
                        logger.log("An error stopped us from processing the request!", e);
                    }
                });
    }*/
}