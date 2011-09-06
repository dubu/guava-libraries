// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.common.cache;

import static com.google.common.cache.CacheTesting.checkEmpty;
import static com.google.common.cache.CacheTesting.checkValidState;
import static com.google.common.cache.TestingCacheLoaders.identityLoader;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.contrib.truth.Truth.ASSERT;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilderFactory.ExpirationSpec;
import com.google.common.cache.CustomConcurrentHashMap.Strength;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.testing.EqualsTester;

import junit.framework.TestCase;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * {@link Cache} tests that deal with caches that actually contain some key-value mappings.
 *
 * @author mike nonemacher
 */
public class PopulatedCachesTest extends TestCase {
  // we use integers as keys; make sure the range covers some values that ARE cached by
  // Integer.valueOf(int), and some that are not cached. (127 is the highest cached value.)
  static final int WARMUP_MIN = 120;
  static final int WARMUP_MAX = 135;
  static final int WARMUP_SIZE = WARMUP_MAX - WARMUP_MIN;

  public void testSize_populated() {
    for (Cache<Object, Object> cache : caches()) {
      // don't let the entries get GCed
      List<Entry<Object, Object>> warmed = warmUp(cache);
      assertEquals(WARMUP_SIZE, cache.size());
      assertMapSize(cache.asMap(), WARMUP_SIZE);
      checkValidState(cache);
    }
  }

  public void testContainsKey_found() {
    for (Cache<Object, Object> cache : caches()) {
      // don't let the entries get GCed
      List<Entry<Object, Object>> warmed = warmUp(cache);
      for (int i = WARMUP_MIN; i < WARMUP_MAX; i++) {
        Entry<Object, Object> entry = warmed.get(i - WARMUP_MIN);
        assertTrue(cache.asMap().containsKey(entry.getKey()));
        assertTrue(cache.asMap().containsValue(entry.getValue()));
        // this getUnchecked() call shouldn't be a cache miss; verified below
        assertEquals(entry.getValue(), cache.getUnchecked(entry.getKey()));
      }
      assertEquals(WARMUP_SIZE, cache.stats().missCount());
      checkValidState(cache);
    }
  }

  public void testPut_fails() {
    for (Cache<Object, Object> cache : caches()) {
      // don't let the entries get GCed
      List<Entry<Object, Object>> warmed = warmUp(cache);
      try {
        cache.asMap().put(-1, -1);
        fail("asMap().put() should have triggered UnsupportedOperationException");
      } catch (UnsupportedOperationException expected) {
      }
      checkValidState(cache);
    }
  }

  public void testPutIfAbsent_fails() {
    for (Cache<Object, Object> cache : caches()) {
      // don't let the entries get GCed
      List<Entry<Object, Object>> warmed = warmUp(cache);
      try {
        cache.asMap().putIfAbsent(-1, -1);
        fail("asMap().putIfAbsent() should have triggered UnsupportedOperationException");
      } catch (UnsupportedOperationException expected) {
      }
      checkValidState(cache);
    }
  }

  public void testPutAll_fails() {
    for (Cache<Object, Object> cache : caches()) {
      // don't let the entries get GCed
      List<Entry<Object, Object>> warmed = warmUp(cache);
      try {
        cache.asMap().putAll(ImmutableMap.of());
        fail("asMap().putAll() should have triggered UnsupportedOperationException");
      } catch (UnsupportedOperationException expected) {
      }
      checkValidState(cache);
    }
  }

  public void testReplace_fails() {
    for (Cache<Object, Object> cache : caches()) {
      // don't let the entries get GCed
      List<Entry<Object, Object>> warmed = warmUp(cache);
      try {
        cache.asMap().replace(warmed.get(1).getKey(), 2);
        fail("asMap().replace() should have triggered UnsupportedOperationException");
      } catch (UnsupportedOperationException expected) {
      }
      try {
        cache.asMap().replace(warmed.get(1).getKey(), warmed.get(1).getValue(), 2);
        fail("asMap().replace() should have triggered UnsupportedOperationException");
      } catch (UnsupportedOperationException expected) {
      }
      checkValidState(cache);
    }
  }

  public void testRemove_byKey() {
    for (Cache<Object, Object> cache : caches()) {
      // don't let the entries get GCed
      List<Entry<Object, Object>> warmed = warmUp(cache);
      for (int i = WARMUP_MIN; i < WARMUP_MAX; i++) {
        Entry<Object, Object> entry = warmed.get(i - WARMUP_MIN);
        Object key = entry.getKey();
        assertEquals(entry.getValue(), cache.asMap().remove(key));
        assertNull(cache.asMap().remove(key));
        assertFalse(cache.asMap().containsKey(key));
      }
      checkEmpty(cache);
    }
  }

  public void testRemove_byKeyAndValue() {
    for (Cache<Object, Object> cache : caches()) {
      // don't let the entries get GCed
      List<Entry<Object, Object>> warmed = warmUp(cache);
      for (int i = WARMUP_MIN; i < WARMUP_MAX; i++) {
        Object key = warmed.get(i - WARMUP_MIN).getKey();
        Object value = warmed.get(i - WARMUP_MIN).getValue();
        assertFalse(cache.asMap().remove(key, -1));
        assertTrue(cache.asMap().remove(key, value));
        assertFalse(cache.asMap().remove(key, -1));
        assertFalse(cache.asMap().containsKey(key));
      }
      checkEmpty(cache);
    }
  }

  public void testKeySet_populated() {
    for (Cache<Object, Object> cache : caches()) {
      Set<Object> keys = cache.asMap().keySet();
      List<Entry<Object, Object>> warmed = warmUp(cache);

      Object[] expectedArray = Maps.newHashMap(cache.asMap()).keySet().toArray(new Object[0]);
      ASSERT.that(keys).hasContentsAnyOrder(expectedArray);
      ASSERT.that(asList(keys.toArray())).hasContentsAnyOrder(expectedArray);
      ASSERT.that(asList(keys.toArray(new Object[cache.size()])))
          .hasContentsAnyOrder(expectedArray);

      new EqualsTester()
          .addEqualityGroup(cache.asMap().keySet(), keys)
          .addEqualityGroup(ImmutableSet.of())
          .testEquals();
      assertEquals(WARMUP_SIZE, keys.size());
      for (int i = WARMUP_MIN; i < WARMUP_MAX; i++) {
        Object key = warmed.get(i - WARMUP_MIN).getKey();
        assertTrue(keys.contains(key));
        assertTrue(keys.remove(key));
        assertFalse(keys.remove(key));
        assertFalse(keys.contains(key));
      }
      checkEmpty(keys);
      checkEmpty(cache);
    }
  }

  public void testValues_populated() {
    for (Cache<Object, Object> cache : caches()) {
      Collection<Object> values = cache.asMap().values();
      List<Entry<Object, Object>> warmed = warmUp(cache);

      Object[] expectedArray = Maps.newHashMap(cache.asMap()).values().toArray(new Object[0]);
      ASSERT.that(values).hasContentsAnyOrder(expectedArray);
      ASSERT.that(asList(values.toArray())).hasContentsAnyOrder(expectedArray);
      ASSERT.that(asList(values.toArray(new Object[cache.size()])))
          .hasContentsAnyOrder(expectedArray);

      assertEquals(WARMUP_SIZE, values.size());
      for (int i = WARMUP_MIN; i < WARMUP_MAX; i++) {
        Object value = warmed.get(i - WARMUP_MIN).getValue();
        assertTrue(values.contains(value));
        assertTrue(values.remove(value));
        assertFalse(values.remove(value));
        assertFalse(values.contains(value));
      }
      checkEmpty(values);
      checkEmpty(cache);
    }
  }

  @SuppressWarnings("unchecked") // generic array creation
  public void testEntrySet_populated() {
    for (Cache<Object, Object> cache : caches()) {
      Set<Entry<Object, Object>> entries = cache.asMap().entrySet();
      List<Entry<Object, Object>> warmed = warmUp(cache, WARMUP_MIN, WARMUP_MAX);

      Set<Entry<Object, Object>> entrySet = Maps.newHashMap(cache.asMap()).entrySet();
      ASSERT.that(entries).is(entrySet);
      ASSERT.that(entries.toArray()).hasContentsAnyOrder(entrySet.toArray());
      ASSERT.that(entries.toArray(new Entry[0]))
          .hasContentsAnyOrder(entrySet.toArray(new Entry[0]));

      new EqualsTester()
          .addEqualityGroup(cache.asMap().entrySet(), entries)
          .addEqualityGroup(ImmutableSet.of())
          .testEquals();
      assertEquals(WARMUP_SIZE, entries.size());
      for (int i = WARMUP_MIN; i < WARMUP_MAX; i++) {
        Entry<Object, Object> newEntry = warmed.get(i - WARMUP_MIN);
        assertTrue(entries.contains(newEntry));
        assertTrue(entries.remove(newEntry));
        assertFalse(entries.remove(newEntry));
        assertFalse(entries.contains(newEntry));
      }
      checkEmpty(entries);
      checkEmpty(cache);
    }
  }

  public void testWriteThroughEntry() {
    for (Cache<Object, Object> cache : caches()) {
      cache.getUnchecked(1);
      Entry<Object, Object> entry = Iterables.getOnlyElement(cache.asMap().entrySet());
      try {
        entry.setValue(3);
        fail("expected entry.setValue to throw UnsupportedOperationException");
      } catch (UnsupportedOperationException expected) {
      }
      try {
        entry.setValue(null);
        fail("expected entry.setValue(null) to throw UnsupportedOperationException");
      } catch (UnsupportedOperationException expected) {
      }
      checkValidState(cache);
    }
  }

  /* ---------------- Local utilities -------------- */

  /**
   * Most of the tests in this class run against every one of these caches.
   */
  private Iterable<Cache<Object, Object>> caches() {
    // lots of different ways to configure a Cache
    CacheBuilderFactory factory = cacheFactory();
    return Iterables.transform(factory.buildAllPermutations(),
        new Function<CacheBuilder<Object, Object>, Cache<Object, Object>>() {
          @Override public Cache<Object, Object> apply(CacheBuilder<Object, Object> builder) {
            return builder.build(identityLoader());
          }
        });
  }

  private CacheBuilderFactory cacheFactory() {
    // This is trickier than expected. We plan to put 15 values in each of these (WARMUP_MIN to
    // WARMUP_MAX), but the tests assume no values get evicted. Even with a maximumSize of 100, one
    // of the values gets evicted. With weak/soft keys, we use identity equality, which means using
    // System.identityHashCode, which means the assignment of keys to segments is nondeterministic,
    // so more than (maximumSize / #segments) keys could get assigned to the same segment, which
    // would cause one to be evicted.
    return new CacheBuilderFactory()
        .withKeyStrengths(ImmutableSet.of(Strength.STRONG, Strength.WEAK))
        .withValueStrengths(ImmutableSet.copyOf(Strength.values()))
        .withConcurrencyLevels(ImmutableSet.of(1, 4, 16, 64))
        .withMaximumSizes(ImmutableSet.of(400, 1000))
        .withInitialCapacities(ImmutableSet.of(0, 1, 10, 100, 1000))
        .withExpirations(ImmutableSet.of(
            ExpirationSpec.afterAccess(10, SECONDS),
            ExpirationSpec.afterAccess(1, DAYS),
            ExpirationSpec.afterWrite(10, SECONDS),
            ExpirationSpec.afterWrite(1, DAYS)));
  }

  private List<Map.Entry<Object, Object>> warmUp(Cache<Object, Object> cache) {
    return warmUp(cache, WARMUP_MIN, WARMUP_MAX);
  }

  /**
   * Returns the entries that were added to the map, so they won't fall out of a map with weak or
   * soft references until the caller drops the reference to the returned entries.
   */
  private List<Map.Entry<Object, Object>> warmUp(
      Cache<Object, Object> cache, int minimum, int maximum) {

    List<Map.Entry<Object, Object>> entries = Lists.newArrayList();
    for (int i = minimum; i < maximum; i++) {
      Object key = i;
      Object value = cache.getUnchecked(key);
      entries.add(entryOf(key, value));
    }
    return entries;
  }

  private Entry<Object, Object> entryOf(Object key, Object value) {
    return Maps.immutableEntry(key, value);
  }

  private void assertMapSize(Map<?, ?> map, int size) {
    assertEquals(size, map.size());
    if (size > 0) {
      assertFalse(map.isEmpty());
    } else {
      assertTrue(map.isEmpty());
    }
    assertCollectionSize(map.keySet(), size);
    assertCollectionSize(map.entrySet(), size);
    assertCollectionSize(map.values(), size);
  }

  private void assertCollectionSize(Collection<?> collection, int size) {
    assertEquals(size, collection.size());
    if (size > 0) {
      assertFalse(collection.isEmpty());
    } else {
      assertTrue(collection.isEmpty());
    }
    assertEquals(size, Iterables.size(collection));
    assertEquals(size, Iterators.size(collection.iterator()));
  }
}