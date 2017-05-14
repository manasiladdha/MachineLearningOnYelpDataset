package bigdata;

import java.io.IOException;
import com.google.gson.Gson;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class SentimentMapper extends Mapper<LongWritable, Text, Text, Text> {

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        Gson gson = new Gson();

        if (!value.toString().contains("\"text\"")) {
            return; // Not a review we're interested in.
        }

        Review review = gson.fromJson(value.toString(), Review.class);

        DoubleWritable sentimentValue = new DoubleWritable(review.calculateSentimentValue());
        Text businessId = new Text(review.business_id);

		Text value_text =  new Text(review.review_id + ":" + sentimentValue);

        context.write(businessId, value_text);
    }

}
