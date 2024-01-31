package group.aelysium.rustyconnector.core.lib.lang;

import group.aelysium.rustyconnector.toolkit.core.logger.PluginLogger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY;

@SuppressWarnings("ConstantConditions")
public class Lang {
    public final static String attachedWordmark = "RustyConnector:";
    /*
     * AQUA - For when data is successfully returned or when we send usage info
     * RED - For when an error has occurred.
     * ORANGE/YELLOW - For emphasis or highlighting.
     */
    public final static JoinConfiguration newlines() {
        return JoinConfiguration.separator(newline());
    }

    public final static Component BORDER = text("█████████████████████████████████████████████████████████████████████████████████████████████████", DARK_GRAY);

    public final static Component SPACING = text("");

    public interface Message {
        Component build();

        default void send(PluginLogger sender) {
            sender.send(
                    join(
                            newlines(),
                            text(attachedWordmark),
                            build()
                    )
            );
        }
    }
    public interface ParameterizedMessage1<A1> {
        Component build(A1 arg1);

        default void send(PluginLogger sender, A1 arg1) {
            sender.send(
                    join(
                            JoinConfiguration.separator(newline()),
                            text(attachedWordmark),
                            build(arg1)
                    )
            );
        }
    }
    public interface ParameterizedMessage2<A1, A2> {
        Component build(A1 arg1, A2 arg2);

        default void send(PluginLogger sender, A1 arg1, A2 arg2) {
            sender.send(
                    join(
                            newlines(),
                            text(attachedWordmark),
                            build(arg1, arg2)
                    )
            );
        }
    }
    public interface ParameterizedMessage3<A1, A2, A3> {
        Component build(A1 arg1, A2 arg2, A3 arg3);

        default void send(PluginLogger sender, A1 arg1, A2 arg2, A3 arg3) {
            sender.send(
                    join(
                            newlines(),
                            text(attachedWordmark),
                            build(arg1, arg2, arg3)
                    )
            );
        }
    }
    public interface ParameterizedMessage4<A1, A2, A3, A4> {
        Component build(A1 arg1, A2 arg2, A3 arg3, A4 arg4);

        default void send(PluginLogger sender, A1 arg1, A2 arg2, A3 arg3, A4 arg4) {
            sender.send(
                    join(
                            newlines(),
                            text(attachedWordmark),
                            build(arg1, arg2, arg3, arg4)
                    )
            );
        }
    }

    public static class WindowBuilder implements Message {
        private Component header = null;
        private final List<Component> sections = new ArrayList<>();

        private WindowBuilder() {}

        public WindowBuilder header(String header) {
            return this.header(header, NamedTextColor.AQUA);
        }
        public WindowBuilder header(String header, NamedTextColor color) {
            this.header = ASCIIAlphabet.generate(header, color);
            return this;
        }

        public WindowBuilder section(Component section) {
            this.sections.add(section);
            return this;
        }

        public WindowBuilder section(Component... section) {
            this.sections.add(join(
                    newlines(),
                    section
            ));
            return this;
        }

        @Override
        public Component build() {
            Component built = Component.text().append(SPACING, BORDER).build();

            if(this.header != null)
                built = built.append(join(
                        newlines(),
                        SPACING,
                        this.header,
                        SPACING,
                        BORDER
                ));

            for (Component section : this.sections) {
                built = built.append(join(
                        newlines(),
                        SPACING,
                        section,
                        SPACING,
                        BORDER
                ));
            }

            return built;
        }

        public static WindowBuilder create() {
            return new WindowBuilder();
        }
    }
}




