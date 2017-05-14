package bigdata;

public class Review {
    public String business_id;
    public String date;
    public String stars;
    public String text;
    public String type;
    public String user_id;
	public String review_id;

    public Review() {
        // noop
    }

    public double calculateSentimentValue() {

        SentimentEvaluator evaluator = SentimentEvaluator.getInstance();

        double sentiment = 2.5;

        String[] words = this.text.replaceAll("[\"#$%^&*@\\-=:;?().,]", "").split("\\W+");

        for (String word : words) {
            boolean isExtreme = word.contains("!");
            if (isExtreme) { word = word.replaceAll("!", ""); }

            if (evaluator.lexicon.containsKey(word)) {
                double lexiconValue = evaluator.lexicon.get(word);
                double bonus = isExtreme ? (0.5 + lexiconValue) : 0;

                sentiment += lexiconValue + bonus;
            }
        }

        return sentiment;
    }
}
