package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.jupiter.extension.ScreenShotTestExtension;
import guru.qa.niffler.model.Bubble;
import guru.qa.niffler.utils.ScreenDiffResult;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.condition.StatConditions.bubbles;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class StatComponent {

  public final SelenideElement self = $("#stat");
  private final ElementsCollection bubbles = self.$("#legend-container").$$("li");
  private final SelenideElement statisticDiagram = $("canvas[role='img']");

  @SneakyThrows
  public StatComponent checkStatisticDiagram(BufferedImage expected) {
    Selenide.sleep(3000);
    assertFalse(new ScreenDiffResult(
            statisticDiagramScreenshot(),
            expected
        ),
        ScreenShotTestExtension.ASSERT_SCREEN_MESSAGE);
    return this;
  }

  public StatComponent checkBubbles(Bubble... expectedBubbles) {
    bubbles.should(bubbles(expectedBubbles));
    return this;
  }

  private BufferedImage statisticDiagramScreenshot() throws IOException {
    return ImageIO.read(requireNonNull(statisticDiagram.screenshot()));
  }
}
