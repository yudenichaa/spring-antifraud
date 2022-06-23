package dev.nightzen.antifraud.business.entity;

import dev.nightzen.antifraud.constants.Regexp;
import dev.nightzen.antifraud.constants.TransactionValidity;
import dev.nightzen.antifraud.constants.WorldRegion;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue
    Long id;

    @JsonGetter("transactionId")
    public Long getId() {
        return id;
    }

    @Column
    @NotNull
    @Min(1)
    private Long amount;

    @Column
    @NotNull
    @Pattern(regexp = Regexp.cardNumber)
    String number;

    @Column
    @NotNull
    @Pattern(regexp = Regexp.ip)
    private String ip;

    @Column
    @NotNull
    WorldRegion region;

    @Column
    @NotNull
    LocalDateTime date;

    @Column
    TransactionValidity result;

    @Column
    TransactionValidity feedback;

    @JsonProperty("feedback")
    public String getFeedbackString() {
        return feedback == null ? "" : feedback.name();
    }
}
