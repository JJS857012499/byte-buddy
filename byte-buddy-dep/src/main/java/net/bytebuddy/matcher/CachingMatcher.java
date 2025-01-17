/*
 * Copyright 2014 - Present Rafael Winterhalter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.bytebuddy.matcher;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

/**
 * A matcher that remembers the results of previously matching an equal target.
 *
 * @param <T> The actual matched type of this matcher.
 */
@HashCodeAndEqualsPlugin.Enhance(permitSubclassEquality = true)
public class CachingMatcher<T> extends ElementMatcher.Junction.AbstractBase<T> {

    /**
     * A substitute value to store in a map instead of a {@code null} value.
     */
    private static final Object NULL_VALUE = new Object();

    /**
     * The underlying matcher to apply for non-cached targets.
     */
    private final ElementMatcher<? super T> matcher;

    /**
     * A map that serves as a cache for previous matches.
     */
    @HashCodeAndEqualsPlugin.ValueHandling(HashCodeAndEqualsPlugin.ValueHandling.Sort.IGNORE)
    protected final ConcurrentMap<? super T, Boolean> map;

    /**
     * Creates a new caching matcher.
     *
     * @param matcher The underlying matcher to apply for non-cached targets.
     * @param map     A map that serves as a cache for previous matches. This match is strongly referenced and
     *                can cause a memory leak if it is not evicted while keeping this matcher alive.
     */
    public CachingMatcher(ElementMatcher<? super T> matcher, ConcurrentMap<? super T, Boolean> map) {
        this.matcher = matcher;
        this.map = map;
    }

    /**
     * {@inheritDoc}
     */
    public boolean matches(@Nullable T target) {
        Boolean cached = map.get(target == null
                ? NULL_VALUE
                : target);
        if (cached == null) {
            cached = onCacheMiss(target);
        }
        return cached;
    }

    /**
     * Invoked if the cache is not hit.
     *
     * @param target The element to be matched.
     * @return {@code true} if the element is matched.
     */
    @SuppressWarnings("unchecked")
    protected boolean onCacheMiss(@Nullable T target) {
        boolean cached = matcher.matches(target);
        map.put(target == null
                ? (T) NULL_VALUE
                : target, cached);
        return cached;
    }

    @Override
    public String toString() {
        return "cached(" + matcher + ")";
    }

    /**
     * A caching matcher with inline cache eviction.
     *
     * @param <S> The actual matched type of this matcher.
     */
    @SuppressFBWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "Equality does not consider eviction size")
    public static class WithInlineEviction<S> extends CachingMatcher<S> {

        /**
         * The maximum amount of entries in this map before removing a random entry from the map.
         */
        private final int evictionSize;

        /**
         * Creates a new caching matcher with inlined cache eviction.
         *
         * @param matcher      The underlying matcher to apply for non-cached targets.
         * @param map          A map that serves as a cache for previous matches. This match is strongly referenced and
         *                     can cause a memory leak if it is not evicted while keeping this matcher alive.
         * @param evictionSize The maximum amount of entries in this map before removing a random entry from the map.
         */
        public WithInlineEviction(ElementMatcher<? super S> matcher, ConcurrentMap<? super S, Boolean> map, int evictionSize) {
            super(matcher, map);
            this.evictionSize = evictionSize;
        }

        /**
         * {@inheritDoc}
         */
        protected boolean onCacheMiss(@Nullable S target) {
            if (map.size() >= evictionSize) {
                Iterator<?> iterator = map.entrySet().iterator();
                iterator.next();
                iterator.remove();
            }
            return super.onCacheMiss(target);
        }
    }
}

