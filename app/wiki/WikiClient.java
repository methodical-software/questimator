package wiki;

import fastily.jwiki.core.NS;
import fastily.jwiki.core.Wiki;
import java.util.List;

public class WikiClient {

  Wiki wiki;

  public WikiClient() {
    wiki = new Wiki("en.wikipedia.org");
  }

  public String getPageText(String title) {
    return wiki.getPageText(title);
  }

  public List<String> getLinks(String title) {
    return wiki.getLinksOnPage(title, NS.MAIN);
  }
}
