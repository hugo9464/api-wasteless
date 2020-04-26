package io.wastelesscorp.platform.support.immutable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.immutables.value.Value;

@Value.Style(
    allParameters = true,
    typeAbstract = {"*Interface"},
    strictBuilder = true,
    typeImmutable = "*",
    jdkOnly = true,
    privateNoargConstructor = true)
@JsonSerialize
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.PACKAGE, ElementType.TYPE})
public @interface WastelessImmutableStyle {}
