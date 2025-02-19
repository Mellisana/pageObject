package ru.netology.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.getFirstCardInfo;
import static ru.netology.data.DataHelper.getSecondCardInfo;

public class MoneyTransferTest {
    @BeforeEach
    public void setup() {
//        System.setProperty("webdriver.chrome.driver", "C:\\Users\\User\\tmp\\chromedriver.exe");
//
//        WebDriver driver = new ChromeDriver();
        open("http://localhost:9999");
    }


    DashboardPage login() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        return verificationPage.validVerify(verificationCode);
    }

    @Test
    void shouldTransferFromTheFirstCardToTheSecond() {
        DashboardPage dashboardPage = login();
        var firstCard = getFirstCardInfo();
        var secondCard = getSecondCardInfo();
        int sum = 1500;
        var balanceForFirstCard = dashboardPage.getCardBalance(firstCard) - sum;
        var balanceForSecondCard = dashboardPage.getCardBalance(secondCard) + sum;
        var moneytransferForCard = dashboardPage.choosCardForTransfer(secondCard);
        dashboardPage = moneytransferForCard.makeMoneyTransfer(Integer.parseInt(String.valueOf(sum)), firstCard);
        var actualBalanceForFirstCard = dashboardPage.getCardBalance(firstCard);
        var actualBalanceForSecondCard = dashboardPage.getCardBalance(secondCard);
        assertEquals(balanceForFirstCard, actualBalanceForFirstCard);
        assertEquals(balanceForSecondCard, actualBalanceForSecondCard);

    }

    @Test
    void shouldTransferFromTheSecondCardToTheFirst() {
        DashboardPage dashboardPage = login();
        var firstCard = getFirstCardInfo();
        var secondCard = getSecondCardInfo();
        int sum = 2500;
        var balanceForFirstCard = dashboardPage.getCardBalance(secondCard) - sum;
        var balanceForSecondCard = dashboardPage.getCardBalance(firstCard) + sum;
        var moneytransferForCard = dashboardPage.choosCardForTransfer(firstCard);
        dashboardPage = moneytransferForCard.makeMoneyTransfer(Integer.parseInt(String.valueOf(sum)), secondCard);
        var actualBalanceForFirstCard = dashboardPage.getCardBalance(secondCard);
        var actualBalanceForSecondCard = dashboardPage.getCardBalance(firstCard);
        assertEquals(balanceForFirstCard, actualBalanceForFirstCard);
        assertEquals(balanceForSecondCard, actualBalanceForSecondCard);

    }

    @Test
    void shouldTransferFromTheSecondCardToTheFirstOverLimit() {
        DashboardPage dashboardPage = login();
        var firstCard = getFirstCardInfo();
        var secondCard = getSecondCardInfo();
        int sum = 22000;
        var balanceForFirstCard = dashboardPage.getCardBalance(secondCard) - sum;
        var balanceForSecondCard = dashboardPage.getCardBalance(firstCard) + sum;
        var moneytransferForCard = dashboardPage.choosCardForTransfer(firstCard);
        dashboardPage = moneytransferForCard.makeMoneyTransfer(Integer.parseInt(String.valueOf(sum)), secondCard);
        var actualBalanceForFirstCard = dashboardPage.getCardBalance(secondCard);
        var actualBalanceForSecondCard = dashboardPage.getCardBalance(firstCard);
        assertEquals(balanceForFirstCard, actualBalanceForFirstCard);
        assertEquals(balanceForSecondCard, actualBalanceForSecondCard);

    }

    @Test
    void shouldAllowAnyAmountTransfer() {
        DashboardPage dashboardPage = login();
        var firstCard = getFirstCardInfo();
        var secondCard = getSecondCardInfo();
        int sum = 11000;
        var moneytransferForCard = dashboardPage.choosCardForTransfer(secondCard);
        dashboardPage = moneytransferForCard.makeMoneyTransfer(Integer.parseInt(String.valueOf(sum)), firstCard);
        var actualBalanceForFirstCard = dashboardPage.getCardBalance(firstCard);
        var actualBalanceForSecondCard = dashboardPage.getCardBalance(secondCard);
        assertEquals(actualBalanceForFirstCard, actualBalanceForSecondCard);
    }
}