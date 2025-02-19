package ru.netology.page;


import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Selenide.$;
import static java.lang.Thread.sleep;

public class MoneyTransferForCard {
    private SelenideElement amountField = $("[data-test-id=amount] .input__control");
    private SelenideElement fromField = $("[data-test-id=from] .input__control");
    private SelenideElement transferButton = $("[data-test-id=action-transfer]");

    public MoneyTransferForCard() {

        amountField.shouldBe(Condition.visible);
    }

    public DashboardPage makeMoneyTransfer(int sum, DataHelper.CardInfo cardInfo) {
        amountField.setValue(String.valueOf(sum));
        fromField.setValue(cardInfo.getCardNumber());
        transferButton.click();
        return new DashboardPage();
    }
}