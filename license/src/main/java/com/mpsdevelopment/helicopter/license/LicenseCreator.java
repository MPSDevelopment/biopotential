package com.mpsdevelopment.helicopter.license;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.h2.store.fs.FileUtils;

import com.mpsdevelopment.plasticine.commons.DateUtils;
import com.mpsdevelopment.plasticine.commons.RegExpUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

public class LicenseCreator {

	private static final Logger LOGGER = LoggerUtil.getLogger(LicenseCreator.class);

	public final String licenseFilename;

	private StreamCoder streamCoder = new StreamCoder();

	private License license;

	public LicenseCreator() {
		String path = String.format("%s", System.getProperty("user.home"));
		licenseFilename = String.format("%s/%s", path, "very/happy.me");
		LOGGER.debug("License filename is %s", licenseFilename);
	}

	/**
	 * @param licenseExpireDate
	 *            - date of license end for saving in license file.
	 * @param releaseEndDate
	 *            - date of release end for saving in license file.
	 * @return true if license file was created on flash drive.
	 */
	public boolean createLicense(Date licenseExpireDate) {
		license = new License().setId(generateId()).setExpirationDate(licenseExpireDate).setLastStartDate(getCurrentDate());
		if (!checkIfLicenseFileExists()) {
			FileUtils.createFile(licenseFilename);
		}
		streamCoder.getWithPadding().symmetricEncrypt(JsonUtils.getCompactLicenseJson(license), new File(licenseFilename));
		LOGGER.debug("Creating license file for id %s, expiration date is %s", license.getId(), license.getExpirationDate());
		return true;
	}

	// public void updateLicense() {
	// if (!checkIfLicenseFileExists()) {
	// FileUtils.createFile(licenseFilename);
	// }
	// }

	public void deleteLicense() {
		File file = new File(licenseFilename);
		if (file.exists()) {
			FileUtils.delete(licenseFilename);
		} else {
			LOGGER.debug("License file %s has been already deleted", licenseFilename);
		}
	}

	/**
	 * Used before running InternetUrok main application for copy protection.
	 *
	 * @return true if license file is on drive and is actual.
	 */
	public boolean checkLicense(Date releaseDate) {
		return checkLicense(licenseFilename, releaseDate, true);
	}

	public boolean checkLicense(Date releaseDate, boolean createIfNotExists) {
		return checkLicense(licenseFilename, releaseDate, createIfNotExists);
	}

	/**
	 * @return true if license file exists.
	 */
	public boolean checkIfLicenseFileExists() {
		File file = new File(licenseFilename);
		return file.exists();
	}

	/**
	 * Create a license file
	 * 
	 * @param licenseFile
	 * @param releaseDate
	 * @param createIfNotExists
	 * @return
	 */
	public boolean checkLicense(String licenseFile, Date releaseDate, boolean createIfNotExists) {
		Date date = getCurrentDate();
		LOGGER.info("releaseDate is %s", releaseDate);
		LOGGER.info("current date is %s", date);

		if (date.after(releaseDate)) {
//			LOGGER.debug("Release date has expired");
			LOGGER.info("Release date has expired");
			return false;
		}

		if (createIfNotExists) {
			if (!checkIfLicenseFileExists()) {
				createLicense(releaseDate);
				LOGGER.info("Profile data file has been created");
			} else {
				LOGGER.info("Profile data file already exists");
			}
		}

		String id = generateId();
		if (StringUtils.isBlank(id)) {
			return false;
		}
		LOGGER.debug("License file path: %s", licenseFile);
		if (checkIfLicenseFileExists()) {
			File file = new File(licenseFile);
			String result = streamCoder.getWithPadding().symmetricDecrypt(file);
			if (StringUtils.isNotBlank(result)) {
				license = JsonUtils.fromJson(License.class, result, JsonUtils.DATE_FORMAT_VERSION);
				LOGGER.debug("Data successfully decrypted");

				// updating expiration date if there is need to
				if (license.getExpirationDate().before(releaseDate)) {
					license.setExpirationDate(releaseDate);
				}

				if (!license.getId().equals(id)) {
					LOGGER.debug("License id is not equal");
					return false;
				}
				Date expirationDate = license.getExpirationDate();
				LOGGER.debug("Current license date is %s", DateUtils.utilDateToString(expirationDate, "dd.MM.yyyy"));
				if (date.after(expirationDate)) {
					LOGGER.debug("License too old");
					return false;
				}
				Date lastStartDate = license.getLastStartDate();
				LOGGER.debug("Last start date is %s", DateUtils.utilDateToString(lastStartDate, "dd.MM.yyyy"));
				if (date.before(lastStartDate)) {
					LOGGER.debug("System time was changed");
					return false;
				}
				license.setLastStartDate(date);
				// saving changes to license
				saveLicense();
				return true;
			} else {
				LOGGER.debug("License file is empty");
			}
		} else {
			LOGGER.debug("License file does not exist");
		}
		return false;
	}

	public Date getCurrentDate() {
		return new Date();
	}

	private String generateId() {
		String mac = getMacAddress();
		String name = getComputerName();
		if (StringUtils.isBlank(mac) || StringUtils.isBlank(name)) {
			return null;
		}
		return String.format("%s-%s", mac, name);
	}

	private void saveLicense() {
		streamCoder.getWithPadding().symmetricEncrypt(JsonUtils.getCompactLicenseJson(license), new File(licenseFilename));
	}

	public String getMacAddress() {
		try {
			InetAddress ip = InetAddress.getLocalHost();
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			byte[] mac = network.getHardwareAddress();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
			String macAddress = sb.toString();
			LOGGER.debug("Max address is %s", macAddress);
			return macAddress;
		} catch (Throwable e) {
			LOGGER.error("Cannot get mac address");
		}
		return null;
	}

	public String getComputerName() {
		try {
			InetAddress address = InetAddress.getLocalHost();
			String hostname = address.getHostName();
			LOGGER.debug("Hostname is %s", hostname);
			return hostname;
		} catch (Throwable e) {
			LOGGER.error("Cannot get mac address");
		}
		return null;
	}

	public String getIpAddress() {
		try {
			InetAddress address = InetAddress.getLocalHost();
			String ip = address.getHostAddress();
			LOGGER.debug("Host address is %s", ip);
			return ip;
		} catch (Throwable e) {
			LOGGER.error("Cannot get mac address");
		}
		return null;
	}

	public List<String> getIpAddresses() throws SocketException {
		List<String> addresses = new ArrayList<String>();

		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface networkInterface = networkInterfaces.nextElement();
			if (networkInterface.isUp()) {
				Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					InetAddress inetAddress = inetAddresses.nextElement();
					addresses.add(inetAddress.getHostName());
				}
			}
		}
		return addresses;
	}

	public void checkArguments(String[] args) {
		Date licenseExpireDate = makeHappy(args);
		if (licenseExpireDate != null) {
			createLicense(licenseExpireDate);
		}
	}

	public Date makeHappy(String[] args) {
		for (String s : args) {
			if (StringUtils.startsWithIgnoreCase(s, "-makemehappy")) {

				String additionalDays = RegExpUtils.getTextGroup(s, "-makemehappy([0-9]*)", 1);

				if (StringUtils.isNumeric(additionalDays)) {
					Date addDays = org.apache.commons.lang3.time.DateUtils.addDays(new Date(), new Integer(additionalDays));
					LOGGER.info("Expiration date will be %s", DateUtils.utilDateToString(addDays, "dd.MM.yyyy"));
					return addDays;
				}
				LOGGER.error("Additional days is not numeric %s", additionalDays);
			} else if (StringUtils.equalsIgnoreCase(s, "-howhappy")) {
				LOGGER.debug("Expiration date is %s", DateUtils.utilDateToString(license.getExpirationDate(), "dd.MM.yyyy"));
			}
		}
		return null;
	}

	public Date getLicenseExpirationDate() {
		if (license != null) {
			return license.getExpirationDate();
		}
		return null;
	}
}
