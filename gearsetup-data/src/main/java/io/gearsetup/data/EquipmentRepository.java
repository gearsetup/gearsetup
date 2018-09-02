package io.gearsetup.data;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.gearsetup.Equipment;
import io.gearsetup.gson.GearSetupGsonFactory;
import io.gearsetup.immutables.ImmutableGearSetupStyle;
import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;

import java.lang.reflect.Type;
import java.util.Set;

/**
 * A representation of a repository of {@link Equipment} information stored in {@link AmazonDynamoDB}.
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
    private static final Type EQUIPMENT_SET = TypeToken.getParameterized(Set.class, Equipment.class).getType();
    private static final Gson GSON = GearSetupGsonFactory.create();

    //Immutables builder stub to hide immutable class dependency
    public static Builder builder() {
        return ImmutableEquipmentRepository.builder();
    }

    /**
     * Represents the AWS region to use when contacting {@link AmazonDynamoDB}.
     *
     * @return the AWS region to use for S3
     */
    protected abstract String getRegion();

    /**
     * Represents the AWS credentials to use which have access to read from the
     * {@link EquipmentRepository#GEAR_SETUP_BUCKET} bucket to download the {@link Equipment} information.
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
     * Represents the {@link AmazonDynamoDB} that gets lazily initialized when retrieving the {@link Equipment} information.
     *
     * @return the S3 client to use for retrieving equipment data
     */
    @Lazy
    protected AmazonDynamoDB getDynamoDB() {
        return AmazonDynamoDBClient.builder()
                .withRegion(getRegion())
                .withCredentials(getCredentials())
                .build();
    }

    @Lazy
    protected Table getEquipmentTable() {
        return new Table(getDynamoDB(), "equipment");
    }

    public Equipment findById(int id) {
        return getEquipmentTable().getItem("id", id);
    }

    //Immutables builder stub to hide immutable class dependency
    public interface Builder {
        Builder setRegion(String region);

        Builder setCredentials(AWSCredentialsProvider credentials);

        EquipmentRepository build();
    }
}
