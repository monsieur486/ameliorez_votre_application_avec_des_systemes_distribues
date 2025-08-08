package com.openclassrooms.tourguide.helper;

import com.openclassrooms.tourguide.configuration.ApplicationConfiguration;
import lombok.Getter;
import lombok.Setter;

public class InternalTestHelper {

	// Set this default up to 100,000 for testing
  @Getter
  @Setter
  private static int internalUserNumber = ApplicationConfiguration.INTERNAL_USER_NUMBER;

}
