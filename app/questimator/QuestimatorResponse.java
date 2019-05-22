package questimator;

import java.util.ArrayList;
import java.util.List;

public class QuestimatorResponse {
  private String topic;

  private List<String> relatedTopics;

  private int numQuestions;

  private int numOptions;

  private List<MCQ> mcqList = new ArrayList<>();

  public QuestimatorResponse(String topic) {
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

  public int getNumQuestions() {
    return numQuestions;
  }

  public void setNumQuestions(int numQuestions) {
    this.numQuestions = numQuestions;
  }

  public int getNumOptions() {
    return numOptions;
  }

  public void setNumOptions(int numOptions) {
    this.numOptions = numOptions;
  }

  public List<MCQ> getMCQList() {
    return mcqList;
  }

  public void addMCQ(MCQ mcq) {
    this.mcqList.add(mcq);
  }
}
