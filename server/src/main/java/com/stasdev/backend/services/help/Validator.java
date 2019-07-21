package com.stasdev.backend.services.help;

import com.stasdev.backend.entitys.Employee;

public interface Validator<E> {
    void validate(E entity);
}
