package com.demo.kontoservice;

import java.util.List;

public interface CrudService<TEntity, TCreateRequest> {
    List<TEntity> getAll();
    TEntity get(Long id);
    TEntity create(TCreateRequest request);
    void delete(Long id);
}
