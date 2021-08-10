/*
 * Copyright 2018 tomoncle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tomoncle.test.springboot.utils;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author tomoncle
 */
public class GuavaTest {

    @Test
    public void bloomFilter() {
        @SuppressWarnings("UnstableApiUsage")
        BloomFilter<String> bloomFilter = BloomFilter.create(
                Funnels.stringFunnel(Charsets.UTF_8), 2 << 8, 0.01);
        // put data
        bloomFilter.put("a");
        // check
        Assert.assertTrue(bloomFilter.mightContain("a"));
        Assert.assertFalse(bloomFilter.mightContain("b"));
    }
}
