package com.openclassrooms.tourguide.helper;

import com.openclassrooms.tourguide.configuration.ApplicationConfiguration;

public class InternalTestHelper {

	// Set this default up to 100,000 for testing
	private static int internalUserNumber = ApplicationConfiguration.INTERNAL_USER_NUMBER;
	
	public static void setInternalUserNumber(int internalUserNumber) {
		InternalTestHelper.internalUserNumber = internalUserNumber;
	}
	
	public static int getInternalUserNumber() {
		return internalUserNumber;
	}
}
