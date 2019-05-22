package questimator;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
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

  public QuestimatorResponse generateMCQ(String topic, int numQuestions, int numOptions) {
    QuestimatorResponse questimatorResponse = new QuestimatorResponse(topic);

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

    questimatorResponse.setRelatedTopics(topRelatedTopics);

    // Get list of sentences in page text for questimatorResponse generation
    String pageText = wikiClient.getPageText(topic);
    // System.out.println(pageText);
    List<String> sentences = wikiParser.getSentences(pageText);

    // Questimator Response generation
    int countQuestions = 0;
    int sentenceIndex = 0;

    while ((countQuestions < numQuestions) && (sentenceIndex < sentences.size())) {
      String parserModel = "englishPCFG.ser.gz";
      LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
      TreebankLanguagePack tlp = lp.getOp().langpack();

      String sentence = sentences.get(sentenceIndex++);
      // if (!sentence.contains(" is ")) continue;

      Tokenizer<? extends HasWord> tokenizer =
          tlp.getTokenizerFactory().getTokenizer(new StringReader(sentence));
      List<? extends HasWord> tokenList = tokenizer.tokenize();
      Tree parse = lp.parse(tokenList);
      // System.out.println(parse.toString());
      parse.pennPrint();

      // Multiple choice generation
      String s = "(VP [> S=parent] [$ NP] [< VBZ=vbz] [< NP=answer]) : (=parent > ROOT)";

      TregexPattern p = TregexPattern.compile(s);
      TregexMatcher m = p.matcher(parse);
      if (m.find()) {
        String vbz = getOptionString(m.getNode("vbz").yieldWords());
        String answer = getOptionString(m.getMatch().yieldWords());

        answer = answer.replaceFirst(vbz, "").trim();

        String question = sentence.replace(answer, "__________");

        MCQ mcq = new MCQ();
        mcq.setQuestion(question);
        mcq.addOption(answer);
        questimatorResponse.addMCQ(mcq);

        System.out.println("question = " + question);
        System.out.println("answer = " + answer);
        parse.pennPrint();

        countQuestions++;
      }
    }

    questimatorResponse.setNumQuestions(countQuestions);
    questimatorResponse.setNumOptions(numOptions);

    return questimatorResponse;
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

  private String getOptionString(List<Word> optionWordList) {
    StringBuilder optionStrBuilder = new StringBuilder();
    optionWordList.stream().forEach(word -> optionStrBuilder.append(word).append(" "));

    String optionStr = optionStrBuilder.toString().trim();

    optionStr = optionStr.replaceAll("-LRB- ", "(");
    optionStr = optionStr.replaceAll(" -RRB-", ")");
    optionStr = optionStr.replaceAll("`` ", "\"");
    optionStr = optionStr.replaceAll(" ''", "\"");
    optionStr = optionStr.replaceAll(" '", "'");
    optionStr = optionStr.replaceAll(" ,", ",");

    return optionStr;
  }
}
