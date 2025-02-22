package ru.netology.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;

import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.netology.data.DataHelper.*;

public class MoneyTransferTest {
    private DashboardPage dashboardPage;
    private DataHelper.CardInfo firstCard;
    private DataHelper.CardInfo secondCard;

    @BeforeEach
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);
        Configuration.browserCapabilities = options;
        open("http://localhost:9999");

        login();
        firstCard = getFirstCardInfo();
        secondCard = getSecondCardInfo();
    }

    private void login() {
        var loginPage = new LoginPage();
        var authInfo = getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
    }

    private void assertBalances(int expectedFirstCardBalance, int expectedSecondCardBalance) {
        int actualFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        int actualSecondCardBalance = dashboardPage.getCardBalance(secondCard);
        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    void shouldTransferFromFirstCardToSecond() {
        int currentBalance = dashboardPage.getCardBalance(firstCard);
        int sum = Math.min(currentBalance, (int) Math.floor(currentBalance * 0.9)); // Переводим целое число
        var expectedFirstCardBalance = dashboardPage.getCardBalance(firstCard) - sum;
        var expectedSecondCardBalance = dashboardPage.getCardBalance(secondCard) + sum;

        var moneyTransferForCard = dashboardPage.chooseCardForTransfer(secondCard);
        dashboardPage = moneyTransferForCard.makeMoneyTransfer(sum, firstCard);

        var actualFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        var actualSecondCardBalance = dashboardPage.getCardBalance(secondCard);

        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    void shouldTransferFromSecondCardToFirst() {
        int currentBalance = dashboardPage.getCardBalance(secondCard);
        int sum = Math.min(currentBalance, (int) Math.floor(currentBalance * 0.8)); // Переводим целое число
        var expectedFirstCardBalance = dashboardPage.getCardBalance(firstCard) + sum;
        var expectedSecondCardBalance = dashboardPage.getCardBalance(secondCard) - sum;

        var moneyTransferForCard = dashboardPage.chooseCardForTransfer(firstCard);
        dashboardPage = moneyTransferForCard.makeMoneyTransfer(sum, secondCard);

        var actualFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        var actualSecondCardBalance = dashboardPage.getCardBalance(secondCard);

        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }



    @Test
    void shouldNotTransferFromSecondCardToFirstZero() {
        int sum = 0; // Пытаемся перевести ноль

        // Сохраняем текущие балансы перед попыткой перевода
        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCard);

        var moneyTransferForCard = dashboardPage.chooseCardForTransfer(firstCard);
        dashboardPage = moneyTransferForCard.makeMoneyTransfer(sum, secondCard);

        // Проверяем наличие ошибки
        String errorMessage = moneyTransferForCard.getErrorMessage();
        assertNotNull(errorMessage, "Ошибка не отображается");
        assertEquals("Введите сумму перевода", errorMessage, "Сообщение об ошибке отсутствует.");

    }

    @Test
    void shouldTransferPartialAmountWithCop() {
        double currentBalance = dashboardPage.getCardBalance(firstCard);

        // Если баланс без копеек, добавляем 0.01 для проверки работы с копейками
        if ((currentBalance % 1) == 0) {
            currentBalance += 0.01;
        }

        double sum = Math.min(currentBalance, Math.round(currentBalance * 0.5 * 100.0) / 100.0); // Переводим с копейками

        double expectedFirstCardBalance = dashboardPage.getCardBalance(firstCard) - sum;
        double expectedSecondCardBalance = dashboardPage.getCardBalance(secondCard) + sum;

        var moneyTransferForCard = dashboardPage.chooseCardForTransfer(secondCard);
        dashboardPage = moneyTransferForCard.makeMoneyTransfer(sum, firstCard);

        assertBalances((int) expectedFirstCardBalance, (int) expectedSecondCardBalance);
    }

    @Test
    void shouldNotTransferFromFirstCardToSecondOverLimit() {
        int currentBalance = dashboardPage.getCardBalance(firstCard);
        int sum = currentBalance + 5000; // Пытаемся перевести больше, чем остаток на первой карте

        // Сохраняем текущие балансы перед попыткой перевода
        int initialFirstCardBalance = currentBalance;
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCard);

        var moneyTransferForCard = dashboardPage.chooseCardForTransfer(secondCard);
        dashboardPage = moneyTransferForCard.makeMoneyTransfer(sum, firstCard);

        // Проверяем наличие ошибки
        String errorMessage = moneyTransferForCard.getErrorMessage();
        assertNotNull(errorMessage, "Ошибка не отображается");
        assertEquals("Операция отклонена, невозможно перевести сумму свыше баланса", errorMessage, "Сообщение об ошибке отсутствует");
    }
}