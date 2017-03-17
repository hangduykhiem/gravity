package fi.zalando.core.exception;

import fi.zalando.core.utils.EqualUtils;
import fi.zalando.core.utils.Preconditions;

/**
 * Custom {@link IllegalStateException} used when a required feature is disabled
 *
 * Created by jduran on 21/01/16.
 */
public class ServiceDisabledException extends IllegalStateException {

  public interface ServiceType {

    String LOCATION_SERVICES = "location";
  }

  private final String disabledService;

  /**
   * Constructor
   *
   * @param disabledService {@link String} with the id of the disabled service
   * @see ServiceType
   */
  public ServiceDisabledException(String disabledService) {

    super();
    Preconditions.checkArgument(checkDisabledServiceId(disabledService), "Service name is not"
        + " defined in the ServiceType interface");
    this.disabledService = disabledService;
  }

  /**
   * Checks if the service name is properly defined based on the handled {@link ServiceType}s
   *
   * @param serviceNameToCheck {@link String} with the service type
   * @return {@link Boolean} indicating if the service name is right
   */
  private boolean checkDisabledServiceId(String serviceNameToCheck) {

    switch (serviceNameToCheck) {
      case ServiceType.LOCATION_SERVICES:
        return true;
      default:
        return false;
    }
  }

  /**
   * Provides the ID of the disabled service
   *
   * @return {@link String} with the disabled service name
   * @see ServiceType
   */
  public String getDisabledService() {
    return disabledService;
  }

  @Override
  public boolean equals(Object other) {

    if (other instanceof ServiceDisabledException) {
      ServiceDisabledException otherServiceDisabledException = (ServiceDisabledException)
          other;
      return EqualUtils.areEqual(getDisabledService(), otherServiceDisabledException
          .getDisabledService());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return disabledService != null ? disabledService.hashCode() : 0;
  }
}
