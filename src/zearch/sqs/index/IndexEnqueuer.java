package zearch.sqs.index;
import zearch.util.IndexRowEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class IndexEnqueuer {

    public static final IndexEnqueuer SINGLETON = new IndexEnqueuer();

    private static final String queueUrl = "https://sqs.us-west-2.amazonaws.com/695775503437/IndexQueue";

    private IndexEnqueuer() {

    }

    public void enqueue(IndexRowEntry entry) throws IOException, InterruptedException {

        Runtime rt = Runtime.getRuntime();
        String cmd = "aws sqs send-message"
                + " --queue-url " + queueUrl
                + " --region us-west-2"
                + " --message-body '" + entry.toJSON() + "'";
        Process pr = rt.exec(new String[] { "bash", "-c", cmd });

        BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }

}
