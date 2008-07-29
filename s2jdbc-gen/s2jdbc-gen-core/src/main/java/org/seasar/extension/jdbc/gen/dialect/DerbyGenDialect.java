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
package org.seasar.extension.jdbc.gen.dialect;

import java.math.BigDecimal;
import java.sql.Types;

import javax.persistence.GenerationType;
import javax.persistence.TemporalType;

/**
 * Derbyの方言を扱うクラスです。
 * 
 * @author taedium
 */
public class DerbyGenDialect extends StandardGenDialect {

    /** テーブルが見つからないことを示すSQLステート */
    protected static String TABLE_NOT_FOUND_SQL_STATE = "42X05";

    /**
     * インスタンスを構築します。
     */
    public DerbyGenDialect() {
        typeMap.put(Types.BINARY, DerbySqlType.BINARY);
        typeMap.put(Types.BLOB, DerbySqlType.BLOB);
        typeMap.put(Types.BOOLEAN, DerbySqlType.BOOLEAN);
        typeMap.put(Types.CLOB, DerbySqlType.CLOB);
        typeMap.put(Types.DECIMAL, DerbySqlType.DECIMAL);
        typeMap.put(Types.FLOAT, DerbySqlType.FLOAT);

        namedTypeMap.put("blob", DerbyColumnType.BLOB);
        namedTypeMap.put("char () for bit data", DerbyColumnType.CHAR_BIT);
        namedTypeMap.put("clob", DerbyColumnType.CLOB);
        namedTypeMap.put("decimal", DerbyColumnType.DECIMAL);
        namedTypeMap.put("long varchar bit data",
                DerbyColumnType.LONGVARCHAR_BIT);
        namedTypeMap.put("long varchar", DerbyColumnType.LONGVARCHAR);
        namedTypeMap
                .put("varchar () for bit data", DerbyColumnType.VARCHAR_BIT);
    }

    @Override
    public GenerationType getDefaultGenerationType() {
        return GenerationType.IDENTITY;
    }

    @Override
    public String getIdentityColumnDefinition() {
        return "not null generated always as identity";
    }

    @Override
    public boolean isTableNotFound(Throwable throwable) {
        String sqlState = getSQLState(throwable);
        return TABLE_NOT_FOUND_SQL_STATE.equals(sqlState);
    }

    /**
     * Derby用の{@link SqlType}の実装です。
     * 
     * @author taedium
     */
    public static class DerbySqlType extends StandardSqlType {

        private static DerbySqlType BINARY = new DerbySqlType(
                "varchar($l) for bit data");

        private static DerbySqlType BLOB = new DerbySqlType("blob($l)");

        private static DerbySqlType BOOLEAN = new DerbySqlType("smallint(1)");

        private static DerbySqlType CLOB = new DerbySqlType("clob($l)");

        private static DerbySqlType DECIMAL = new DerbySqlType("decimal($p,$s)");

        private static DerbySqlType FLOAT = new DerbySqlType("real");

        /**
         * インスタンスを構築します。
         * 
         * @param columnDefinition
         *            カラム定義
         */
        protected DerbySqlType(String columnDefinition) {
            super(columnDefinition);
        }
    }

    public static class DerbyColumnType extends StandardColumnType {

        private static DerbyColumnType BLOB = new DerbyColumnType("blob($l)",
                byte[].class);

        private static DerbyColumnType CHAR_BIT = new DerbyColumnType(
                "char($l) for bit data", byte[].class);

        private static DerbyColumnType CLOB = new DerbyColumnType("clob($l)",
                String.class);

        private static DerbyColumnType DECIMAL = new DerbyColumnType(
                "decimal($p,$s)", BigDecimal.class);

        private static DerbyColumnType LONGVARCHAR_BIT = new DerbyColumnType(
                "long varchar for bit data", byte[].class);

        private static DerbyColumnType LONGVARCHAR = new DerbyColumnType(
                "long varchar", String.class);

        private static DerbyColumnType VARCHAR_BIT = new DerbyColumnType(
                "varchar(%d) for bit data", byte[].class);

        public DerbyColumnType(String columnDefinition, Class<?> attributeClass) {
            super(columnDefinition, attributeClass);
        }

        public DerbyColumnType(String columnDefinition,
                Class<?> attributeClass, boolean lob) {
            super(columnDefinition, attributeClass, lob);
        }

        public DerbyColumnType(String columnDefinition,
                Class<?> attributeClass, TemporalType temporalType) {
            super(columnDefinition, attributeClass, temporalType);
        }

    }
}
