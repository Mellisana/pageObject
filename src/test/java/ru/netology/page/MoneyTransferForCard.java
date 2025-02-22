package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Selenide.$;

public class MoneyTransferForCard {
    private SelenideElement amountField = $("[data-test-id=amount] .input__control");
    private SelenideElement fromField = $("[data-test-id=from] .input__control");
    private SelenideElement transferButton = $("[data-test-id=action-transfer]");

    public MoneyTransferForCard() {
        amountField.shouldBe(Condition.visible);
    }

    public DashboardPage makeMoneyTransfer(double sum, DataHelper.CardInfo cardInfo) {
        // Проверяем, является ли сумма целым числом
        if (sum % 1 == 0) {
            // Если сумма целая, преобразуем в строку без запятой
            amountField.setValue(String.valueOf((int) sum));
        } else {
            // Если сумма дробная, преобразуем в строку с запятой
            amountField.setValue(String.format("%.2f", sum).replace('.', ','));
        }
        fromField.setValue(cardInfo.getCardNumber());
        transferButton.click();
        return new DashboardPage();
    }

    public String getErrorMessage() {
        SelenideElement errorElement = $("#error-message");

        if (errorElement.exists()) {
            return errorElement.text();
        } else {
            return "";
        }
    }
}