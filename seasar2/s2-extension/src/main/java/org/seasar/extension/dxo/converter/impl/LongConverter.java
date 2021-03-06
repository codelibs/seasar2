/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
package org.seasar.extension.dxo.converter.impl;

/**
 * {@link Number 数}から{@link Long}への変換を行うコンバータです。
 * 
 * @author Satoshi Kimura
 * @author koichik
 */
public class LongConverter extends NumberConverter {

    public Class getDestClass() {
        return Long.class;
    }

    protected Number convert(final Number number) {
        if (number instanceof Long) {
            return number;
        }
        return new Long(number.longValue());
    }

}
