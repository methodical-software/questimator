package questimator;

import java.util.List;

public class Question {
  private String topic;

  private List<String> relatedTopics;

  public Question(String topic) {
    this.topic = topic;
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  public List<String> getRelatedTopics() {
    return relatedTopics;
  }

  public void setRelatedTopics(List<String> relatedTopics) {
    this.relatedTopics = relatedTopics;
  }
}
