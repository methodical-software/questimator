package actors;

public class WikiMCQActorProtocol {

  public static class MCQRequest {
    public final String topic;

    public MCQRequest(String topic) {
      this.topic = topic;
    }

    public String getTopic() {
      return this.topic;
    }
  }

  public static class MCQError {
    public final String error;

    public MCQError(String error) {
      this.error = error;
    }
  }
}
