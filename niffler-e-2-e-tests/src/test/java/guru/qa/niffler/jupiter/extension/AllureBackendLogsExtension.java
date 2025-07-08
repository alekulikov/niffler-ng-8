package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@ParametersAreNonnullByDefault
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

  private void addLogAttachment(String serviceName) {
    try (InputStream is = Files.newInputStream(Path.of(String.format("./logs/%s/app.log", serviceName)))) {
      Allure.getLifecycle().addAttachment(
          String.format("%s-log", serviceName),
          "text/html",
          ".log",
          is
      );
    } catch (Exception e) {
      Allure.getLifecycle().addAttachment(
          String.format("%s-log", serviceName),
          "text/html",
          ".log",
          new ByteArrayInputStream(
              ("Failed to attach log: " + e.getMessage())
                  .getBytes(StandardCharsets.UTF_8)
          )
      );
    }
  }
}
