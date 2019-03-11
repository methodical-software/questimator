package questimator;

public class Term {
  private String topic;
  private int frequency;

  public Term(String topic) {
    this.topic = topic;
    this.frequency = 0;
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  public int getFrequency() {
    return frequency;
  }

  public void setFrequency(int frequency) {
    this.frequency = frequency;
  }

  public void incrementFrequency() {
    this.frequency += 1;
  }
}
