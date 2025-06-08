package guru.qa.niffler.jupiter.converter;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.utils.Browser;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

public class BrowserArgumentConverter implements ArgumentConverter {

  @Override
  public SelenideDriver convert(Object source, ParameterContext context) throws ArgumentConversionException {
    if (source == null) {
      throw new ArgumentConversionException("Cannot convert null to " + context.getParameter().getType());
    }
    if (source instanceof Browser browser) {
      return new SelenideDriver(browser.getConfig());
    } else {
      throw new ArgumentConversionException("Cannot convert " + source + " to " + context.getParameter().getType());
    }
  }
}
