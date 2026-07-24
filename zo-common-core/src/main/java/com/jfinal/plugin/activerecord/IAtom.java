package com.jfinal.plugin.activerecord;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public interface IAtom {
    /**
     * Place codes here that need transaction support.
     * @return true if you want to commit the transaction otherwise roll back transaction
     */
    boolean run();


    /**
     * 开启事务的数据库别名，默认主数据库
     * @return
     */
    default String db() {
        return DbKit.MAIN_CONFIG_NAME;
    }


    /**
     * The transaction propagation type.
     * <p>Defaults to {@link Propagation#REQUIRED}.
     * @see org.springframework.transaction.interceptor.TransactionAttribute#getPropagationBehavior()
     */
    default Propagation propagation() {
        return Propagation.REQUIRED;
    }

    /**
     * The transaction isolation level.
     * <p>Defaults to {@link Isolation#DEFAULT}.
     * <p>Exclusively designed for use with {@link Propagation#REQUIRED} or
     * {@link Propagation#REQUIRES_NEW} since it only applies to newly started
     * transactions. Consider switching the "validateExistingTransactions" flag to
     * "true" on your transaction manager if you'd like isolation level declarations
     * to get rejected when participating in an existing transaction with a different
     * isolation level.
     * @see org.springframework.transaction.interceptor.TransactionAttribute#getIsolationLevel()
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#setValidateExistingTransaction
     */
    default Isolation isolation() {
        return Isolation.DEFAULT;
    }

    /**
     * The timeout for this transaction (in seconds).
     * <p>Defaults to the default timeout of the underlying transaction system.
     * <p>Exclusively designed for use with {@link Propagation#REQUIRED} or
     * {@link Propagation#REQUIRES_NEW} since it only applies to newly started
     * transactions.
     * @see org.springframework.transaction.interceptor.TransactionAttribute#getTimeout()
     */
    default int timeout() {
        return TransactionDefinition.TIMEOUT_DEFAULT;
    }

    /**
     * A boolean flag that can be set to {@code true} if the transaction is
     * effectively read-only, allowing for corresponding optimizations at runtime.
     * <p>Defaults to {@code false}.
     * <p>This just serves as a hint for the actual transaction subsystem;
     * it will <i>not necessarily</i> cause failure of write access attempts.
     * A transaction manager which cannot interpret the read-only hint will
     * <i>not</i> throw an exception when asked for a read-only transaction
     * but rather silently ignore the hint.
     * @see org.springframework.transaction.interceptor.TransactionAttribute#isReadOnly()
     * @see org.springframework.transaction.support.TransactionSynchronizationManager#isCurrentTransactionReadOnly()
     */
    default boolean readOnly() {
        return false;
    }
}
