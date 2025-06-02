package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.Spend;
import guru.qa.niffler.model.SpendJson;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

@ParametersAreNonnullByDefault
public class SpendConditions {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);

  @Nonnull
  public static WebElementsCondition spends(@Nonnull SpendJson... expectedSpendJsons) {
    return new WebElementsCondition() {

      @NotNull
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        if (ArrayUtils.isEmpty(expectedSpendJsons)) {
          throw new IllegalArgumentException("No expected spends given");
        }
        if (expectedSpendJsons.length != elements.size()) {
          final String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedSpendJsons.length, elements.size());
          return rejected(message, elements);
        }

        List<Spend> expectedSpends = spendJsonsToSpends(Arrays.asList(expectedSpendJsons));
        List<Spend> actualSpends = webElementsToSpends(elements);
        boolean passed = expectedSpends.equals(actualSpends);
        if (!passed) {
          final String message = String.format(
              "List bubbles mismatch (expected: %s, actual: %s)", expectedSpends, actualSpends
          );
          return rejected(message, actualSpends);
        }
        return accepted();
      }

      @Override
      public String toString() {
        return spendJsonsToSpends(Arrays.asList(expectedSpendJsons)).toString();
      }
    };
  }

  @NotNull
  private static List<Spend> webElementsToSpends(@Nonnull List<WebElement> elements) {
    return elements.stream()
        .map(e -> e.findElements(By.tagName("td")))
        .map(cells -> new Spend(
            cells.get(1).getText(),
            Double.parseDouble(cells.get(2).getText().split(" ")[0]),
            CurrencyValues.fromSymbol(cells.get(2).getText().split(" ")[1]),
            cells.get(3).getText(),
            LocalDate.parse(cells.get(4).getText(), DATE_FORMATTER)))
        .toList();
  }

  @NotNull
  private static List<Spend> spendJsonsToSpends(@Nonnull List<SpendJson> spendJsons) {
    return spendJsons.stream()
        .map(json -> new Spend(
            json.category().name(),
            json.amount(),
            json.currency(),
            json.description(),
            dateToLocalDate(json.spendDate())))
        .toList();
  }

  @NotNull
  private static LocalDate dateToLocalDate(@Nonnull Date date) {
    if (date instanceof java.sql.Date sqlDate) {
      return sqlDate.toLocalDate();
    } else {
      return date.toInstant()
          .atZone(ZoneId.systemDefault())
          .toLocalDate();
    }
  }
}
