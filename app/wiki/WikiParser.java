package wiki;

import java.util.ArrayList;
import java.util.List;

public class WikiParser {
  public List<String> getSentences(String pageText) {
    ArrayList<String> sentences = new ArrayList<>();
    StringBuilder sentence = new StringBuilder();
    StringBuilder linkName = new StringBuilder();
    StringBuilder tagName = new StringBuilder();

    boolean isCurlyBracketRef = false;
    boolean isImage = false;
    boolean isTag = false;
    boolean isRef = false;

    int countLeftSquareBracket = 0;
    int countRightSquareBracket = 0;
    int countSingleQuote = 0;
    char ch;

    for (int i = 0; i < pageText.length(); i++) {
      ch = pageText.charAt(i);
      switch (ch) {
        case '{':
          isCurlyBracketRef = true;
          break;
        case '}':
          isCurlyBracketRef = false;
          break;
        case '[':
          countLeftSquareBracket++;
          break;
        case ']':
          countRightSquareBracket++;
          if (countLeftSquareBracket == countRightSquareBracket) {
            System.out.print(linkName);
            sentence.append(linkName);
            linkName.setLength(0);
            isImage = false;
          }
          break;
        case '|':
          String strLinkName = linkName.toString();
          if (strLinkName.startsWith("File:") || strLinkName.startsWith("Image:")) {
            isImage = true;
          }
          linkName.setLength(0);
          break;
        case '<':
          isTag = true;
          isRef = false;
          break;
        case '>':
          String strTagName = tagName.toString();
          if (strTagName.startsWith("ref")) {
            isRef = true;
          }
          if (strTagName.startsWith("/ref") || strTagName.endsWith("/")) {
            isRef = false;
          }
          isTag = false;
          tagName.setLength(0);
          break;
        case '\'':
          countSingleQuote++;
          break;
        default:
          if (isCurlyBracketRef || isImage || isRef) {
            // skip
          } else if (countLeftSquareBracket != countRightSquareBracket) {
            linkName.append(ch);
          } else if (isTag) {
            tagName.append(ch);
          } else {
            if (countSingleQuote == 1) {
              sentence.append("'");
            }
            countSingleQuote = 0;
            sentence.append(ch);
            System.out.print(ch);
          }
          break;
      }
    }
    return sentences;
  }
}
