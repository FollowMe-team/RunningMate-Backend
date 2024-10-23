package com.follow_me.running_mate.global.error.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationErrors {
    private List<ValidationError> errors;
}

