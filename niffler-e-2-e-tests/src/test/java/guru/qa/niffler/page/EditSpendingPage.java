package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class EditSpendingPage extends BasePage<EditSpendingPage> {

  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement submitBtn = $("#save");
  private final SelenideElement amountInput = $("#amount");

  @Nonnull
  public EditSpendingPage editDescription(String description) {
    descriptionInput.setValue(description);
    return this;
  }

  @Nonnull
  public EditSpendingPage editAmount(double amount) {
    amountInput.setValue(String.format("%.2f", amount));
    return this;
  }

  @Nonnull
  public MainPage save() {
    submitBtn.click();
    return new MainPage();
  }
}
