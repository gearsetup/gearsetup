package io.gearsetup;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import lombok.Data;

/**
 * A representation of the required parameters to the {@link SnapshotHandler} Lambda to complete {@link AmazonDynamoDB} snapshots.
 *
 * @author Ian Caffey
 * @since 1.0
 */
@Data
public class SnapshotRequest {
    private String table;
    private String bucket;
}
