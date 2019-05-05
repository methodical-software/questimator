package questimator;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import wiki.WikiClient;
import wiki.WikiParser;

public class Questimator {
  private static final int MAX_RELATED_TOPICS = 10;
  private WikiClient wikiClient = new WikiClient();
  private WikiParser wikiParser = new WikiParser();

  public Question generateMCQ(String topic) {
    Question question = new Question(topic);

    // Related topic identification
    List<String> relatedTopics = wikiClient.getLinks(topic);

    List<Term> relatedTerms =
        relatedTopics
            .stream()
            .map(term -> new Term(term.toLowerCase()))
            .collect(Collectors.toList());

    updateWikiLinkTermFrequency(topic, relatedTerms);

    List<Term> sortedTerms =
        relatedTerms
            .stream()
            .sorted(Comparator.comparing(Term::getFrequency).reversed())
            .collect(Collectors.toList());

    List<Term> topRelatedTerms =
        sortedTerms.stream().limit(MAX_RELATED_TOPICS).collect(Collectors.toList());

    List<String> topRelatedTopics =
        topRelatedTerms.stream().map(term -> term.getTopic()).collect(Collectors.toList());

    question.setRelatedTopics(topRelatedTopics);

    // Get list of sentences in page text for question generation
    String pageText = wikiClient.getPageText(topic);
    // System.out.println(pageText);
    List<String> sentences = wikiParser.getSentences(pageText);

    // TODO: Question generation
    String parserModel = "englishPCFG.ser.gz";
    LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
    TreebankLanguagePack tlp = lp.getOp().langpack();
    String sent2 = "This is another sentence.";
    Tokenizer<? extends HasWord> tokenizer =
        tlp.getTokenizerFactory().getTokenizer(new StringReader(sent2));
    List<? extends HasWord> sentence2 = tokenizer.tokenize();
    Tree parse = lp.parse(sentence2);
    parse.pennPrint();

    // TODO: Multiple choice generation

    return question;
  }

  private int getTermMaxWordCount(List<Term> terms) {
    Optional<Term> longestTerm =
        terms
            .stream()
            .reduce(
                (t1, t2) ->
                    t1.getTopic().split(" ").length > t2.getTopic().split(" ").length ? t1 : t2);

    int maxCount = longestTerm.isPresent() ? longestTerm.get().getTopic().split(" ").length : 0;

    return maxCount;
  }

  private void updateWikiLinkTermFrequency(String topic, List<Term> relatedTerms) {
    String pageText = wikiClient.getPageText(topic);
    InputStream inputStream = new ByteArrayInputStream(pageText.getBytes());

    Scanner scanner = new Scanner(inputStream);
    StringBuilder stringBuilder = new StringBuilder();

    int numWords = 0;
    int maxTermWordCount = getTermMaxWordCount(relatedTerms);

    while (scanner.hasNext()) {
      String s = scanner.next().toLowerCase();
      stringBuilder.append(s);
      stringBuilder.append(" ");
      if (++numWords > maxTermWordCount) {
        stringBuilder.delete(0, stringBuilder.indexOf(" ") + 1);
      }
      relatedTerms
          .stream()
          .forEach(
              term -> {
                if (stringBuilder.toString().contains(term.getTopic())) {
                  term.incrementFrequency();
                }
              });
    }
  }
}
