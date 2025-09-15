package com.pragati.ecommerce.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class CustomerNotFoundException extends RuntimeException {

    private final String msg;
}
