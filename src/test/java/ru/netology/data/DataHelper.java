package ru.netology.data;

import lombok.Value;

public class DataHelper {
    private DataHelper() {
    }

    @Value
    public static class AuthInfo {
        private String login;
        private String password;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    @Value
    public static class VerificationCode {
        private String code;
    }

    public static VerificationCode getVerificationCodeFor(AuthInfo authInfo) {
        return new VerificationCode("12345");
    }

    @Value
    public static class CardInfo {
        private String cardNumber;
    }

    public static CardInfo getCardInfo(String cardNumber) {
        return new CardInfo(cardNumber);
    }

    public static CardInfo getFirstCardInfo() {
        return getCardInfo("5559000000000001");
    }

    public static CardInfo getSecondCardInfo() {
        return getCardInfo("5559000000000002");
    }

    public static int generateValidAmount(int balance) {
        return Math.abs(balance) / 10;
    }

    public static int generateInvalidAmount(int balance) {
        return Math.abs(balance) + 1;
    }
}