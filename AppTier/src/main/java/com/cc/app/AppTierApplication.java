package com.clocomp.apptier;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class AppTierApplication {
	private static final Logger SystemLogger = LogManager.getLogger(AppTierApplication.class);
	public static void main(String[] args) {
		SystemLogger.info("Initialized AppTier");
		AppTierInitializer Initializer = new AppTierInitializer();
		Initializer.run();
		SystemLogger.info("Completed AppTier");
	}

}
