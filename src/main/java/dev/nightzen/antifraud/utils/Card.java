package dev.nightzen.antifraud.utils;

import dev.nightzen.antifraud.constants.Regexp;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Card {
    public static boolean isValid(String number) {
        if (!number.matches(Regexp.cardNumber)) {
            return false;
        }

        List<Integer> digits = Stream.of(number.split(""))
                .map(Integer::new)
                .collect(Collectors.toList());
        return LuhnCheck(digits);
    }

    private static boolean LuhnCheck(List<Integer> digits) {
        int sum = IntStream.range(0, digits.size() - 1)
                .mapToObj(idx -> (idx + 1) % 2 == 0 ? digits.get(idx) : digits.get(idx) * 2)
                .mapToInt(number -> number > 9 ? number - 9 : number)
                .sum() + digits.get(digits.size() - 1);
        return sum % 10 == 0;
    }
}
