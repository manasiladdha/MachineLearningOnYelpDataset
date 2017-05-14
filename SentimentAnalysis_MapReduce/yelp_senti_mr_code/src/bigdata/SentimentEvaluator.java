package bigdata;

import java.io.IOException;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.net.*;

public class SentimentEvaluator {
    private static SentimentEvaluator ourInstance = new SentimentEvaluator();

    public static SentimentEvaluator getInstance() {
        return ourInstance;
    }

    public HashMap<String, Double> lexicon = new HashMap<String, Double>();

    private SentimentEvaluator() { }

    public void buildDataSet(Configuration conf) throws IOException, URISyntaxException {
        //FileReader fr = new FileReader("bigdata/lexicon.ttf");
		//Path pt = new Path("hdfs:/bigdata/lexicon.ttf"); //Location of file in HDFS
        //FileSystem fs = FileSystem.get(new Configuration());
		
		FileSystem fs = FileSystem.get(new URI("hdfs://localhost:9000"), conf );
        Path popFile = new Path(fs.getWorkingDirectory()+"/bigdata/lexicon.ttf");
        BufferedReader reader = new BufferedReader(new InputStreamReader(fs.open(popFile)));       
		
		//BufferedReader reader = new BufferedReader(new InputStreamReader(fs.open(pt)));
        String line;

        while ((line = reader.readLine()) != null) {
            String type = "";
            String word = "";
            String polarity = "";

            String[] properties = line.split(" ");
            for (String property : properties) {

                if (!property.contains("=")) {
                    continue;
                }

                String[] keyVal = property.split("=");
                String key = keyVal[0];
                String val = keyVal[1];

                if (key.equals("type")) {
                    type = val;
                } else if (key.equals("word1")) {
                    word = val;
                } else if (key.equals("priorpolarity")) {
                    polarity = val;
                }
            }

            double sentimentValue;
            if (type.equals("strongsubj")) {
                sentimentValue = 0.3;
            } else {
                sentimentValue = 0.1;
            }

            if (polarity.equals("negative")) {
                sentimentValue = -sentimentValue;
            }


            lexicon.put(word, sentimentValue);
        }
    }
}
