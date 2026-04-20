package com.erp.common.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class InMemoryRepository<E, ID> {

    protected final Map<ID, E> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    protected abstract ID extractId(E entity);

    protected long nextLongId() {
        return sequence.incrementAndGet();
    }

    public E save(E entity) {
        ID id = extractId(entity);
        if (id == null) throw new IllegalStateException("entity id must be assigned before save");
        store.put(id, entity);
        return entity;
    }

    public Optional<E> findById(ID id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<E> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<E> findAllBy(Predicate<E> predicate) {
        return store.values().stream().filter(predicate).collect(Collectors.toList());
    }

    public boolean existsById(ID id) { return store.containsKey(id); }
    public void deleteById(ID id)    { store.remove(id); }
    public long count()              { return store.size(); }
    public void clear()              { store.clear(); }
}