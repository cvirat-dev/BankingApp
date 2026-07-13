package com.demo.kontoservice;

public interface UpdatableCrudService<TEntity, TCreateRequest, TUpdateRequest> extends CrudService<TEntity, TCreateRequest> {
    TEntity update(Long id, TUpdateRequest request);
}
