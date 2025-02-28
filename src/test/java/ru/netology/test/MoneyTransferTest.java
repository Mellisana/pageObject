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
        int sum = currentBalance / 10;
        int expectedFirstCardBalance = currentBalance - sum;
        int expectedSecondCardBalance = dashboardPage.getCardBalance(secondCard) + sum;

        var transferPage = dashboardPage.selectCardToTransfer(secondCard);
        transferPage.makeValidTransfer(String.valueOf(sum), firstCard);

        assertBalances(expectedFirstCardBalance, expectedSecondCardBalance);
    }

    @Test
    void shouldNotTransferFromSecondCardToFirstZero() {
        String sum = "0";

        var transferPage = dashboardPage.selectCardToTransfer(firstCard);
        transferPage.makeTransfer(sum, secondCard);

        transferPage.findErrorMessage("Введите сумму перевода");
    }

    @Test
    void shouldNotTransferFromFirstCardToSecondOverLimit() {
        int currentBalance = dashboardPage.getCardBalance(firstCard);
        int sum = currentBalance + 5000;
        var transferPage = dashboardPage.selectCardToTransfer(secondCard);
        transferPage.makeTransfer(String.valueOf(sum), firstCard);
        transferPage.findErrorMessage("Операция отклонена, невозможно перевести сумму свыше баланса");
    }

    @Test
    void shouldNotTransferInvalidAmountWithZero() {
        String sumString = "02000";

        var transferPage = dashboardPage.selectCardToTransfer(secondCard);
        transferPage.makeTransfer(sumString, firstCard);

        String errorMessage = transferPage.getErrorMessage();
        assertNotNull(errorMessage, "Ошибка не отображается");
        assertEquals("Введите корректную сумму перевода", errorMessage);
    }
}