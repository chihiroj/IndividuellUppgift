package com.example.IndividuellUppgift.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;

/**
 * Common class for formating responses.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class MessageResponseDTO extends RepresentationModel<MessageResponseDTO> {
    private String message;
    private HttpStatus httpStatus;
}
