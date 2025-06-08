package guru.qa.niffler.utils;

import com.codeborne.selenide.SelenideConfig;

public enum Browser {
  CHROME(SelenideUtils.chromeConfig),
  FIREFOX(SelenideUtils.firefoxConfig);

  private final SelenideConfig config;

  Browser(SelenideConfig config) {
    this.config = config;
  }

  public SelenideConfig getConfig() {
    return config;
  }
}
