package io.gearsetup.data;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.gearsetup.gson.GearSetupGsonFactory;
import lombok.NonNull;

import java.lang.reflect.Type;
import java.util.Set;

/**
 * A representation of a retriever of a snapshot stored in {@link AmazonS3}.
 * <p>
 * A snapshot is a single file in {@link AmazonS3} as a JSON array where each element in the array is a serialized form
 * of the model class being retrieved.
 *
 * @param <T> the type of model being retrieved in the snapshot
 * @author Ian Caffey
 * @since 1.0
 */
public final class SnapshotRetriever<T> {
    private static final Gson GSON = GearSetupGsonFactory.create();
    private final String bucket;
    private final String key;
    private final Type listingType;
    private final AmazonS3 amazonS3;

    /**
     * Constructs a new {@link SnapshotRetriever} pointing to the specified {@link AmazonS3} bucket with necessary
     * credentials to read the snapshot file.
     * <p>
     * The model class is used as a type hint for {@link Gson} for serializing each element in the snapshot.
     *
     * @param region      the AWS region to use for S3
     * @param bucket      the bucket containing the snapshot file
     * @param key         the key of the snapshot file
     * @param credentials the credentials with access to read the object listing
     * @param model       the type of each element in the object listing
     */
    public SnapshotRetriever(@NonNull String region, @NonNull String bucket, @NonNull String key,
                             @NonNull AWSCredentialsProvider credentials, @NonNull Class<T> model) {
        this.bucket = bucket;
        this.key = key;
        this.listingType = TypeToken.getParameterized(Set.class, model).getType();
        this.amazonS3 = AmazonS3Client.builder()
                .withRegion(region)
                .withCredentials(credentials)
                .build();
    }

    /**
     * Loads the snapshot file from {@link AmazonS3}.
     * <p>
     * {@link SnapshotRetriever#GSON} is used to deserialize the object listing found in {@link AmazonS3}.
     *
     * @return the deserialized snapshot file
     */
    public Set<T> load() {
        return GSON.fromJson(amazonS3.getObjectAsString(bucket, key), listingType);
    }
}
