package dev.nightzen.antifraud.presentation.dto;

import dev.nightzen.antifraud.constants.TransactionValidity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddFeedbackRequestDto {
    @NotNull
    private Long transactionId;

    private TransactionValidity feedback;
}
