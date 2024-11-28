package com.diplom.cloudstorage.exceptions;

import lombok.Getter;

@Getter
public class CrudExceptions extends RuntimeException {
    private int id;

    public CrudExceptions(String message) {
        super(message);
    }

    public CrudExceptions(String message, Long id) {
        super(message);
        this.id = id.intValue();
    }
}
