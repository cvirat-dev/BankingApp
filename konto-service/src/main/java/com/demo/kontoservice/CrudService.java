package com.demo.kontoservice;

import java.util.List;

public interface CrudService<TEntity, TRequest> {
    List<TEntity> getAll();
    TEntity get(Long id);
    TEntity create(TRequest request);
    void delete(Long id);
}
