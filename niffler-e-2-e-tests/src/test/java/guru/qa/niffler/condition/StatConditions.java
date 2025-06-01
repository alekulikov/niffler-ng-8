package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.Bubble;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

@ParametersAreNonnullByDefault
public class StatConditions {

  @Nonnull
  public static WebElementCondition bubble(Bubble expectedBubble) {
    return new WebElementCondition(expectedBubble.toString()) {
      @NotNull
      @Override
      public CheckResult check(Driver driver, WebElement element) {
        final Bubble actualBubble = new Bubble(
            Color.fromRgb(element.getCssValue("background-color")),
            element.getText()
        );
        return new CheckResult(
            expectedBubble.equals(actualBubble),
            actualBubble
        );
      }
    };
  }

  @Nonnull
  public static WebElementsCondition bubbles(@Nonnull Bubble... expectedBubbles) {
    return new WebElementsCondition() {

      @NotNull
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        if (ArrayUtils.isEmpty(expectedBubbles)) {
          throw new IllegalArgumentException("No expected bubbles given");
        }
        if (expectedBubbles.length != elements.size()) {
          final String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedBubbles.length, elements.size());
          return rejected(message, elements);
        }

        List<Bubble> actualBubbles = webElementsToBubbles(elements);
        boolean passed = Arrays.asList(expectedBubbles).equals(actualBubbles);
        if (!passed) {
          final String message = String.format(
              "List bubbles mismatch (expected: %s, actual: %s)", Arrays.toString(expectedBubbles), actualBubbles
          );
          return rejected(message, actualBubbles);
        }
        return accepted();
      }

      @Override
      public String toString() {
        return Arrays.toString(expectedBubbles);
      }
    };
  }

  @Nonnull
  public static WebElementsCondition statBubblesInAnyOrder(@Nonnull Bubble... expectedBubbles) {
    return new WebElementsCondition() {

      @NotNull
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        if (ArrayUtils.isEmpty(expectedBubbles)) {
          throw new IllegalArgumentException("No expected bubbles given");
        }
        if (expectedBubbles.length != elements.size()) {
          final String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedBubbles.length, elements.size());
          return rejected(message, elements);
        }

        List<Bubble> actualBubbles = webElementsToBubbles(elements);
        boolean passed = actualBubbles.containsAll(Arrays.asList(expectedBubbles));
        if (!passed) {
          final String message = String.format(
              "List bubbles mismatch (expected: %s, actual: %s)", Arrays.toString(expectedBubbles), actualBubbles
          );
          return rejected(message, actualBubbles);
        }
        return accepted();
      }

      @Override
      public String toString() {
        return Arrays.toString(expectedBubbles);
      }
    };
  }

  @Nonnull
  public static WebElementsCondition statBubblesContains(@Nonnull Bubble... expectedBubbles) {
    return new WebElementsCondition() {

      @NotNull
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        if (ArrayUtils.isEmpty(expectedBubbles)) {
          throw new IllegalArgumentException("No expected bubbles given");
        }

        List<Bubble> actualBubbles = webElementsToBubbles(elements);
        boolean passed = actualBubbles.containsAll(Arrays.asList(expectedBubbles));
        if (!passed) {
          final String message = String.format(
              "List bubbles mismatch (expected: %s, actual: %s)", Arrays.toString(expectedBubbles), actualBubbles
          );
          return rejected(message, actualBubbles);
        }
        return accepted();
      }

      @Override
      public String toString() {
        return Arrays.toString(expectedBubbles);
      }
    };
  }

  private static List<Bubble> webElementsToBubbles(@Nonnull List<WebElement> elements) {
    return elements.stream()
        .map(e -> new Bubble(
            Color.fromRgb(e.getCssValue("background-color")),
            e.getText()))
        .toList();
  }
}
