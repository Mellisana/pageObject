package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DashboardPage {
    private SelenideElement heading = $("[data-test-id=dashboard]");
    private SelenideElement reloadButton = $("[data-test-id=action-reload]");
    private ElementsCollection cards = $$(".list__item div");
    private final String balanceStart = "баланс: ";
    private final String balanceFinish = " р.";

    public DashboardPage() {
        heading.shouldBe(visible);
    }

    public int getCardBalance(DataHelper.CardInfo cardInfo) {
        var text = getCard(cardInfo).getText();
        return extractBalance(text);
    }

    private SelenideElement getCard(DataHelper.CardInfo cardInfo) {
        return cards.findBy(Condition.text(cardInfo.getCardNumber().substring(12, 16)));
    }

    public MoneyTransferForCard selectCardToTransfer(DataHelper.CardInfo cardInfo) {
        getCard(cardInfo).$("button").click();
        return new MoneyTransferForCard();
    }

    public void reloadDashboardPage() {
        reloadButton.click();
        heading.shouldBe(visible);
    }

    private int extractBalance(String text) {
        if (!text.contains(balanceStart) || !text.contains(balanceFinish)) {
            throw new IllegalArgumentException("Неверная структура текста баланса.");
        }

        var startIndex = text.indexOf(balanceStart) + balanceStart.length();
        var endIndex = text.indexOf(balanceFinish);
        var balanceString = text.substring(startIndex, endIndex);
        return Integer.parseInt(balanceString.trim());
    }
}