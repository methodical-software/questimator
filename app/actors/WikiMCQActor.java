package actors;

import actors.WikiMCQActorProtocol.*;
import akka.actor.AbstractActor;
import akka.actor.Props;
import org.springframework.util.StringUtils;
import questimator.Questimator;
import questimator.QuestimatorResponse;

public class WikiMCQActor extends AbstractActor {
  final Questimator questimator = new Questimator();

  public static Props getProps() {
    return Props.create(WikiMCQActor.class);
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(
            MCQRequest.class,
            mcqRequest -> {
              if (StringUtils.isEmpty(mcqRequest.getTopic())) {
                MCQError error = new MCQError("Mandatory parameter topic is blank.");
                sender().tell(error, self());
              } else {
                QuestimatorResponse questimatorResponse = questimator.generateMCQ(mcqRequest.getTopic(), mcqRequest.getNumQuestions(), mcqRequest.getNumOptions());
                sender().tell(questimatorResponse, self());
              }
            })
        .build();
  }
}
