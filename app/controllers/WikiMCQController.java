package controllers;

import static akka.pattern.Patterns.ask;

import actors.WikiMCQActor;
import actors.WikiMCQActorProtocol.*;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.inject.Singleton;
import play.libs.Json;
import play.mvc.*;
import scala.compat.java8.FutureConverters;

/** Controller to automate generation of a MCQ (Multiple Choice Question) */
@Singleton
public class WikiMCQController extends Controller {

  final ActorRef mcqActor;

  @Inject
  public WikiMCQController(ActorSystem system) {
    mcqActor = system.actorOf(WikiMCQActor.getProps());
  }

  /** Generates a MCQ. */
  @BodyParser.Of(BodyParser.Json.class)
  public CompletionStage<Result> generateQuestion(Http.Request request) {
    JsonNode json = request.body().asJson();
    String topic = json.findPath("topic").textValue();
    int numQuestions = json.findPath("numQuestions").intValue();
    int numOptions = json.findPath("numOptions").intValue();
    return FutureConverters.toJava(ask(mcqActor, new MCQRequest(topic, numQuestions, numOptions), 1000000))
        .thenApply(response -> ok(Json.toJson(response)));
  }
}
