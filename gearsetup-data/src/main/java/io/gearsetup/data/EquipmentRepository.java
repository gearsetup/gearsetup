package io.gearsetup.data;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import io.gearsetup.Equipment;
import io.gearsetup.immutables.ImmutableGearSetupStyle;
import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;

import java.util.Set;

/**
 * A representation of a repository of {@link Equipment} snapshot stored in {@link AmazonS3}.
 * <p>
 * {@link Equipment} information for the entire <a href="https://oldschool.runescape.com/">Old School Runescape</a> is
 * maintained in the {@code gearsetup} S3 bucket in an {@code equipment.json} file.
 *
 * @author Ian Caffey
 * @since 1.0
 */
@Immutable
@ImmutableGearSetupStyle
public abstract class EquipmentRepository {
    //Immutables builder stub to hide immutable class dependency
    public static Builder builder() {
        return ImmutableEquipmentRepository.builder();
    }

    /**
     * Represents the AWS region to use when contacting {@link AmazonS3}.
     * <p>
     * The default AWS region to use is {@code us-east-1}.
     *
     * @return the AWS region to use for S3
     */
    @Default
    protected String getRegion() {
        return "us-east-1";
    }

    /**
     * Represents the AWS credentials to use which have access to read from the {@code gearsetup} bucket to download the
     * {@link Equipment} information.
     * <p>
     * The default {@link AWSCredentialsProvider} to use is {@link DefaultAWSCredentialsProviderChain}.
     *
     * @return the AWS credentials to use for S3 to access the gearsetup bucket
     */
    @Default
    @Auxiliary
    protected AWSCredentialsProvider getCredentials() {
        return new DefaultAWSCredentialsProviderChain();
    }

    /**
     * Represents the {@link SnapshotRetriever} that gets lazily initialized to retrieving the {@link Equipment} snapshot.
     *
     * @return the listing retriever to use for loading equipment snapshots
     */
    @Lazy
    protected SnapshotRetriever<Equipment> getRetriever() {
        return new SnapshotRetriever<>(getRegion(), "gearsetup", "Equipment/latest.json", getCredentials(), Equipment.class);
    }

    /**
     * Loads the current {@link Equipment} snapshot from S3 using {@link SnapshotRetriever}.
     * <p>
     * The call to retrieve the {@link Equipment} snapshot from S3 only happens once and the results are cached for future calls.
     *
     * @return the current set of equipment metadata present in S3
     */
    @Lazy
    public Set<Equipment> load() {
        return getRetriever().load();
    }

    //Immutables builder stub to hide immutable class dependency
    public interface Builder {
        Builder setRegion(String region);

        Builder setCredentials(AWSCredentialsProvider credentials);

        EquipmentRepository build();
    }
}
