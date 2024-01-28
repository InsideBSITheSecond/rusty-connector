public class TimedCache<K, E extends Entry> {
    private Map<K, E> items = new ConcurrentHashMap<>();
    private Map<K, Long> expirations = new ConcurrentHashMap<>();
    private LiquidTimestamp expiration;

    public void put(K key, E value) {
        this.items.put(key, value);
    }

    public static class Entry {
        private final long expiration;

        public Entry(LiquidTimestamp expireAfter) {
            this.expiration = expireAfter.epochFromNow();
        }

        public boolean expired() {
            return this.expiration < Instant.now();
        }
    }
}