package com.openclassrooms.tourguide.helper;

import com.openclassrooms.tourguide.TourGuideConfiguration;

public class InternalTestHelper {

  // Set this default up to 100,000 for testing
  private static int internalUserNumber = TourGuideConfiguration.NUMBER_OF_USERS;

  public static int getInternalUserNumber() {
    return internalUserNumber;
  }

  public static void setInternalUserNumber(int internalUserNumber) {
    InternalTestHelper.internalUserNumber = internalUserNumber;
  }
}
