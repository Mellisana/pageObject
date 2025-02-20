package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.val;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DashboardPage {
    private SelenideElement heading = $("[data-test-id=dashboard]");
    private ElementsCollection cards =

            $$(".list__item div");
    private final String balanceStart = "баланс: ";
    private final String balanceFinish = " р.";

    public DashboardPage() {
        heading.shouldBe(visible);
    }

    public int getCardBalance(DataHelper.CardInfo cardInfo) {
        var text = cards.findBy(Condition.text(cardInfo.getCardNumber().substring(12, 16)))
                .getText();
        return extractBalance(text);
    }

    public MoneyTransferForCard chooseCardForTransfer(DataHelper.CardInfo cardInfo) {
        cards.findBy(Condition.text(cardInfo.getCardNumber().substring(12, 16)))
                .$("[data-test-id=action-deposit]")
                .click();
        return new MoneyTransferForCard();
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