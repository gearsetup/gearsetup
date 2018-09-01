package io.gearsetup.data;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.gearsetup.Equipment;
import io.gearsetup.gson.GearSetupGsonFactory;
import io.gearsetup.immutables.ImmutableGearSetupStyle;
import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;

import java.lang.reflect.Type;
import java.util.Set;

/**
 * A representation of a repository of {@link Equipment} information stored in {@link AmazonS3}.
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
    private static final String GEAR_SETUP_BUCKET = "gearsetup";
    private static final String EQUIPMENT_FILE = "equipment.json";
    private static final Type EQUIPMENT_SET = TypeToken.getParameterized(Set.class, Equipment.class).getType();
    private static final Gson GSON = GearSetupGsonFactory.create();

    //Immutables builder stub to hide immutable class dependency
    public static Builder builder() {
        return ImmutableEquipmentRepository.builder();
    }

    /**
     * Represents the AWS region to use when contacting {@link AmazonS3}.
     *
     * @return the AWS region to use for S3
     */
    protected abstract String getRegion();

    /**
     * Represents the AWS credentials to use which have access to read from the
     * {@link EquipmentRepository#GEAR_SETUP_BUCKET} bucket to download the {@link Equipment} information.
     *
     * @return the AWS credentials to use for S3 to access the gearsetup bucket
     */
    @Auxiliary
    protected abstract AWSCredentialsProvider getCredentials();

    /**
     * Represents the {@link AmazonS3} that gets lazily initialized when retrieving the {@link Equipment} information.
     *
     * @return the S3 client to use for retrieving equipment data
     */
    @Lazy
    protected AmazonS3 getAmazonS3() {
        return AmazonS3Client.builder()
                .withRegion(getRegion())
                .withCredentials(getCredentials())
                .build();
    }

    /**
     * Loads the current {@link Equipment} information from S3 using {@link EquipmentRepository#getAmazonS3()}.
     * <p>
     * The call to retrieve {@link Equipment} information S3 only happens once and the results are cached for future calls.
     * <p>
     * {@link GearSetupGsonFactory} is used internally to create the {@link Gson} used to deserialize the
     * {@link Equipment} information.
     *
     * @return the current set of equipment metadata present in S3
     */
    @Lazy
    public Set<Equipment> load() {
        return GSON.fromJson(getAmazonS3().getObjectAsString(GEAR_SETUP_BUCKET, EQUIPMENT_FILE), EQUIPMENT_SET);
    }

    //Immutables builder stub to hide immutable class dependency
    public interface Builder {
        Builder setRegion(String region);

        Builder setCredentials(AWSCredentialsProvider credentials);

        EquipmentRepository build();
    }
}
