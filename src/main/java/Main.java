import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class Main {
    static Scanner scanner;

    public static void main(String[] args) {
        int score = 0;
        scanner = new Scanner(System.in);

        BaseRobot site = siteSelection();
        if (site != null) {
            score += wordsGame(site);
            String userText = getHeadlinesText();
            System.out.println("how many time it will appears?:");
            int quantity = scanner.nextInt();
            try {
                score += textDetails(quantity, userText, site);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("you achieved " + score + " points!");
        } else {
            System.out.println("cant access website, sorry.");
        }

    }

    private static int textDetails(int quantity, String userText, BaseRobot site) throws IOException {
        int realQuantity = site.countTitles(userText);
        if (Math.abs(quantity - realQuantity) <= 2) {
            return 250;
        }
        return 0;
    }

    private static String getHeadlinesText() {
        String userText = "";
        while (userText.length() < 1 || userText.length() > 20) {
            System.out.println("Enter text that you think will be in the headlines on the site,\n" +
                    "the text should be between 1 and 20 chars:");
            userText = scanner.nextLine();
        }
        return userText;
    }

    private static BaseRobot siteSelection() {
        int selection = 0;
        while (selection < 1 || selection > 3) {
            System.out.println("Choose site:\n");
            System.out.println("1.Mako\n");
            System.out.println("2.Ynet\n");
            System.out.println("3.Walla\n");
            selection = scanner.nextInt();
        }
        scanner.nextLine();
        try {
            switch (selection) {
                case 1:
                    return new MakoRobot();

                case 2:
                    return new YnetRobot();
                default:
                    return new WallaRobot();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int wordsGame(BaseRobot site) {
        String guess;
        int score = 0;
        try {
            String longestArticle = site.getLongestArticleTitle();
            System.out.println("guess what are the common words");
            System.out.println("hint:\n" + longestArticle);
            Map<String, Integer> wordsInSite = site.getWordsStatistics();
            for (int i = 1; i <= 5; i++) {
                System.out.println("guess number " + i + ":");
                guess = scanner.nextLine();
                score += wordsInSite.getOrDefault(guess, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return score;
    }

}