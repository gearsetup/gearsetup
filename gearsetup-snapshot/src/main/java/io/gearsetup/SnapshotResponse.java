package io.gearsetup;

import lombok.Builder;
import lombok.Data;

/**
 * A representation of the description of a successful {@link SnapshotHandler} invocation.
 *
 * @author Ian Caffey
 * @since 1.0
 */
@Data
@Builder
public class SnapshotResponse {
    private long time;
    private String destination;
    private int snapshotSize;
}
