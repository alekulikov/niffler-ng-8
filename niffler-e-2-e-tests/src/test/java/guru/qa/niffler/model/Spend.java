package guru.qa.niffler.model;

import java.time.LocalDate;
import java.util.Objects;

public record Spend(
    String category,
    Double amount,
    CurrencyValues currency,
    String description,
    LocalDate spendDate) {

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Spend spend = (Spend) o;
    return Objects.equals(amount, spend.amount)
        && Objects.equals(category, spend.category)
        && Objects.equals(description, spend.description)
        && Objects.equals(spendDate, spend.spendDate)
        && currency == spend.currency;
  }

  @Override
  public int hashCode() {
    return Objects.hash(category, amount, currency, description, spendDate);
  }

  @Override
  public String toString() {
    return "Spend{" +
        "category='" + category + '\'' +
        ", amount=" + amount +
        ", currency=" + currency +
        ", description='" + description + '\'' +
        ", spendDate=" + spendDate +
        '}';
  }
}
