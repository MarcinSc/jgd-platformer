package com.gempukku.secsy.context.util;

import com.google.common.base.Supplier;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.*;

public class PriorityCollection<T> implements Iterable<T> {
    private Multimap<Float, T> multimap = Multimaps.newMultimap(new TreeMap<>(Collections.reverseOrder()),
            (Supplier<Collection<T>>) ArrayList::new);

    public void add(T t) {
        float priority = getItemPriority(t);

        multimap.put(priority, t);
    }

    public void remove(T t) {
        float priority = getItemPriority(t);

        multimap.remove(priority, t);
    }

    private float getItemPriority(T t) {
        float priority = 0;
        if (t instanceof Prioritable)
            priority = ((Prioritable) t).getPriority();
        return priority;
    }

    @Override
    public Iterator<T> iterator() {
        return multimap.values().iterator();
    }
}
