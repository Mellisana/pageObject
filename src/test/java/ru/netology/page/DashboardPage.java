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
        private ElementsCollection cards = $$(".list__item");
        private final String balanceStart = "баланс: ";
        private final String balanceFinish = " р.";

        public DashboardPage() {
            heading.shouldBe(visible);
        }

        public int getCardBalance(DataHelper.CardInfo cardInfo) {
            var text = cards.findBy(Condition.text(cardInfo.getCardNumber().substring(12, 16))).getText();
            return extractBalance(text);
        }

        public MoneyTransferForCard choosCardForTransfer(DataHelper.CardInfo cardInfo) {
            cards.findBy(Condition.text(cardInfo.getCardNumber().substring(12, 16))).$("[data-test-id=action-deposit]").click();
            return new MoneyTransferForCard();
        }

        private int extractBalance(String text) {
            var start = text.indexOf(balanceStart);
            var finish = text.indexOf(balanceFinish);
            var value = text.substring(start + balanceStart.length(), finish);
            return Integer.parseInt(value);
        }
    }