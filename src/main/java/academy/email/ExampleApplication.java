package academy.email;

import java.net.http.HttpClient;

public final class ExampleApplication {
  public static void main(String[] args) throws Exception {
    String key = System.getenv("INFRAI_API_KEY");
    String recipient = System.getenv("DEMO_EMAIL_TO");
    if (key == null || recipient == null) throw new IllegalStateException("Set INFRAI_API_KEY and DEMO_EMAIL_TO");
    InfraiEmailClient client = new InfraiEmailClient(HttpClient.newHttpClient(), key);
    LifecycleMailService service = new LifecycleMailService(client);
    System.out.println(service.digitalAssetDelivered(recipient, "Photography 101", "https://courses.example/download/lesson-1"));
  }
}
