package ru.netology.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;

import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.*;

public class MoneyTransferTest {
    @BeforeEach
    public void setup() {

        ChromeOptions options = new ChromeOptions();
        options. addArguments("--start-maximized");
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("credentials_enable_service", false);
        prefs.put("password_manager_enabled", false);
        options. setExperimentalOption("prefs", prefs);
        Configuration. browserCapabilities = options;
        open("http://localhost:9999");

    }

    private DashboardPage login() {
        var loginPage = new LoginPage();
        var authInfo = getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = getVerificationCodeFor(authInfo);
        return verificationPage.validVerify(verificationCode);
    }

    @Test
    void shouldTransferFromFirstCardToSecond() {
        DashboardPage dashboardPage = login();
        var firstCard = getFirstCardInfo();
        var secondCard = getSecondCardInfo();
        int sum = 1500;

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
        DashboardPage dashboardPage = login();
        var firstCard = getFirstCardInfo();
        var secondCard = getSecondCardInfo();
        int sum = 2500;

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
    void shouldTransferFromSecondCardToFirstOverLimit() {
        DashboardPage dashboardPage = login();
        var firstCard = getFirstCardInfo();
        var secondCard = getSecondCardInfo();
        int sum = 22000;

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
    void shouldTransferFromSecondCardToFirstZero() {
        DashboardPage dashboardPage = login();
        var firstCard = getFirstCardInfo();
        var secondCard = getSecondCardInfo();
        int sum = 0;

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
    void shouldTransferCope() {
        DashboardPage dashboardPage = login();
        var firstCard = getFirstCardInfo();
        var secondCard = getSecondCardInfo();
        double sum = 10.02;

        var expectedFirstCardBalance = dashboardPage.getCardBalance(firstCard) + sum;
        var expectedSecondCardBalance = dashboardPage.getCardBalance(secondCard) - sum;

        var moneyTransferForCard = dashboardPage.chooseCardForTransfer(firstCard);
        dashboardPage = moneyTransferForCard.makeMoneyTransfer((int) sum, secondCard);

        var actualFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        var actualSecondCardBalance = dashboardPage.getCardBalance(secondCard);

        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }
}