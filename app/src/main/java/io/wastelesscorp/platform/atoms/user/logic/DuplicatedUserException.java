package io.wastelesscorp.platform.atoms.user.logic;

import io.wastelesscorp.platform.support.exceptions.WastelessException;

public class DuplicatedUserException extends WastelessException {
  public DuplicatedUserException(String email) {
    super(String.format("The user with '%s' exists already.", email));
  }
}
