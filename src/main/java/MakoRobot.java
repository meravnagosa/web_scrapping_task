import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MakoRobot extends BaseRobot implements MapOrder {
    private Map<String, Integer> map = new HashMap<>();
    private final ArrayList<String> urlList;

    public MakoRobot() throws IOException {
        super("https://www.mako.co.il/");
        String url, begging = "https://www.mako.co.il/";
        urlList = new ArrayList<>();
        Document mako = Jsoup.connect(getRootWebsiteUrl()).get();
        for (Element teasers : mako.getElementsByClass("teasers")) {
            for (Element child : teasers.children()) {
                url = child.child(0).child(0).attributes().get("href");
                if (url.contains(begging)) {
                    urlList.add(url);
                } else {
                    urlList.add(begging + url);
                }
            }
        }
        for (Element news : mako.getElementsByClass("neo_ordering scale_image horizontal news")) {
            for (Element h5 : news.getElementsByTag("h5")) {
                url = h5.child(0).attributes().get("href");
                if (url.contains(begging)) {
                    urlList.add(url);
                } else {
                    urlList.add(begging + url);
                }
            }
        }
    }

    @Override
    public Map<String, Integer> getWordsStatistics() throws IOException {
        for (String site : urlList) {
            String siteText;
            siteText = accessSite(site);
            siteText = fixWords(siteText);
            String[] wordsOfArticle = siteText.split(" ");
            map = addToMap(wordsOfArticle, map);
        }
        return map;
    }

    @Override
    public int countTitles(String text) throws IOException {
        Document mako = Jsoup.connect(getRootWebsiteUrl()).get();
        int numOfTitles = 0;
        for (Element spanElements : mako.getElementsByTag("span")) {
            for (Element title : spanElements.getElementsByAttributeValue("data-type", "title")) {
                if (title.text().contains(text)) {
                    numOfTitles++;
                }
            }
        }
        return numOfTitles;
    }

    @Override
    public String getLongestArticleTitle() throws IOException {
        Document article;
        String longestArticleTitle = "";
        int longest = 0;
        for (String site : urlList) {
            article = Jsoup.connect(site).get();
            String title = article.getElementsByTag("h1").get(0).text();
            StringBuilder siteTextBuilder = new StringBuilder();
            Element articleBody = article.getElementsByClass("article-body").get(0);
            for (Element p : articleBody.getElementsByTag("p")) {
                siteTextBuilder.append(p.text());
            }
            if (longest < siteTextBuilder.length()) {
                longest = siteTextBuilder.length();
                longestArticleTitle = title;
            }
        }
        return longestArticleTitle;
    }

    public String accessSite(String site) throws IOException {
        Document article;
        StringBuilder siteTextBuilder = new StringBuilder();
        article = Jsoup.connect(site).get();
        siteTextBuilder.append(article.getElementsByTag("h1").get(0).text());
        siteTextBuilder.append(" ");
        siteTextBuilder.append(article.getElementsByTag("h2").text());
        Element articleBody = article.getElementsByClass("article-body").get(0);
        for (Element p : articleBody.getElementsByTag("p")) {
            siteTextBuilder.append(" ");
            siteTextBuilder.append(p.text());
        }
        return siteTextBuilder.toString();
    }
}