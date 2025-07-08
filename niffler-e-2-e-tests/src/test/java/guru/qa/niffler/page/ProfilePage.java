package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.utils.ScreenDiffResult;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class ProfilePage extends BasePage<ProfilePage> {

  private final SelenideElement archiveCategoriesToggle = $("input.MuiSwitch-input");
  private final ElementsCollection categories = $$(".MuiChip-label");
  private final SelenideElement avatar = $(".MuiAvatar-img");
  private final SelenideElement pictureInput = $("input[type='file']");

  @Nonnull
  public ProfilePage switchArchivedCategories() {
    archiveCategoriesToggle.click();
    return this;
  }

  @Nonnull
  public ProfilePage checkCategoryExist(String categoryName) {
    categories.find(text(categoryName)).shouldBe(visible);
    return this;
  }

  @Nonnull
  public ProfilePage checkAvatar(BufferedImage expected) throws IOException {
    Selenide.sleep(3000);
    BufferedImage actual = ImageIO.read(Objects.requireNonNull(avatar.screenshot()));
    assertFalse(new ScreenDiffResult(actual, expected));
    return this;
  }

  @Nonnull
  public ProfilePage uploadAvatar(String path) {
    pictureInput.uploadFromClasspath(path);
    return this;
  }
}
