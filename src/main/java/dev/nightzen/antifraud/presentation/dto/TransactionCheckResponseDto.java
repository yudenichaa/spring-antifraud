package dev.nightzen.antifraud.presentation.dto;

import dev.nightzen.antifraud.constants.TransactionValidity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCheckResponseDto {

    TransactionValidity result;
    String info;
}
