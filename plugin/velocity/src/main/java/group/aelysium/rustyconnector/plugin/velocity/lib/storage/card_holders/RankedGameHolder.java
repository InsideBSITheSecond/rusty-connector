package group.aelysium.rustyconnector.plugin.velocity.lib.storage.card_holders;

import group.aelysium.rustyconnector.plugin.velocity.lib.matchmaking.storage.RankedGame;
import group.aelysium.rustyconnector.plugin.velocity.lib.players.Player;
import group.aelysium.rustyconnector.toolkit.core.card.Card;
import group.aelysium.rustyconnector.toolkit.core.card.CardController;
import group.aelysium.rustyconnector.toolkit.velocity.matchmaking.storage.IScoreCard;

import java.util.UUID;

public class RankedGameHolder extends Card.Holder.Map<String, RankedGame> {
    @Override
    public Builder create() {
        return new Builder(this);
    }

    public static class Builder extends CardController.Create.Creator<RankedGame> {
        private String name;
        private IScoreCard.IRankSchema.Type<?> schema;

        public Builder(RankedGameHolder owner) {
            super(owner);
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder schema(IScoreCard.IRankSchema.Type<?> schema) {
            this.schema = schema;
            return this;
        }

        @Override
        public ReadyForInsert<RankedGame> prepare() {
            return new ReadyForInsert<>(this.owner, new RankedGame(this.name, this.schema));
        }
    }
}
