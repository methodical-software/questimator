package actors;

public class WikiMCQActorProtocol {

  public static class MCQRequest {
    private final String topic;

    private final int numQuestions;
    private final int numOptions;

    private static final int DEFAULT_NUM_QUESTIONS = 1;
    private static final int DEFAULT_NUM_OPTIONS = 4;

    public MCQRequest(String topic, int numQuestions, int numOptions) {
      this.topic = topic;
      this.numQuestions = numQuestions > 0 ? numQuestions : DEFAULT_NUM_QUESTIONS;
      this.numOptions = numOptions > 0 ? numOptions : DEFAULT_NUM_OPTIONS;
    }

    public String getTopic() {
      return this.topic;
    }

    public int getNumQuestions() {
      return numQuestions;
    }

    public int getNumOptions() {
      return numOptions;
    }
  }

  public static class MCQError {
    public final String error;

    public MCQError(String error) {
      this.error = error;
    }
  }
}
