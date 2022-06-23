package dev.nightzen.antifraud.constants;

public class Regexp {
    private static final String ipPart = "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[1-5])";
    public static final String ip = ipPart + "\\." + ipPart + "\\." + ipPart + "\\." + ipPart;
    public static final String cardNumber = "\\d{16}";
}
