package zearch.sqs.index;
import zearch.util.IndexRowEntry;

import java.io.IOException;

public class IndexEnqueuer {

    public static final IndexEnqueuer SINGLETON = new IndexEnqueuer();

    private static final String queueUrl = "https://sqs.us-west-2.amazonaws.com/695775503437/IndexQueue";

    private IndexEnqueuer() {

    }

    public void enqueue(IndexRowEntry entry) throws IOException, InterruptedException {

        Runtime rt = Runtime.getRuntime();
        System.out.println(entry.toJSON());
        Process pr = rt.exec("aws sqs send-message"
                + " --queue-url " + queueUrl
                + " --region us-west-2"
                + " --message-body '" + entry.toJSON() +"'");
    }

}
