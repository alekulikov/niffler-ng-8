package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage {

  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement submitBtn = $("#save");
  private final SelenideElement amountInput = $("#amount");

  public EditSpendingPage editDescription(String description) {
    descriptionInput.setValue(description);
    return this;
  }

  public EditSpendingPage editAmount(double amount) {
    amountInput.setValue(String.format("%.2f", amount));
    return this;
  }

  public MainPage save() {
    submitBtn.click();
    return new MainPage();
  }
}
