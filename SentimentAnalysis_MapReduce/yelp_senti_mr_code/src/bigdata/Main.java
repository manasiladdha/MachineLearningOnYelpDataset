package bigdata;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.mapreduce.Job;
import java.io.IOException;
import java.net.URI;

public class Main {

	public static void main(String[] args) throws Exception {
		SentimentEvaluator evaluator = SentimentEvaluator.getInstance();

		try {
			Configuration conf = new Configuration();
			GenericOptionsParser parser = new GenericOptionsParser(conf, args);
			args = parser.getRemainingArgs();
			evaluator.buildDataSet(conf);
			System.out.println("Found Mr lexi");

			boolean success = runFirstJob(args[0], args[1], conf);
			System.exit(success ? 0 : 1);
		} catch (IOException e) {
			System.out.println("IOException : " + e.getMessage());
			System.exit(1);
		}
		catch (Exception e) {
			System.out.println("Exception : " + e.getMessage());
			System.exit(1);
		}
	}

	public static boolean runFirstJob(String dataFile, String outputPath, Configuration conf) throws Exception {
		Job job = new Job(conf, "Yelp Sentiment Analysis");
		job.setJarByClass(Main.class);

		FileSystem fs = FileSystem.get(new URI("hdfs://localhost:9000"), conf);
		FileInputFormat.setInputPaths(job, new Path(fs.getWorkingDirectory()+ "/" + dataFile));
		FileOutputFormat.setOutputPath(job, new Path(fs.getWorkingDirectory()+ "/" + outputPath));

		job.setMapperClass(SentimentMapper.class);
		job.setReducerClass(RatingReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		return job.waitForCompletion(true);
	}

}
