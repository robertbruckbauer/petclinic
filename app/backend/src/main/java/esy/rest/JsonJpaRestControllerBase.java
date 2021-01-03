package esy.rest;

import esy.json.JsonJpaEntity;
import io.micrometer.core.lang.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public abstract class JsonJpaRestControllerBase<V extends JsonJpaEntity<V>> {

    private final ApplicationEventPublisher eventPublisher;

    private final TransactionTemplate transactionTemplate;

    @HandleBeforeCreate
    public final void beforeCreate(@NonNull final V value) {
        log.info("RECEIVED [{}]; creating ...", value);
        beforeCreateTransaction(value.verify()).ifPresent(transactionTemplate::execute);
        log.info("ACCEPTED [{}]", value);
    }

    // tag::beforeCreateTransaction[]
    protected Optional<TransactionCallback<Void>> beforeCreateTransaction(@NonNull final V value) {
        return Optional.empty();
    }
    // end::beforeCreateTransaction[]

    @HandleAfterCreate
    public final void afterCreate(@NonNull final V value) {
        afterCreateTransaction(value).ifPresent(transactionTemplate::execute);
        eventPublisher.publishEvent(value);
        log.info("CREATED [{}]", value);
    }

    // tag::afterCreateTransaction[]
    protected Optional<TransactionCallback<Void>> afterCreateTransaction(@NonNull final V value) {
        return Optional.empty();
    }
    // end::afterCreateTransaction[]

    @HandleBeforeSave
    public final void beforeSave(@NonNull final V value) {
        log.info("RECEIVED [{}]; updating ...", value);
        beforeSaveTransaction(value.verify()).ifPresent(transactionTemplate::execute);
        log.info("ACCEPTED [{}]", value);
    }

    // tag::beforeSaveTransaction[]
    protected Optional<TransactionCallback<Void>> beforeSaveTransaction(@NonNull final V value) {
        return Optional.empty();
    }
    // end::beforeSaveTransaction[]

    @HandleAfterSave
    public final void afterSave(@NonNull final V value) {
        afterSaveTransaction(value).ifPresent(transactionTemplate::execute);
        eventPublisher.publishEvent(value);
        log.info("UPDATED [{}]", value);
    }

    // tag::afterSaveTransaction[]
    protected Optional<TransactionCallback<Void>> afterSaveTransaction(@NonNull final V value) {
        return Optional.empty();
    }
    // end::afterSaveTransaction[]

    @HandleBeforeLinkSave
    public final void beforeLinkSave(@NonNull final V value, final Object rel) {
        log.info("RECEIVED [{}]; linking with [{}] ...", value, rel);
        beforeLinkSaveTransaction(value.verify(), rel).ifPresent(transactionTemplate::execute);
        log.info("ACCEPTED [{}]", value);
    }

    // tag::beforeLinkSaveTransaction[]
    protected Optional<TransactionCallback<Void>> beforeLinkSaveTransaction(@NonNull final V value, final Object rel) {
        return Optional.empty();
    }
    // end::beforeLinkSaveTransaction[]

    @HandleAfterLinkSave
    public final void afterLinkSave(@NonNull final V value, final Object rel) {
        afterLinkSaveTransaction(value, rel).ifPresent(transactionTemplate::execute);
        eventPublisher.publishEvent(value);
        log.info("UPDATED [{}]", value);
    }

    // tag::afterLinkSaveTransaction[]
    protected Optional<TransactionCallback<Void>> afterLinkSaveTransaction(@NonNull final V value, final Object rel) {
        return Optional.empty();
    }
    // end::afterLinkSaveTransaction[]

    @HandleBeforeDelete
    public final void beforeDelete(@NonNull final V value) {
        log.info("RECEIVED [{}]; deleting ...", value);
        beforeDeleteTransaction(value.verify()).ifPresent(transactionTemplate::execute);
        log.info("ACCEPTED [{}]", value);
    }

    // tag::beforeDeleteTransaction[]
    protected Optional<TransactionCallback<Void>> beforeDeleteTransaction(@NonNull final V value) {
        return Optional.empty();
    }
    // end::beforeDeleteTransaction[]

    @HandleAfterDelete
    public final void afterDelete(@NonNull final V value) {
        afterDeleteTransaction(value).ifPresent(transactionTemplate::execute);
        eventPublisher.publishEvent(value);
        log.info("DELETED [{}]", value);
    }

    // tag::afterDeleteTransaction[]
    protected Optional<TransactionCallback<Void>> afterDeleteTransaction(@NonNull final V value) {
        return Optional.empty();
    }
    // end::afterDeleteTransaction[]

    @HandleBeforeLinkDelete
    public final void beforeLinkDelete(@NonNull final V value, final Object rel) {
        log.info("RECEIVED [{}]; unlinking [{}] ...", value, rel);
        beforeLinkDeleteTransaction(value.verify(), rel).ifPresent(transactionTemplate::execute);
        log.info("ACCEPTED [{}]", value);
    }

    // tag::beforeLinkDeleteTransaction[]
    protected Optional<TransactionCallback<Void>> beforeLinkDeleteTransaction(@NonNull final V value, final Object rel) {
        return Optional.empty();
    }
    // end::beforeLinkDeleteTransaction[]

    @HandleAfterLinkDelete
    public final void afterLinkDelete(@NonNull final V value, final Object rel) {
        afterLinkDeleteTransaction(value, rel).ifPresent(transactionTemplate::execute);
        eventPublisher.publishEvent(value);
        log.info("DELETED [{}]", value);
    }

    // tag::afterLinkDeleteTransaction[]
    protected Optional<TransactionCallback<Void>> afterLinkDeleteTransaction(@NonNull final V value, final Object rel) {
        return Optional.empty();
    }
    // end::afterLinkDeleteTransaction[]
}
