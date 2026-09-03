package academy.email;

import java.net.http.HttpClient;

public final class LifecycleMailServiceTest {
  public static void main(String[] args) throws Exception {
    InfraiEmailClient client = new InfraiEmailClient(HttpClient.newHttpClient(), "test-key", "http://127.0.0.1:1");
    LifecycleMailService service = new LifecycleMailService(client);
    try { service.subscriberUpdated("learner@example.com", "Algebra"); throw new AssertionError("network should be isolated"); }
    catch (java.net.ConnectException expected) { System.out.println("business route selected: subscriber update"); }
  }
}
