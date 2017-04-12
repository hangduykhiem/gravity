package org.zalando.core.exception;

import org.zalando.core.utils.EqualUtils;

/**
 * Custom {@link SecurityException} used when a runtime permission is required
 *
 * @see android.Manifest.permission
 *
 * Created by jduran on 19/01/16.
 */
public class PermissionSecurityException extends SecurityException {

  private final String requiredPermission;

  /**
   * Constructor
   *
   * @param requiredPermission {@link String} with the required permission name
   */
  public PermissionSecurityException(String requiredPermission) {

    super();
    this.requiredPermission = requiredPermission;
  }

  /**
   * Provides the name of the required permission
   *
   * @return {@link String} with the name of the required permission
   */
  public String getRequiredPermission() {
    return requiredPermission;
  }

  @Override
  public boolean equals(Object other) {

    if (other instanceof PermissionSecurityException) {
      PermissionSecurityException otherPermissionSecurityException =
          (PermissionSecurityException) other;
      return EqualUtils.areEqual(getRequiredPermission(), otherPermissionSecurityException
          .getRequiredPermission());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return requiredPermission != null ? requiredPermission.hashCode() : 0;
  }
}
