package group.aelysium.rustyconnector.toolkit.core.card;

import org.eclipse.serializer.collections.lazy.LazyArrayList;
import org.eclipse.serializer.collections.lazy.LazyHashMap;
import org.eclipse.serializer.collections.lazy.LazyHashSet;
import org.eclipse.serializer.persistence.types.Persister;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class Card implements CardController.Alter, CardController.Delete {
    private boolean deleted = false;
    private transient Persister persister;
    /**
     * Gets the parent {@link Holder} of the Card.
     */
    public final Holder<Card> parent() {
        return (Holder<Card>) this.persister;
    }

    /**
     * Takes in the past attribute and compares it to an attribute in the Card.
     * @param attribute The attribute to search for.
     * @return `true` if the attribute matches. `false` otherwise.
     */
    public abstract boolean attributeEquals(Attribute<?> attribute);

    @Override
    public void delete() {
        this.parent().delete(this);
        this.deleted = true;
    }

    @Override
    public final boolean deleted() {
        return this.deleted;
    }

    public final void store() {
        this.persister.store(this);
    }

    /**
     * An implementation of {@link Card} for use with {@link Card.Holder.Map} which guarantees that there is a key for {@link Card.Holder.Map} to access.
     * @param <K> The key for this card.
     */
    public abstract static class WithKey<K> extends Card {
        /**
         * Returns the key that this card uses.
         * The key should be valid in that it must be able to be used by the {@link LazyHashMap} that {@link Card.Holder.Map} uses.
         * @return This {@link Card Card's} key.
         */
        public abstract K key();
    }

    public abstract static class Holder<C extends Card> implements CardController.Create, CardController.Storeable<C>, CardController.Deletable<C> {
        protected transient Persister persister;
        private Holder() {}

        public abstract Creator<?> create();

        public abstract static class Set<C extends Card> extends Holder<C> implements CardController.Read.Entry<C> {
            private final LazyHashSet<C> items = new LazyHashSet<>();

            @Override
            public Optional<C> searchFor(Attribute... attributes) {
                return this.items.stream().filter(item -> {
                    for (Attribute attribute : attributes)
                        return item.attributeEquals(attribute);

                    return false;
                }).findAny();
            }

            @Override
            public Stream<C> filter(Predicate<C> predicate) {
                return this.items.stream().filter(predicate);
            }

            @Override
            public void store(@NotNull C card) {
                this.items.add(card);
                this.persister.store(this.items);
            }

            @Override
            public void delete(@NotNull C card) {
                this.items.remove(card);
                this.persister.store(this.items);
            }
        }

        public abstract static class List<C extends Card> extends Holder<C> implements CardController.Read.Entry<C> {
            private final LazyArrayList<C> items = new LazyArrayList<>();

            @Override
            public Optional<C> searchFor(Attribute... attributes) {
                return this.items.stream().filter(item -> {
                    for (Attribute attribute : attributes)
                        return item.attributeEquals(attribute);

                    return false;
                }).findAny();
            }

            @Override
            public Stream<C> filter(Predicate<C> predicate) {
                return this.items.stream().filter(predicate);
            }

            @Override
            public void store(@NotNull C card) {
                this.items.add(card);
                this.persister.store(this.items);
            }

            @Override
            public void delete(@NotNull C card) {
                this.items.remove(card);
                this.persister.store(this.items);
            }
        }

        public abstract static class Map<K, CWK extends Card.WithKey<K>> extends Holder<CWK> implements CardController.Read.KeyValue<K, CWK> {
            private final LazyHashMap<K, CWK> items = new LazyHashMap<>();

            @Override
            public Optional<CWK> searchFor(Attribute... attributes) {
                return this.items.values().stream().filter(item -> {
                    for (Attribute attribute : attributes)
                        return item.attributeEquals(attribute);

                    return false;
                }).findAny();
            }

            @Override
            public Stream<CWK> filter(Predicate<CWK> predicate) {
                return this.items.values().stream().filter(predicate);
            }

            @Override
            public Optional<CWK> fetch(K key) {
                CWK item = this.items.get(key);
                if(item == null) return Optional.empty();
                return Optional.of(item);
            }

            @Override
            public void store(@NotNull CWK card) {
                this.items.put(card.key(), card);
                this.persister.store(this.items);
            }

            @Override
            public void delete(@NotNull CWK card) {
                this.items.remove(card.key());
                this.persister.store(this.items);
            }
        }
    }

    public record Attribute<T>(String name, T attribute) {
        @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Attribute<T> attribute = (Attribute<T>) o;
                return Objects.equals(name, attribute.name);
            }

            @Override
            public int hashCode() {
                return Objects.hash(name);
            }
        }
}