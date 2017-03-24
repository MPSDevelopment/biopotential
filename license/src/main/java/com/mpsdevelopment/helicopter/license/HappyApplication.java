package com.mpsdevelopment.helicopter.license;

import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

public class HappyApplication {

	private static Logger LOGGER = LoggerUtil.getLogger(HappyApplication.class);

	private static final LicenseCreator LICENSE_CREATOR = new LicenseCreator();

	public static void main(String args[]) throws Exception {
		// LICENSE_CREATOR.createLicense(LICENSE_EXPIRE_DATE);
		LICENSE_CREATOR.checkArguments(args);

		LOGGER.info("License will work till %s", com.mpsdevelopment.plasticine.commons.DateUtils.utilDateToString(LICENSE_CREATOR.getLicenseExpirationDate(), "dd.MM.yyyy"));
	}
}
