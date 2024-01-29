package group.aelysium.rustyconnector.core.lib.cache;

import java.io.Closeable;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import group.aelysium.rustyconnector.toolkit.core.serviceable.ClockService;
import group.aelysium.rustyconnector.toolkit.velocity.util.LiquidTimestamp;

public class TimeoutCache<K, V> implements Closeable, Map<K, V> {
    private final Map<K, TimedValue<V>> map = new ConcurrentHashMap<>();
    private final LiquidTimestamp expiration;
    private final ClockService clock = new ClockService(1);
    private boolean shutdown = false;

    public TimeoutCache(LiquidTimestamp expiration) {
        this.expiration = expiration;
        this.clock.scheduleNow(this::evaluateThenRunAgain);
    }

    private void evaluateThenRunAgain() {
        long now = Instant.now().getEpochSecond();
        this.map.entrySet().removeIf(entry -> entry.getValue().olderThan(now));

        if(shutdown) return;
        this.clock.scheduleDelayed(this::evaluateThenRunAgain, this.expiration);
    }

    @Override
    public void close() throws IOException {
        this.shutdown = true;
        this.clock.kill();
    }

    @Override
    public V put(K key, V value) {
        this.map.put(key, new TimedValue<>(value, this.expiration.epochFromNow()));
        return value;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(new TimedValue<>(value, 0));
    }

    @Override
    public V get(Object key) {
        TimedValue<V> timedValue = this.map.get(key);
        return timedValue == null ? null : timedValue.value();
    }

    @Override
    public V remove(Object key) {
        TimedValue<V> timedValue = this.map.remove(key);
        return timedValue == null ? null : timedValue.value();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach((k, v) -> this.map.put(k, new TimedValue<>(v, this.expiration.epochFromNow())));
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Set<K> keySet() {
        return this.map.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.map.values().stream().map(TimedValue::value).collect(Collectors.toList());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return this.map.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().value()))
                .collect(Collectors.toSet());
    }

    protected static class TimedValue<V> {
        private V value;
        private long expiration;

        public TimedValue(V value, long expiration) {
            this.value = value;
            this.expiration = expiration;
        }

        public V value() {
            return value;
        }

        public boolean olderThan(long epochSeconds) {
            return this.expiration < epochSeconds;
        }

        public long expiration() {
            return expiration;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TimedValue<?> that = (TimedValue<?>) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
}