package com.hexagon.studentservice.service;

import java.util.List;

public interface ServiceInterface<E, ID, T> {
    E create (E e);
    E getById (ID id);
    List<E> getAll();
    E update (E e);
    void delete(ID id);

}