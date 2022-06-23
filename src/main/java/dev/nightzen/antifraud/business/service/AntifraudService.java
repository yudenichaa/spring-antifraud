package dev.nightzen.antifraud.business.service;

import dev.nightzen.antifraud.business.entity.CardLimits;
import dev.nightzen.antifraud.business.entity.StolenCard;
import dev.nightzen.antifraud.business.entity.SuspiciousIp;
import dev.nightzen.antifraud.business.entity.Transaction;
import dev.nightzen.antifraud.constants.Regexp;
import dev.nightzen.antifraud.constants.TransactionValidity;
import dev.nightzen.antifraud.persistance.CardLimitsRepository;
import dev.nightzen.antifraud.persistance.StolenCardRepository;
import dev.nightzen.antifraud.persistance.SuspiciousIpRepository;
import dev.nightzen.antifraud.persistance.TransactionRepository;
import dev.nightzen.antifraud.presentation.dto.AddFeedbackRequestDto;
import dev.nightzen.antifraud.presentation.dto.TransactionCheckResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import dev.nightzen.antifraud.utils.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AntifraudService {
    @Autowired
    SuspiciousIpRepository suspiciousIpRepository;

    @Autowired
    StolenCardRepository stolenCardRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    CardLimitsRepository cardLimitsRepository;

    public TransactionCheckResponseDto checkTransaction(Transaction transaction) {
        TransactionValidity checkResult = null;
        List<String> checkInfo = new ArrayList<>();

        if (!Card.isValid(transaction.getNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        checkResult = checkInStolenCards(checkResult, transaction, checkInfo);
        checkResult = checkInSuspiciousIps(checkResult, transaction, checkInfo);
        checkResult = checkRegionCorrelation(checkResult, transaction, checkInfo);
        checkResult = checkIpCorrelation(checkResult, transaction, checkInfo);
        checkResult = checkCardLimits(checkResult, transaction, checkInfo);
        transaction.setResult(checkResult);
        transactionRepository.save(transaction);
        checkInfo.sort(String::compareTo);
        return new TransactionCheckResponseDto(
                checkResult,
                String.join(", ", checkInfo)
        );
    }

    private TransactionValidity checkInStolenCards(TransactionValidity checkResult,
                                                   Transaction transaction,
                                                   List<String> checkInfo) {
        if (stolenCardRepository.findByNumber(transaction.getNumber()).isPresent()) {
            checkInfo.add("card-number");
            return TransactionValidity.PROHIBITED;
        }

        return checkResult;
    }

    private TransactionValidity checkInSuspiciousIps(TransactionValidity checkResult,
                                                     Transaction transaction,
                                                     List<String> checkInfo) {
        if (suspiciousIpRepository.findByIp(transaction.getIp()).isPresent()) {
            checkInfo.add("ip");
            return TransactionValidity.PROHIBITED;
        }

        return checkResult;
    }

    private TransactionValidity checkRegionCorrelation(TransactionValidity checkResult,
                                                       Transaction transaction,
                                                       List<String> checkInfo) {
        Long transactionsWithDistinctRegionCount =
                transactionRepository.getTransactionsWithDistinctRegionCount(
                        transaction.getRegion(),
                        transaction.getNumber(),
                        transaction.getDate().minusHours(1),
                        transaction.getDate()
                );

        if (transactionsWithDistinctRegionCount > 1) {
            checkInfo.add("region-correlation");

            if (transactionsWithDistinctRegionCount == 2) {
                return TransactionValidity.MANUAL_PROCESSING;
            } else {
                return TransactionValidity.PROHIBITED;
            }
        }

        return checkResult;
    }

    private TransactionValidity checkIpCorrelation(TransactionValidity checkResult,
                                                   Transaction transaction,
                                                   List<String> checkInfo) {
        Long transactionsWithDistinctIpCount =
                transactionRepository.getTransactionsWithDistinctIpCount(
                        transaction.getIp(),
                        transaction.getNumber(),
                        transaction.getDate().minusHours(1),
                        transaction.getDate()
                );

        if (transactionsWithDistinctIpCount > 1) {
            checkInfo.add("ip-correlation");

            if (transactionsWithDistinctIpCount == 2) {
                return TransactionValidity.MANUAL_PROCESSING;
            } else {
                return TransactionValidity.PROHIBITED;
            }
        }

        return checkResult;
    }

    private TransactionValidity checkCardLimits(TransactionValidity checkResult,
                                                Transaction transaction,
                                                List<String> checkInfo) {
        CardLimits cardLimits = getCardLimits(transaction.getNumber());

        if (transaction.getAmount() > cardLimits.getManualLimit()) {
            checkInfo.add("amount");
            return TransactionValidity.PROHIBITED;
        }

        if (checkResult == null) {
            if (transaction.getAmount() <= cardLimits.getAllowedLimit()) {
                checkInfo.add("none");
                return TransactionValidity.ALLOWED;
            } else {
                checkInfo.add("amount");
                return TransactionValidity.MANUAL_PROCESSING;
            }
        }

        return checkResult;
    }

    public Transaction addFeedback(AddFeedbackRequestDto addFeedbackRequestDto) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(addFeedbackRequestDto.getTransactionId());

        if (optionalTransaction.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Transaction transaction = optionalTransaction.get();

        if (transaction.getFeedback() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        if (transaction.getResult() == addFeedbackRequestDto.getFeedback()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        transaction.setFeedback(addFeedbackRequestDto.getFeedback());
        updateCardLimits(transaction);
        return transactionRepository.save(transaction);
    }

    public Iterable<Transaction> getTransactionsHistory() {
        return transactionRepository.findByOrderByIdAsc();
    }

    public List<Transaction> getTransactionHistory(String cardNumber) {
        if (!Card.isValid(cardNumber)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        List<Transaction> transactions = transactionRepository.findByNumberOrderByIdAsc(cardNumber);

        if (transactions.size() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return transactions;
    }

    private void updateCardLimits(Transaction transaction) {
        CardLimits cardLimits = getCardLimits(transaction.getNumber());

        if (transaction.getResult() == TransactionValidity.ALLOWED) {
            if (transaction.getFeedback() == TransactionValidity.MANUAL_PROCESSING) {
                cardLimits.setAllowedLimit(
                        decreaseLimit(cardLimits.getAllowedLimit(), transaction.getAmount()));
            } else {
                cardLimits.setAllowedLimit(
                        decreaseLimit(cardLimits.getAllowedLimit(), transaction.getAmount()));
                cardLimits.setManualLimit(
                        decreaseLimit(cardLimits.getManualLimit(), transaction.getAmount()));
            }
        } else if (transaction.getResult() == TransactionValidity.MANUAL_PROCESSING) {
            if (transaction.getFeedback() == TransactionValidity.ALLOWED) {
                cardLimits.setAllowedLimit(
                        increaseLimit(cardLimits.getAllowedLimit(), transaction.getAmount()));
            } else {
                cardLimits.setManualLimit(
                        decreaseLimit(cardLimits.getManualLimit(), transaction.getAmount()));
            }
        } else {
            if (transaction.getFeedback() == TransactionValidity.ALLOWED) {
                cardLimits.setAllowedLimit(
                        increaseLimit(cardLimits.getAllowedLimit(), transaction.getAmount()));
                cardLimits.setManualLimit(
                        increaseLimit(cardLimits.getManualLimit(), transaction.getAmount()));
            } else {
                cardLimits.setManualLimit(
                        increaseLimit(cardLimits.getManualLimit(), transaction.getAmount()));
            }
        }

        cardLimitsRepository.save(cardLimits);
    }

    public SuspiciousIp addSuspiciousIp(SuspiciousIp suspiciousIp) {
        if (suspiciousIpRepository.findByIp(suspiciousIp.getIp()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        return suspiciousIpRepository.save(suspiciousIp);
    }

    public void deleteSuspiciousIp(String ip) {
        if (!ip.matches(Regexp.ip)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Optional<SuspiciousIp> suspiciousIp = suspiciousIpRepository.findByIp(ip);

        if (suspiciousIp.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        suspiciousIpRepository.delete(suspiciousIp.get());
    }

    public Iterable<SuspiciousIp> getSuspiciousIpList() {
        return suspiciousIpRepository.findByOrderByIdAsc();
    }

    public StolenCard addStolenCard(StolenCard stolenCard) {
        if (!Card.isValid(stolenCard.getNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (stolenCardRepository.findByNumber(stolenCard.getNumber()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        return stolenCardRepository.save(stolenCard);
    }

    public void deleteStolenCard(String number) {
        if (!Card.isValid(number)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Optional<StolenCard> stolenCard = stolenCardRepository.findByNumber(number);

        if (stolenCard.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        stolenCardRepository.delete(stolenCard.get());
    }

    public Iterable<StolenCard> getStolenCardList() {
        return stolenCardRepository.findByOrderByIdAsc();
    }

    private CardLimits getCardLimits(String cardNumber) {
        Optional<CardLimits> optionalCardLimits = cardLimitsRepository.findByNumber(cardNumber);

        if (optionalCardLimits.isPresent()) {
            return optionalCardLimits.get();
        } else {
            CardLimits cardLimits = new CardLimits();
            cardLimits.setNumber(cardNumber);
            return cardLimitsRepository.save(cardLimits);
        }
    }

    private long increaseLimit(Long currentLimit, Long transactionAmount) {
        return (long) Math.ceil(0.8 * currentLimit + 0.2 * transactionAmount);
    }

    private long decreaseLimit(Long currentLimit, Long transactionAmount) {
        return (long) Math.ceil(0.8 * currentLimit - 0.2 * transactionAmount);
    }
}
