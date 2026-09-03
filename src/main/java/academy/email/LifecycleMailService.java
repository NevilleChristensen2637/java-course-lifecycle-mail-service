package academy.email;

import java.io.IOException;

public final class LifecycleMailService {
  private final InfraiEmailClient client;
  public LifecycleMailService(InfraiEmailClient client) { this.client = client; }

  public String digitalAssetDelivered(String learner, String course, String downloadLink) throws IOException, InterruptedException {
    return client.send(learner, "Your " + course + " download is ready", "<p>Your lesson materials are ready.</p><p><a href=\"" + downloadLink + "\">Open the course files</a></p>");
  }

  public String subscriberUpdated(String learner, String course) throws IOException, InterruptedException {
    return client.send(learner, course + " has a new lesson", "<p>A new lesson is waiting in your learning plan.</p>");
  }

  public String contentProcessed(String learner, String course) throws IOException, InterruptedException {
    return client.send(learner, course + " processing complete", "<p>Your uploaded content has finished processing.</p>");
  }
}
