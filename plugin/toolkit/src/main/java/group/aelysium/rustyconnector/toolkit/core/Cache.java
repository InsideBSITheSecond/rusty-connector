package group.aelysium.rustyconnector.toolkit.core;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Cache<K, V> {
    private final AtomicBoolean enabled = new AtomicBoolean(true);
    private final ScheduledExecutorService supervisor = Executors.newSingleThreadScheduledExecutor();
    private final Map<Key<K>, V> map = new ConcurrentHashMap<>();
    private final int expirationAfterSeconds;

    public Cache(int expirationAfterSeconds) {
        this.expirationAfterSeconds = expirationAfterSeconds;

        supervisor.submit(this.startSupervisor());
    }
    public Cache() {
        this(20);
    }

    private Runnable startSupervisor() {
        return () -> {
            this.map.entrySet().forEach(entry -> {
                if(entry.getKey().expired()) this.map.remove(entry.getKey());
                entry.getKey().decrementExpiration();
            });
            if(!enabled.get()) return;
            supervisor.schedule(this.startSupervisor(), 1, TimeUnit.SECONDS);
        };
    }

    public void put(K key, V value) {
        this.map.put(new Key<>(key, this.expirationAfterSeconds), value);
    }

    public void end() {
        this.enabled.set(false);
        this.supervisor.close();
        this.map.clear();
    }

    public static class Key<K> {
        private final K key;
        private final AtomicInteger expiration;

        public Key(K key, int expirationInSeconds) {
            this.key = key;
            this.expiration = new AtomicInteger(expirationInSeconds);
        }

        public boolean expired() {
            return this.expiration.get() <= 0;
        }

        public void decrementExpiration() {
            this.expiration.decrementAndGet();
        }

        public K key() {
            return this.key;
        }
    }
}
