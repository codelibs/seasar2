package org.seasar.framework.ejb.unit.impl;

import static javax.persistence.DiscriminatorType.STRING;
import static javax.persistence.InheritanceType.JOINED;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.Table;

import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.FieldNotFoundRuntimeException;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.PropertyNotFoundRuntimeException;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.ejb.unit.AnnotationNotFoundException;
import org.seasar.framework.ejb.unit.PersistentClassDesc;
import org.seasar.framework.ejb.unit.PersistentStateDesc;
import org.seasar.framework.exception.EmptyRuntimeException;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.StringUtil;

/**
 * @author taedium
 * 
 */
public class EntityClassDesc implements PersistentClassDesc {

    private static String DEFAULT_DISCRIMINATOR_COLUMN = "DTYPE";

    private final Class<?> entityClass;

    private String name;

    private List<String> tableNames = new ArrayList<String>();

    private List<PersistentStateDesc> stateDescs = new ArrayList<PersistentStateDesc>();

    private Map<String, PersistentStateDesc> stateDescsByName = new HashMap<String, PersistentStateDesc>();

    private Map<String, PersistentStateDesc> stateDescsByColumnName = new HashMap<String, PersistentStateDesc>();

    private boolean propertyAccessed;

    private PersistentClassDesc mappedSuperclassDesc;

    private EntityClassDesc superClassDesc;

    private Map<String, Column> attribOverrides = new HashMap<String, Column>();

    private InheritanceType inheritanceType;

    private String discriminatorColumnName;

    private DiscriminatorType discriminatorType;

    private String discriminatorValue;

    private Map<String, String> pkJoinColumns = new HashMap<String, String>();

    private boolean rootEntity;

    public EntityClassDesc(Class<?> entityClass) {
        if (entityClass == null) {
            throw new EmptyRuntimeException("entityClass");
        }
        this.entityClass = entityClass;

        Entity entity = entityClass.getAnnotation(Entity.class);
        if (entity == null) {
            throw new AnnotationNotFoundException("@Entity", entityClass
                    .getName());
        }
        name = StringUtil.isEmpty(entity.name()) ? ClassUtil
                .getShortClassName(entityClass) : entity.name();

        setupTableNames();
        setupAccessType();
        setupAttributeOverrides();
        setupPrimaryKeyJoinColumns();
        setupSuperclass();
        setupInheritanceStrategy();
        setupPersistentStateDescs();
    }

    private void setupTableNames() {
        Table primary = entityClass.getAnnotation(Table.class);
        if (primary == null || StringUtil.isEmpty(primary.name())) {
            tableNames.add(name);
        } else {
            tableNames.add(primary.name());
        }

        SecondaryTable secondary = entityClass
                .getAnnotation(SecondaryTable.class);
        if (secondary != null) {
            tableNames.add(secondary.name());
        }

        SecondaryTables secondaries = entityClass
                .getAnnotation(SecondaryTables.class);
        if (secondaries != null) {
            for (SecondaryTable each : secondaries.value()) {
                tableNames.add(each.name());
            }
        }
    }

    private void setupAccessType() {
        List<Class<?>> entityHierarchy = new ArrayList<Class<?>>();
        for (Class<?> clazz = entityClass; clazz != Object.class
                && clazz != null; clazz = clazz.getSuperclass()) {
            entityHierarchy.add(clazz);
        }
        for (Class<?> clazz : entityHierarchy) {
            BeanDesc beanDesc2 = BeanDescFactory.getBeanDesc(clazz);
            for (int i = 0; i < beanDesc2.getPropertyDescSize(); i++) {
                PropertyDesc propertyDesc = beanDesc2.getPropertyDesc(i);
                if (propertyDesc.hasReadMethod()) {
                    Method m = propertyDesc.getReadMethod();
                    if (m.isAnnotationPresent(Id.class)
                            || m.isAnnotationPresent(EmbeddedId.class)) {
                        propertyAccessed = true;
                        return;
                    }
                }
            }
        }
    }

    private void setupAttributeOverrides() {
        AttributeOverride ao = entityClass
                .getAnnotation(AttributeOverride.class);
        if (ao != null) {
            setupOverridedColumns(ao);
        }
        AttributeOverrides aos = entityClass
                .getAnnotation(AttributeOverrides.class);
        if (aos != null) {
            for (AttributeOverride each : aos.value()) {
                if (each != null) {
                    setupOverridedColumns(each);
                }
            }
        }
    }

    private void setupOverridedColumns(AttributeOverride ao) {
        String name = ao.name();
        Column column = ao.column();
        attribOverrides.put(name, column);
    }

    private void setupPrimaryKeyJoinColumns() {
        PrimaryKeyJoinColumn pkColumn = entityClass
                .getAnnotation(PrimaryKeyJoinColumn.class);
        if (pkColumn != null) {
            pkJoinColumns.put(pkColumn.name(), pkColumn.referencedColumnName());
        }
        PrimaryKeyJoinColumns pkColumns = entityClass
                .getAnnotation(PrimaryKeyJoinColumns.class);
        if (pkColumns != null) {
            for (PrimaryKeyJoinColumn column : pkColumns.value()) {
                pkJoinColumns.put(column.name(), column.referencedColumnName());
            }
        }
    }

    private void setupSuperclass() {
        Class<?> superclass = entityClass.getSuperclass();

        if (superclass.isAnnotationPresent(Entity.class)) {
            superClassDesc = new EntityClassDesc(superclass);
        } else {
            rootEntity = true;
        }

        if (superclass.isAnnotationPresent(MappedSuperclass.class)) {
            mappedSuperclassDesc = new AttributeOverridableClassDesc(
                    superclass, tableNames.get(0), propertyAccessed,
                    attribOverrides);
        }

    }

    private void setupInheritanceStrategy() {
        if (rootEntity) {
            Inheritance inheritance = entityClass
                    .getAnnotation(Inheritance.class);
            if (inheritance != null) {
                inheritanceType = inheritance.strategy();
            }
            DiscriminatorColumn dc = entityClass
                    .getAnnotation(DiscriminatorColumn.class);
            if (dc != null) {
                discriminatorColumnName = StringUtil.isEmpty(dc.name()) ? DEFAULT_DISCRIMINATOR_COLUMN
                        : dc.name();
                discriminatorType = dc.discriminatorType();
            } else {
                discriminatorColumnName = DEFAULT_DISCRIMINATOR_COLUMN;
                discriminatorType = STRING;
            }
        }
        DiscriminatorValue dv = entityClass
                .getAnnotation(DiscriminatorValue.class);
        if (dv != null) {
            discriminatorValue = dv.value();
        } else if (discriminatorType == DiscriminatorType.STRING) {
            discriminatorValue = entityClass.getName();
        }
    }

    private EntityClassDesc getRootEntity() {
        if (rootEntity) {
            return this;
        }
        return superClassDesc.getRootEntity();
    }

    private InheritanceType getInheritanceType() {
        if (rootEntity) {
            return inheritanceType;
        }
        return superClassDesc.getInheritanceType();
    }

    private void setupPersistentStateDescs() {
        if (mappedSuperclassDesc != null) {
            for (int i = 0; i < mappedSuperclassDesc.getStateDescSize(); i++) {
                setupPersistentStateDescs(mappedSuperclassDesc.getStateDesc(i));
            }
        }

        if (superClassDesc != null) {
            for (int i = 0; i < superClassDesc.getStateDescSize(); i++) {
                setupPersistentStateDescs(superClassDesc.getStateDesc(i));
            }
        }

        EntityClassDesc root = getRootEntity();
        String primaryTableName = getPrimaryTableName();
        if (root.inheritanceType != JOINED) {
        }
        if (discriminatorValue != null) {
            DiscriminatorStateDesc d = new DiscriminatorStateDesc(
                    primaryTableName, root.discriminatorColumnName,
                    root.discriminatorType, discriminatorValue);
            setupPersistentStateDescs(d);
        }

        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(entityClass);
        for (int i = 0; i < beanDesc.getFieldSize(); i++) {
            Field field = beanDesc.getField(i);
            if (field.getDeclaringClass() != entityClass) {
                continue;
            }
            PersistentStateDesc ps = null;
            if (propertyAccessed) {
                if (beanDesc.hasPropertyDesc(field.getName())) {
                    ps = new PersistentPropertyDesc(beanDesc
                            .getPropertyDesc(field.getName()), primaryTableName);
                } else {
                    continue;
                }
            } else {
                ps = new PersistentFieldDesc(field, primaryTableName);
            }

            setupPersistentStateDescs(ps);
        }
    }

    private void setupPersistentStateDescs(PersistentStateDesc ps) {
        if (stateDescsByName.containsKey(ps.getStateName())) {
            PersistentStateDesc removed = stateDescsByName.remove(ps
                    .getStateName());
            stateDescs.remove(removed);
            if (stateDescsByColumnName.containsKey(removed.getColumnName())) {
                stateDescsByColumnName.remove(removed.getColumnName());
            }
        }
        stateDescs.add(ps);
        stateDescsByName.put(ps.getStateName(), ps);
        if (ps.getColumnName() != null) {
            stateDescsByColumnName.put(ps.getColumnName(), ps);
        }
    }

    private String getPrimaryTableName() {
        if (rootEntity) {
            return tableNames.get(0);
        } else {
            EntityClassDesc root = getRootEntity();
            if (root.getInheritanceType() == SINGLE_TABLE) {
                return root.getTableName(0);
            } else {
                return tableNames.get(0);
            }
        }
    }

    public String getName() {
        return name;
    }

    public Class<?> getPersistentClass() {
        return entityClass;
    }

    public PersistentStateDesc getStateDesc(int index) {
        return stateDescs.get(index);
    }

    public PersistentStateDesc getStateDesc(String persistentStateName) {
        if (stateDescsByName.containsKey(persistentStateName)) {
            return stateDescsByName.get(persistentStateName);
        }
        if (propertyAccessed) {
            throw new PropertyNotFoundRuntimeException(entityClass,
                    persistentStateName);

        } else {
            throw new FieldNotFoundRuntimeException(entityClass,
                    persistentStateName);
        }
    }

    public int getStateDescSize() {
        return stateDescs.size();
    }

    public String getTableName(int index) {
        return tableNames.get(index);
    }

    public int getTableSize() {
        return tableNames.size();
    }

    public boolean isPropertyAccessed() {
        return propertyAccessed;
    }
}
