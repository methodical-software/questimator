package questimator;

import java.util.List;
import wiki.WikiClient;

public class Questimator {
  public Question generateMCQ(String topic) {
    Question question = new Question(topic);

    // Related topic identification
    WikiClient wikiClient = new WikiClient();
    List<String> relatedTopics = wikiClient.getLinks(topic);
    question.setRelatedTopics(relatedTopics);

    // TODO: Question generation

    // TODO: Multiple choice generation

    return question;
  }
}
