package guru.qa.niffler.model;

import guru.qa.niffler.condition.Color;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record Bubble(Color color, String text) {

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Bubble bubble = (Bubble) o;
    return color == bubble.color && Objects.equals(text, bubble.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(color, text);
  }

  @NotNull
  @Override
  public String toString() {
    return "Bubble{" +
        "color=" + color +
        ", text='" + text + '\'' +
        '}';
  }
}
