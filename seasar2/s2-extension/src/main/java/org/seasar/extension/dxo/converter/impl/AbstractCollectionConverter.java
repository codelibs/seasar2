/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.seasar.extension.dxo.converter.CollectionConverter;
import org.seasar.extension.dxo.converter.ConversionContext;
import org.seasar.extension.dxo.converter.Converter;

/**
 * 変換元クラスのインスタンスを変換先コレクションクラスのインスタンスに変換するコンバータの抽象クラスです。
 * 
 * @author koichik
 */
public abstract class AbstractCollectionConverter extends AbstractConverter
        implements CollectionConverter {

    public void convert(final Object source, final Object dest,
            final ConversionContext context) {
        final Collection destCollection = (Collection) dest;
        if (source.getClass().isArray()) {
            destCollection.addAll(Arrays.asList((Object[]) source));
        } else if (source instanceof Collection) {
            destCollection.addAll((Collection) source);
        } else {
            destCollection.add(source);
        }
    }

    public void convert(final Object source, final Object dest,
            final Class destElementClass, final ConversionContext context) {
        final Collection destCollection = (Collection) dest;
        if (source.getClass().isArray()) {
            convertFromArray((Object[]) source, destCollection,
                    destElementClass, context);
        } else if (source instanceof Collection) {
            convertFromCollection((Collection) source, destCollection,
                    destElementClass, context);
        } else if (destElementClass.isAssignableFrom(source.getClass())) {
            destCollection.add(source);
        } else {
            final Converter converter = context.getConverterFactory()
                    .getConverter(source.getClass(), destElementClass);
            destCollection.add(converter.convert(source, destElementClass,
                    context));
        }
    }

    /**
     * 配列からコレクションに変換します。
     * 
     * @param source
     *            変換元の配列
     * @param dest
     *            変換先のコレクション
     * @param destElementClass
     *            変換先コレクションの要素型
     * @param context
     *            変換コンテキスト
     */
    protected void convertFromArray(final Object[] source,
            final Collection dest, final Class destElementClass,
            final ConversionContext context) {
        if (destElementClass == null) {
            convert(source, dest, context);
            return;
        }
        final Class sourceElementClass = source.getClass().getComponentType();
        final Converter converter = context.getConverterFactory().getConverter(
                sourceElementClass, destElementClass);
        for (int i = 0; i < source.length; ++i) {
            final Object sourceElement = source[i];
            if (sourceElement == null) {
                dest.add(null);
            } else if (destElementClass.isAssignableFrom(sourceElement
                    .getClass())) {
                dest.add(sourceElement);
            } else {
                dest.add(converter.convert(sourceElement, destElementClass,
                        context));
            }
        }
    }

    /**
     * コレクションからコレクションに変換します。
     * 
     * @param source
     *            変換元のコレクション
     * @param dest
     *            変換先のコレクション
     * @param destElementClass
     *            変換先コレクションの要素型
     * @param context
     *            変換コンテキスト
     */
    protected void convertFromCollection(final Collection source,
            final Collection dest, final Class destElementClass,
            final ConversionContext context) {
        if (destElementClass == null) {
            convert(source, dest, context);
            return;
        }
        for (final Iterator it = source.iterator(); it.hasNext();) {
            final Object sourceElement = it.next();
            if (sourceElement == null) {
                dest.add(null);
            } else if (destElementClass.isAssignableFrom(sourceElement
                    .getClass())) {
                dest.add(sourceElement);
            } else {
                final Converter converter = context.getConverterFactory()
                        .getConverter(sourceElement.getClass(),
                                destElementClass);
                dest.add(converter.convert(sourceElement, destElementClass,
                        context));
            }
        }
    }

}