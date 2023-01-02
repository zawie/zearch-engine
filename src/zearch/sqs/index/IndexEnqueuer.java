package zearch.sqs.index;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import zearch.util.IndexRowEntry;

public class IndexEnqueuer {

    public static final IndexEnqueuer SINGLETON = new IndexEnqueuer();

    private static final String queueName = "IndexQueue";

    private final AmazonSQS sqs;
    private final String queueUrl;

    private IndexEnqueuer() {
        sqs = AmazonSQSClientBuilder.standard()
                .withRegion(Regions.US_WEST_2)
                .build();
        queueUrl = sqs.getQueueUrl(new GetQueueUrlRequest(queueName)).getQueueUrl();
    }

    public void enqueue(IndexRowEntry entry) {
        SendMessageRequest sendMessageStandardQueue = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(entry.toJSON());
        sqs.sendMessage(sendMessageStandardQueue);
    }

}
