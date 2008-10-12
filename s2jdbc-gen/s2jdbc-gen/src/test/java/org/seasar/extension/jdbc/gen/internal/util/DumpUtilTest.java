/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.extension.jdbc.gen.internal.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author taedium
 * 
 */
public class DumpUtilTest {

    /**
     * 
     */
    @Test
    public void testEncode() {
        assertEquals("\"aa\na\"", DumpUtil.encode("aa\na"));
        assertEquals("\"aa\"\"a\"", DumpUtil.encode("aa\"a"));
        assertEquals("\"aaa\"", DumpUtil.encode("aaa"));
        assertEquals("\"\"", DumpUtil.encode(""));
        assertEquals("", DumpUtil.encode(null));
    }

    /**
     * 
     */
    @Test
    public void testDecode() {
        assertEquals("aa\na", DumpUtil.decode("\"aa\na\""));
        assertEquals("aa\"a", DumpUtil.decode("\"aa\"\"a\""));
        assertEquals("", DumpUtil.decode("\"\""));
        assertNull(DumpUtil.decode(""));
        assertNull(DumpUtil.decode(null));
    }

    /**
     * @throws Exception
     */
    @Test
    public void testIsDecodable_true() {
        assertTrue(DumpUtil.isDecodable("\"aaa\""));
        assertTrue(DumpUtil.isDecodable("\"aa\"a\""));
        assertTrue(DumpUtil.isDecodable("\"aa,a\""));
        assertTrue(DumpUtil.isDecodable("\"aa\na\""));
        assertTrue(DumpUtil.isDecodable("\"aa\ra\""));
    }

    /**
     * 
     */

    @Test
    public void testIsDecodable_false() {
        assertFalse(DumpUtil.isDecodable("\"aaa"));
        assertFalse(DumpUtil.isDecodable("aaa\""));
        assertFalse(DumpUtil.isDecodable("aa,a"));
        assertFalse(DumpUtil.isDecodable("aa\na"));
        assertFalse(DumpUtil.isDecodable("aa\ra"));
    }

}
