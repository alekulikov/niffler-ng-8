package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class AllureBackendLogsExtension implements SuiteExtension {

  public static final String caseName = "Niffler backend logs";

  @Override
  public void afterSuite() {
    AllureLifecycle allureLifecycle = Allure.getLifecycle();
    String caseId = UUID.randomUUID().toString();
    allureLifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setName(caseName));
    allureLifecycle.startTestCase(caseId);

    addLogAttachment("niffler-auth");
    addLogAttachment("niffler-spend");
    addLogAttachment("niffler-userdata");
    addLogAttachment("niffler-currency");
    addLogAttachment("niffler-gateway");

    allureLifecycle.stopTestCase(caseId);
    allureLifecycle.writeTestCase(caseId);
  }

  @SneakyThrows
  private void addLogAttachment(String serviceName) {
    Allure.getLifecycle().addAttachment(
        String.format("%s-log", serviceName),
        "text/html",
        ".log",
        Files.newInputStream(
            Path.of(String.format("./logs/%s/app.log", serviceName))
        )
    );
  }
}
