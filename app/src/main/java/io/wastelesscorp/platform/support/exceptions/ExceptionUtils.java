package io.wastelesscorp.platform.support.exceptions;

import java.util.function.Function;
import java.util.function.Predicate;

public final class ExceptionUtils {
  private ExceptionUtils() {}

  public static <T, U> Function<T, U> defaultOnError(
      ThrowingFunction<T, U, ?> function, U defaultValue) {
    return t -> {
      try {
        return function.apply(t);
      } catch (Throwable throwable) {
        return defaultValue;
      }
    };
  }

  public static <T> Predicate<T> defaultOnError(
      ThrowingPredicate<T, ?> predicate, boolean defaultValue) {
    return t -> {
      try {
        return predicate.test(t);
      } catch (Throwable throwable) {
        return defaultValue;
      }
    };
  }

  public static <T, U> Function<T, U> silent(ThrowingFunction<T, U, ?> function) {
    return t -> {
      try {
        return function.apply(t);
      } catch (Throwable throwable) {
        throw new RuntimeException(throwable);
      }
    };
  }

  @FunctionalInterface
  public interface ThrowingFunction<R, S, E extends Exception> {
    S apply(R r) throws E;
  }

  @FunctionalInterface
  public interface ThrowingPredicate<R, E extends Exception> {
    boolean test(R r) throws E;
  }
}
