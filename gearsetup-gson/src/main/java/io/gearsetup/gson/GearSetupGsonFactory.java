package io.gearsetup.gson;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import io.gearsetup.AttackSpeed;
import io.gearsetup.AttackType;
import io.gearsetup.ImmutableTypeDependentAttackSpeed;
import io.gearsetup.TypeDependentAttackSpeed;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.ServiceLoader;

/**
 * A utility class providing an factory for {@link Gson} that supports all of
 * <a href="https://github.com/gearsetup/gearsetup/tree/master/gearsetup-model">gearsetup-model</a> model classes.
 * <p>
 * <a href="https://github.com/gearsetup/gearsetup/tree/master/gearsetup-model">gearsetup-model</a> utilizes
 * <a href="https://github.com/gearsetup/gearsetup/tree/master/immutables">Immutables</a> for auto-generating {@link Gson}
 * {@link TypeAdapter} for all the model classes. These type adapters need to be found using {@link ServiceLoader} as well
 * as a few custom type adapters for wrapper classes to improve readability of the output JSON.
 *
 * @author Ian Caffey
 * @since 1.0
 */
@UtilityClass
public class GearSetupGsonFactory {
    /**
     * Constructs a new {@link Gson} with all {@link TypeAdapter} necessary to serialize and deserialize
     * <a href="https://github.com/gearsetup/gearsetup/tree/master/gearsetup-model">gearsetup-model</a>.
     * <p>
     * All auto-generated {@link TypeAdapter} are found using {@code ServiceLoader.load(TypeAdapterFactory.class)}. And
     * the serializer and deserializer override for {@link TypeDependentAttackSpeed} to remove the unnecessary nested
     * JSON object is registered.
     *
     * @return a new {@link Gson} with all necessary type adapters to serialize the gearsetup model
     */
    public Gson create() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        for (TypeAdapterFactory factory : ServiceLoader.load(TypeAdapterFactory.class)) {
            gsonBuilder.registerTypeAdapterFactory(factory);
        }
        //Custom serializer and deserializer required for wrapper objects, otherwise JSON looks redundant with extra JSON object for field being wrapped
        gsonBuilder.registerTypeAdapter(TypeDependentAttackSpeed.class, (JsonSerializer<TypeDependentAttackSpeed>) (src, typeOfSrc, context) ->
                context.serialize(src.getAttackSpeeds())
        );
        gsonBuilder.registerTypeAdapter(TypeDependentAttackSpeed.class, (JsonDeserializer<TypeDependentAttackSpeed>) (json, typeOfT, context) -> {
            Map<AttackType, AttackSpeed> speeds = context.deserialize(json, TypeToken.getParameterized(Map.class, AttackType.class, AttackSpeed.class).getType());
            return ImmutableTypeDependentAttackSpeed.of(speeds);
        });
        return gsonBuilder.create();
    }
}
