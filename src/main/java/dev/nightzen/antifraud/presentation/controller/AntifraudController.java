package dev.nightzen.antifraud.presentation.controller;

import dev.nightzen.antifraud.business.entity.StolenCard;
import dev.nightzen.antifraud.business.entity.SuspiciousIp;
import dev.nightzen.antifraud.business.entity.Transaction;
import dev.nightzen.antifraud.business.service.AntifraudService;
import dev.nightzen.antifraud.presentation.dto.AddFeedbackRequestDto;
import dev.nightzen.antifraud.presentation.dto.TransactionCheckResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/antifraud/")
public class AntifraudController {
    @Autowired
    AntifraudService antifraudService;

    @PostMapping("transaction")
    public TransactionCheckResponseDto checkTransaction(@RequestBody @Valid Transaction transaction) {
        return antifraudService.checkTransaction(transaction);
    }

    @PutMapping("transaction")
    public Transaction addFeedback(@RequestBody @Valid AddFeedbackRequestDto addFeedbackRequestDto) {
        return antifraudService.addFeedback(addFeedbackRequestDto);
    }

    @GetMapping("history")
    public Iterable<Transaction> getTransactionsHistory() {
        return antifraudService.getTransactionsHistory();
    }

    @GetMapping("history/{number}")
    public List<Transaction> getTransactionHistory(@PathVariable String number) {
        return antifraudService.getTransactionHistory(number);
    }

    @PostMapping("suspicious-ip")
    public SuspiciousIp addSuspiciousIp(@RequestBody @Valid SuspiciousIp suspiciousIp) {
        return antifraudService.addSuspiciousIp(suspiciousIp);
    }

    @DeleteMapping("suspicious-ip/{ip}")
    public Map<String, String> deleteSuspiciousIp(@PathVariable String ip) {
        antifraudService.deleteSuspiciousIp(ip);
        return Map.of(
                "status",
                "IP " + ip + " successfully removed!"
        );
    }

    @GetMapping("suspicious-ip")
    public Iterable<SuspiciousIp> getSuspiciousIpList() {
        return antifraudService.getSuspiciousIpList();
    }

    @PostMapping("stolencard")
    public StolenCard addStolenCard(@RequestBody @Valid StolenCard stolenCard) {
        return antifraudService.addStolenCard(stolenCard);
    }

    @DeleteMapping("stolencard/{number}")
    public Map<String, String> deleteStolenCard(@PathVariable String number) {
        antifraudService.deleteStolenCard(number);
        return Map.of(
                "status",
                "Card " + number + " successfully removed!"
        );
    }

    @GetMapping("stolencard")
    public Iterable<StolenCard> getStolenCardList() {
        return antifraudService.getStolenCardList();
    }
}
