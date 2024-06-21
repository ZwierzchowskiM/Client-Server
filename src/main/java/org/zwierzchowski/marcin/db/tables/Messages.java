/*
 * This file is generated by jOOQ.
 */
package org.zwierzchowski.marcin.db.tables;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.InverseForeignKey;
import org.jooq.Name;
import org.jooq.Path;
import org.jooq.PlainSQL;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.SQL;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.Stringly;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.zwierzchowski.marcin.db.Keys;
import org.zwierzchowski.marcin.db.Public;
import org.zwierzchowski.marcin.db.tables.Users.UsersPath;
import org.zwierzchowski.marcin.db.tables.records.MessagesRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Messages extends TableImpl<MessagesRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.messages</code>
     */
    public static final Messages MESSAGES = new Messages();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MessagesRecord> getRecordType() {
        return MessagesRecord.class;
    }

    /**
     * The column <code>public.messages.user_id</code>.
     */
    public final TableField<MessagesRecord, Integer> USER_ID = createField(DSL.name("user_id"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>public.messages.content</code>.
     */
    public final TableField<MessagesRecord, String> CONTENT = createField(DSL.name("content"), SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>public.messages.sender</code>.
     */
    public final TableField<MessagesRecord, String> SENDER = createField(DSL.name("sender"), SQLDataType.VARCHAR(20), this, "");

    /**
     * The column <code>public.messages.date</code>.
     */
    public final TableField<MessagesRecord, LocalDateTime> DATE = createField(DSL.name("date"), SQLDataType.LOCALDATETIME(6), this, "");

    /**
     * The column <code>public.messages.status</code>.
     */
    public final TableField<MessagesRecord, String> STATUS = createField(DSL.name("status"), SQLDataType.VARCHAR(10), this, "");

    /**
     * The column <code>public.messages.id</code>.
     */
    public final TableField<MessagesRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    private Messages(Name alias, Table<MessagesRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private Messages(Name alias, Table<MessagesRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>public.messages</code> table reference
     */
    public Messages(String alias) {
        this(DSL.name(alias), MESSAGES);
    }

    /**
     * Create an aliased <code>public.messages</code> table reference
     */
    public Messages(Name alias) {
        this(alias, MESSAGES);
    }

    /**
     * Create a <code>public.messages</code> table reference
     */
    public Messages() {
        this(DSL.name("messages"), null);
    }

    public <O extends Record> Messages(Table<O> path, ForeignKey<O, MessagesRecord> childPath, InverseForeignKey<O, MessagesRecord> parentPath) {
        super(path, childPath, parentPath, MESSAGES);
    }

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    public static class MessagesPath extends Messages implements Path<MessagesRecord> {
        public <O extends Record> MessagesPath(Table<O> path, ForeignKey<O, MessagesRecord> childPath, InverseForeignKey<O, MessagesRecord> parentPath) {
            super(path, childPath, parentPath);
        }
        private MessagesPath(Name alias, Table<MessagesRecord> aliased) {
            super(alias, aliased);
        }

        @Override
        public MessagesPath as(String alias) {
            return new MessagesPath(DSL.name(alias), this);
        }

        @Override
        public MessagesPath as(Name alias) {
            return new MessagesPath(alias, this);
        }

        @Override
        public MessagesPath as(Table<?> alias) {
            return new MessagesPath(alias.getQualifiedName(), this);
        }
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public Identity<MessagesRecord, Integer> getIdentity() {
        return (Identity<MessagesRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<MessagesRecord> getPrimaryKey() {
        return Keys.MESSAGES_PKEY;
    }

    @Override
    public List<ForeignKey<MessagesRecord, ?>> getReferences() {
        return Arrays.asList(Keys.MESSAGES__FK_USER);
    }

    private transient UsersPath _users;

    /**
     * Get the implicit join path to the <code>public.users</code> table.
     */
    public UsersPath users() {
        if (_users == null)
            _users = new UsersPath(this, Keys.MESSAGES__FK_USER, null);

        return _users;
    }

    @Override
    public Messages as(String alias) {
        return new Messages(DSL.name(alias), this);
    }

    @Override
    public Messages as(Name alias) {
        return new Messages(alias, this);
    }

    @Override
    public Messages as(Table<?> alias) {
        return new Messages(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Messages rename(String name) {
        return new Messages(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Messages rename(Name name) {
        return new Messages(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Messages rename(Table<?> name) {
        return new Messages(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Messages where(Condition condition) {
        return new Messages(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Messages where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Messages where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Messages where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Messages where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Messages where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Messages where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Messages where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Messages whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Messages whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
