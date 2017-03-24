package com.mpsdevelopment.helicopter.license;

import java.net.SocketException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.mockito.Mockito.*;

import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LicenseTest {

	private static final Logger LOGGER = LoggerUtil.getLogger(LicenseTest.class);

	private Date releaseDate = new Date();

	@Spy
	private LicenseCreator licenseCreator = new LicenseCreator();

	@Before
	public void before() {
		licenseCreator.deleteLicense();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void checkLicense() throws InterruptedException {
		String mac = licenseCreator.getMacAddress();
		LOGGER.info("Mac is %s", mac);

		Assert.assertFalse(licenseCreator.checkLicense(releaseDate));

		releaseDate = DateUtils.addMilliseconds(new Date(), 1000);
		licenseCreator.createLicense(releaseDate);

		Assert.assertTrue(licenseCreator.checkLicense(releaseDate));
		Thread.sleep(1000);
		Assert.assertFalse(licenseCreator.checkLicense(releaseDate));

		licenseCreator.checkLicense(releaseDate = DateUtils.addDays(new Date(), 2));

		when(licenseCreator.getCurrentDate()).thenReturn(DateUtils.addHours(new Date(), 1));

		Assert.assertTrue(licenseCreator.checkLicense(releaseDate));

		when(licenseCreator.getCurrentDate()).thenReturn(new Date());

		Assert.assertFalse(licenseCreator.checkLicense(releaseDate));

		licenseCreator.makeHappy(new String[] { "-makemehappy20" });
		
		
	}

	@Test
	public void deleteLicense() throws InterruptedException {
		checkLicense();
		licenseCreator.deleteLicense();
		Assert.assertTrue(licenseCreator.checkLicense(releaseDate));
		licenseCreator.deleteLicense();
		Assert.assertFalse(licenseCreator.checkLicense(releaseDate, false));
		Assert.assertFalse(licenseCreator.checkIfLicenseFileExists());
	}

	@Test
	public void checkGetIPAddresses() throws SocketException {
		List<String> ipAddresses = licenseCreator.getIpAddresses();
		for (String address : ipAddresses) {
			LOGGER.info("IP is %s", address);
		}
	}
}