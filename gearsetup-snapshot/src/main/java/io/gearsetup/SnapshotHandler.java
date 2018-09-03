package io.gearsetup;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of an Amazon Lambda {@link RequestHandler} that takes snapshots of {@link AmazonDynamoDB} tables
 * and stores them in {@link AmazonS3}.
 * <p>
 * An invocation of the Lambda takes in a source {@link AmazonDynamoDB} table and a target {@link AmazonS3} bucket.
 * <p>
 * The snapshot file is stored in {@code s3://{{bucket}}/{{table}}/{{timestamp}}.json} as well as updating a "latest"
 * snapshot file {@code s3://{{bucket}}/{{table}}/latest.json}.
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class SnapshotHandler implements RequestHandler<SnapshotRequest, SnapshotResponse> {
    private final AmazonS3 amazonS3 = AmazonS3Client.builder().withRegion("us-east-1").build();
    private final AmazonDynamoDB dynamoDb = AmazonDynamoDBClient.builder().withRegion("us-east-1").build();
    private final Gson gson = new Gson();

    /**
     * Accepts the {@link SnapshotRequest}, scans the entire {@link AmazonDynamoDB} table, serializes it to JSON,
     * and uploads the table contents to {@link AmazonS3}.
     * <p>
     * The snapshot file is stored in {@code s3://{{bucket}}/{{table}}/{{timestamp}}.json} as well as updating a "latest"
     * snapshot file {@code s3://{{bucket}}/{{table}}/latest.json}.
     *
     * @param request the snapshot request
     * @param context the lambda context
     * @return a {@link SnapshotResponse} representing the time, destination, and size of the snapshot
     */
    @Override
    public SnapshotResponse handleRequest(SnapshotRequest request, Context context) {
        long time = System.currentTimeMillis();
        String table = request.getTable();
        String bucket = request.getBucket();
        String key = table + "/" + time + ".json";
        String latestKey = table + "/latest.json";
        Set<Map<String, Object>> items = new HashSet<>();
        new Table(dynamoDb, table).scan().forEach(item -> items.add(item.asMap()));
        String snapshot = gson.toJson(items);
        amazonS3.putObject(bucket, key, snapshot);
        amazonS3.putObject(bucket, latestKey, snapshot);
        return SnapshotResponse.builder()
                .time(time)
                .destination(String.format("s3://%s/%s", bucket, key))
                .snapshotSize(items.size())
                .build();
    }
}
