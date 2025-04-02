package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProfilePage {

  private final SelenideElement archiveCategoriesToggle = $("input.MuiSwitch-input");
  private final ElementsCollection categories = $$(".MuiChip-label");

  public ProfilePage switchArchivedCategories() {
    archiveCategoriesToggle.click();
    return this;
  }

  public ProfilePage checkCategoryExist(String categoryName) {
    categories.find(text(categoryName)).shouldBe(visible);
    return this;
  }
}
