package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.utils.ScreenDiffResult;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ProfilePage {

  private final SelenideElement archiveCategoriesToggle = $("input.MuiSwitch-input");
  private final ElementsCollection categories = $$(".MuiChip-label");
  private final SelenideElement avatar = $(".MuiAvatar-img");
  private final SelenideElement pictureInput = $("input[type='file']");

  public ProfilePage switchArchivedCategories() {
    archiveCategoriesToggle.click();
    return this;
  }

  public ProfilePage checkCategoryExist(String categoryName) {
    categories.find(text(categoryName)).shouldBe(visible);
    return this;
  }

  @SneakyThrows
  public ProfilePage checkAvatar(BufferedImage expected) {
    Selenide.sleep(3000);
    BufferedImage actual = ImageIO.read(avatar.screenshot());
    assertFalse(new ScreenDiffResult(actual, expected));
    return this;
  }

  @SneakyThrows
  public ProfilePage uploadAvatar(String path) {
    pictureInput.uploadFromClasspath(path);
    return this;
  }
}
