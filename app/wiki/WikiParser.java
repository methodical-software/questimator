package wiki;

import java.util.ArrayList;
import java.util.List;

public class WikiParser {
  public List<String> getSentences(String wikiMarkupText) {
    ArrayList<String> sentences = new ArrayList<>();
    StringBuilder pageText = new StringBuilder();
    StringBuilder linkName = new StringBuilder();
    StringBuilder tagName = new StringBuilder();

    boolean isCurlyBracketRef = false;
    boolean isImage = false;
    boolean isCategory = false;
    boolean isTag = false;
    boolean isRef = false;
    boolean isSectionHeading = false;
    boolean isBulletedList = false;
    boolean isComment = false;

    int countLeftCurlyBracket = 0;
    int countRightCurlyBracket = 0;
    int countLeftSquareBracket = 0;
    int countRightSquareBracket = 0;
    int countSingleQuote = 0;
    int prevEqualSignIndex = 0;
    int sectionHeadingLevel = 0;
    char ch;
    String strLinkName;

    for (int i = 0; i < wikiMarkupText.length(); i++) {
      ch = wikiMarkupText.charAt(i);
      switch (ch) {
        case '{':
          isCurlyBracketRef = true;
          countLeftCurlyBracket++;
          break;
        case '}':
          countRightCurlyBracket++;
          if (countLeftCurlyBracket == countRightCurlyBracket) {
            isCurlyBracketRef = false;
          }
          break;
        case '[':
          countLeftSquareBracket++;
          break;
        case ']':
          countRightSquareBracket++;
          strLinkName = linkName.toString();
          if (strLinkName.startsWith("Category")) {
            isCategory = true;
            linkName.setLength(0);
          }
          if (countLeftSquareBracket == countRightSquareBracket) {
            // System.out.print(linkName);
            pageText.append(linkName);
            linkName.setLength(0);
            isImage = false;
            isCategory = false;
          }
          break;
        case '|':
          strLinkName = linkName.toString();
          if (strLinkName.startsWith("File") || strLinkName.startsWith("Image")) {
            isImage = true;
          }
          linkName.setLength(0);
          break;
        case '<':
          if (!isCurlyBracketRef) {
            isTag = true;
            isRef = false;
          }
          break;
        case '>':
          if (!isCurlyBracketRef) {
            String strTagName = tagName.toString();
            if (strTagName.startsWith("ref")) {
              isRef = true;
            }
            if (strTagName.startsWith("/ref") || strTagName.endsWith("/")) {
              isRef = false;
            }
            isTag = false;
            tagName.setLength(0);
          }
          isComment = false;
          break;
        case '\'':
          countSingleQuote++;
          break;
        case '=':
          // System.out.println();
          // System.out.println("pre (i, prevEqualSignIndex, sectionHeadingLevel, isSectionHeading)
          // = " +
          //        i + " " + prevEqualSignIndex + " " + sectionHeadingLevel + " " +
          // isSectionHeading);
          if ((i == (prevEqualSignIndex + 1))) {
            prevEqualSignIndex = i;
            if (!isSectionHeading) {
              isSectionHeading = true;
              sectionHeadingLevel = 2;
            } else {
              sectionHeadingLevel++;
            }
          } else {
            if (sectionHeadingLevel > 0) {
              sectionHeadingLevel--;
              if (sectionHeadingLevel == 0) {
                prevEqualSignIndex = 0;
                isSectionHeading = false;
              }
            }
          }
          if (!isSectionHeading) {
            prevEqualSignIndex = i;
          }
          // System.out.println("post (i, prevEqualSignIndex, sectionHeadingLevel, isSectionHeading)
          // = " +
          //        i + " " + prevEqualSignIndex + " " + sectionHeadingLevel + " " +
          // isSectionHeading);
          break;
        case '!':
          if (isTag) {
            isComment = true;
          }
          break;
        case '*':
          // Bulleted list
          isBulletedList = true;
          break;
        case '\n':
          isBulletedList = false;
          pageText.append(' ');
          // System.out.println();
          break;
        case ':':
          // Indenting text
          break;
        default:
          if (isCurlyBracketRef
              || isImage
              || isCategory
              || isRef
              || isSectionHeading
              || isBulletedList
              || isComment) {
            // skip
          } else if (countLeftSquareBracket != countRightSquareBracket) {
            linkName.append(ch);
          } else if (isTag) {
            tagName.append(ch);
          } else {
            if (countSingleQuote == 1) {
              pageText.append("'");
            }
            countSingleQuote = 0;
            pageText.append(ch);
            // System.out.print(ch);
          }
          break;
      }
    }
    String trimmedPageText =
        pageText.toString().trim().replaceAll("&nbsp;", " ").replaceAll(" +", " ");
    // System.out.println(trimmedPageText);
    String[] sentenceArray = trimmedPageText.split("[.] (?=\\p{Upper})");
    for (int i = 0; i < sentenceArray.length; i++) {
      String trimmedSentence = sentenceArray[i].trim();
      if (trimmedSentence.length() > 0) {
        if (!trimmedSentence.endsWith(".")) {
          trimmedSentence = trimmedSentence + ".";
        }
        // System.out.println(trimmedSentence);
        // System.out.println();
        sentences.add(trimmedSentence);
      }
    }
    return sentences;
  }
}
