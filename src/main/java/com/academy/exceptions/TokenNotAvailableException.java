package com.academy.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenNotAvailableException extends RuntimeException{
   String message;
}
