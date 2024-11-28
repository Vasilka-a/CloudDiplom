package com.diplom.cloudstorage.exceptions;

import lombok.Getter;

@Getter
public class InputDataException extends RuntimeException {
    private int id;

    public InputDataException(String message) {
        super(message);
    }

    public InputDataException(String errorInputData, Long id) {
        super(errorInputData);
        this.id = id.intValue();
    }
}
