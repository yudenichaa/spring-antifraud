package dev.nightzen.antifraud.business.entity;

import dev.nightzen.antifraud.constants.Regexp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardLimits {
    @Id
    @GeneratedValue
    Long id;

    @Column
    @NotNull
    @Pattern(regexp = Regexp.cardNumber)
    String number;

    @Column
    Long allowedLimit = 200L;

    @Column
    Long manualLimit = 1500L;
}
