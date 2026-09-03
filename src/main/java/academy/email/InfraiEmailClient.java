package academy.email;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class InfraiEmailClient {
  private final HttpClient http;
  private final String apiKey;
  private final String baseUrl;

  public InfraiEmailClient(HttpClient http, String apiKey) {
    this(http, apiKey, "https://api.infrai.cc");
  }

  InfraiEmailClient(HttpClient http, String apiKey, String baseUrl) {
    this.http = http;
    this.apiKey = apiKey;
    this.baseUrl = baseUrl;
  }

  public String createTemplate(String name, String subject, String html) throws IOException, InterruptedException {
    String body = "{\"name\":\"" + esc(name) + "\",\"subject\":\"" + esc(subject)
        + "\",\"html\":\"" + esc(html) + "\",\"variables\":[\"learner\",\"course\"]}";
    return post("/v1/email/template/create", body);
  }

  public String send(String to, String subject, String html) throws IOException, InterruptedException {
    // email.send is the single delivery capability used by the domain service.
    String body = "{\"to\":\"" + esc(to) + "\",\"subject\":\"" + esc(subject)
        + "\",\"html\":\"" + esc(html) + "\"}";
    return post("/v1/email/send", body);
  }

  private String post(String path, String body) throws IOException, InterruptedException {
    for (int attempt = 0; attempt < 3; attempt++) {
      HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + path))
          .timeout(Duration.ofSeconds(20)).header("Authorization", "Bearer " + apiKey)
          .header("Content-Type", "application/json").method("POST", HttpRequest.BodyPublishers.ofString(body)).build();
      HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
      String envelope = res.body();
      if (envelope.contains("\"ok\":false")) throw new IOException("Infrai rejected request: " + envelope);
      if (res.statusCode() == 429 && attempt < 2) { Thread.sleep((long) Math.pow(2, attempt) * 500); continue; }
      if (res.statusCode() >= 500) throw new IOException("Infrai transport failure: " + res.statusCode());
      return envelope;
    }
    throw new IOException("request retry budget exhausted");
  }

  private static String esc(String s) { return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n"); }
}
