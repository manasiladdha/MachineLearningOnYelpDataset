package bigdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class RatingReducer extends Reducer<Text, Text, Text, DoubleWritable> {

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {
        double total = 0;
		double length = 0;
		
        List<Double> dd = new ArrayList<Double>();
		
		for (Text t : values ){
			String[] arrVal = t.toString().split(":");
			String sentiment_score = arrVal[1];
			Double sentiment_score_double = Double.parseDouble(sentiment_score);
			dd.add(sentiment_score_double);
		}
		 
        for (Double sentiment : dd) {
            total += sentiment;
            length++;
        }

        double average = total / length;
        
        if(key.toString().equalsIgnoreCase("--0ZoBTQWQra1FxD4rBWmg"))
        	System.out.println("Found it again");
        
        context.write(key, new DoubleWritable(average));
    }
}
