package com.maxtree.tb4license.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.prefs.Preferences;

import javax.security.auth.x500.X500Principal;
//import de.schlichtherle.license.CipherParam;
//import de.schlichtherle.license.KeyStoreParam;
//import de.schlichtherle.license.LicenseContent;
//import de.schlichtherle.license.LicenseManager;
//import de.schlichtherle.license.LicenseParam;

import de.schlichtherle.license.CipherParam;
import de.schlichtherle.license.KeyStoreParam;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseParam;

/**
 * This class is used to generate new license file.
 * 
 * @author chens
 *
 */
public class LicenseServer {
	// TODO move this to the properties file
	private static final String APP_VERSION = "1.1";
	private static final String PROPERTIES_FILENAME = "DSLicenseServer.properties";

	// get these from properties file
	private String appName;
	private String dataFileExtension;
	private String licenseFileExtension;

	// keystore information (from properties file)
	private static String keystoreFilename; // this app needs the "private" keystore
	private static String keystorePassword;
	private static String keyPassword;
	private static String alias;
	private static String cipherParamPassword; // 6+ chars, and both letters and numbers

	// built by our app
	private final KeyStoreParam privateKeyStoreParam;
	private final CipherParam cipherParam;

	// exit status codes
	private static int EXIT_STATUS_ALL_GOOD = 0;
	private static int EXIT_STATUS_ERR_WRONG_NUM_ARGS = 1;
	private static int EXIT_STATUS_ERR_EXCEPTION_THROWN = 2;
	private static int EXIT_STATUS_ERR_CANT_READ_DATA_FILE = 3;
	private static int EXIT_STATUS_ERR_CANT_OUR_PROPERTIES_FILE = 4;

	// properties we get from the data file, and write to the license file
	private String firstName;
	private String lastName;
	private String city;
	private String state;
	private String country;

	public static void main(String[] args) {
		// should have one arg, and it should be the basename of the file(s)
//		if (args.length != 2) {
//			System.err.println("Need two args: [directory] [baseFilename]");
//			System.exit(EXIT_STATUS_ERR_WRONG_NUM_ARGS);
//		}

		args = new String[] {"output", "Customer"};
		
		// args ok, run program
		new LicenseServer(args[0], args[1], null);
	}

	public LicenseServer(final String directory, final String fileBasename, LicenseCheckModel model) {
		// load all the properties we need to run
		loadOurPropertiesFile();

		// read all the attributes from the data file for this customer
		loadInfoFromCustomerDataFile(directory, fileBasename);

		// set up an implementation of the KeyStoreParam interface that returns
		// the information required to work with the keystore containing the private
		// key:
		privateKeyStoreParam = new KeyStoreParam() {
			public InputStream getStream() throws IOException {
				final String resourceName = keystoreFilename;
				final InputStream in = getClass().getResourceAsStream(resourceName);
				if (in == null) {
					System.err.println("Could not load file: " + resourceName);
					throw new FileNotFoundException(resourceName);
				}
				return in;
			}

			public String getAlias() {
				return alias;
			}

			public String getStorePwd() {
				return keystorePassword;
			}

			public String getKeyPwd() {
				return keyPassword;
			}
		};

		// Set up an implementation of the CipherParam interface to return the password
		// to be
		// used when performing the PKCS-5 encryption.
		cipherParam = new CipherParam() {
			public String getKeyPwd() {
				return cipherParamPassword;
			}
		};

		// Set up an implementation of the LicenseParam interface.
		// Note that the subject string returned by getSubject() must match the subject
		// property
		// of any LicenseContent instance to be used with this LicenseParam instance.
		LicenseParam licenseParam = new LicenseParam() {
			public String getSubject() {
				return appName;
			}

			public Preferences getPreferences() {
				// TODO why is this needed for the app that creates the license?
				// return Preferences.userNodeForPackage(LicenseServer.class);
				Preferences preference = Preferences
						.userNodeForPackage(LicenseServer.class);
				return preference;
			}

			public KeyStoreParam getKeyStoreParam() {
				return privateKeyStoreParam;
			}

			public CipherParam getCipherParam() {
				return cipherParam;
			}
		};

		// create the license file
		LicenseManager lm = new LicenseManager(licenseParam);
		try {
			// write the file to the same directory we read it in from
			String filename = directory + "/" + fileBasename + licenseFileExtension;
			File outputDir = new File(directory);
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
			lm.store(createLicenseContent(licenseParam, model), new File(filename));
			System.exit(EXIT_STATUS_ALL_GOOD);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(EXIT_STATUS_ERR_EXCEPTION_THROWN);
		}
	}

	/**
	 * Load the general properties this application needs in order to run.
	 */
	private void loadOurPropertiesFile() {
		Properties properties = new Properties();
		InputStream in;
		try {
			in = LicenseServer.class.getResourceAsStream(PROPERTIES_FILENAME);
//			in = new FileInputStream(PROPERTIES_FILENAME);
			properties.load(in);
			in.close();
			appName = properties.getProperty("app_name");
			dataFileExtension = properties.getProperty("data_file_extension");
			licenseFileExtension = properties.getProperty("license_file_extension");
			keystoreFilename = properties.getProperty("keystore_filename");
			keystorePassword = properties.getProperty("keystore_password");
			alias = properties.getProperty("alias");
			keyPassword = properties.getProperty("key_password");
			cipherParamPassword = properties.getProperty("cipher_param_password");
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(EXIT_STATUS_ERR_CANT_OUR_PROPERTIES_FILE);
		}
	}

	/**
	 * Read the data file that has information about the current customer.
	 * 
	 * @param directory
	 *            The directory where the properties file is located.
	 * @param fileBasename
	 *            The base portion of the filename.
	 */
	private void loadInfoFromCustomerDataFile(final String directory, final String fileBasename) {
		Properties properties = new Properties();
		InputStream in;
		try {
//			in = new FileInputStream(directory + "/" + fileBasename + dataFileExtension);
			in = LicenseServer.class.getResourceAsStream(fileBasename + dataFileExtension);
			properties.load(in);
			in.close();
			firstName = properties.getProperty("first_name");
			lastName = properties.getProperty("last_name");
			city = properties.getProperty("city");
			state = properties.getProperty("state");
			country = properties.getProperty("country");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(EXIT_STATUS_ERR_CANT_READ_DATA_FILE);
		}
	}

	// Set up the LicenseContent instance. This is the information that will be in
	// the
	// generated license file.

	public LicenseContent createLicenseContent(LicenseParam licenseParam, LicenseCheckModel model) {
		LicenseContent result = new LicenseContent();
		X500Principal holder = new X500Principal(
				"CN=" + firstName + " " + lastName + ", " + "L=" + city + ", " + "ST=" + state + ", " + "C=" + country);
		result.setHolder(holder);
		X500Principal issuer = new X500Principal("CN=forerunner2018@hotmail.com, L=Dalian, ST=Liaoning, "
				+ " OU=Maxtree Software," + " O=DevDaily Interactive," + " C=China," + " DC=CN");
		result.setIssuer(issuer);
		// i'm selling one license
		result.setConsumerAmount(1);
		// i think this needs to be "user"
		result.setConsumerType("User");
		result.setInfo("License key for the " + appName + " application.");
		Date now = new Date();
		result.setIssued(now);
		
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(now);
//		int year = cal.get(Calendar.YEAR);
//		int month = cal.get(Calendar.MONTH);
//		int day = cal.get(Calendar.DAY_OF_MONTH);
//		
//		cal.set(Calendar.MONTH, month + 1);
//		cal.set(Calendar.YEAR, year + 1);
//		now.setYear(now.getYear() + 50);
		
		result.setNotBefore(model.getFrom());
		result.setNotAfter(model.getTo());
		result.setSubject(licenseParam.getSubject());
		
		Map<String, String> someinfo = new HashMap<String, String>();
		someinfo.put("MacAddress", model.getMacAddress());
		someinfo.put("IP", model.getIpAddress());
		someinfo.put("HostName", model.getHostName());
		someinfo.put("DiskSerialNumber", model.getHardDiskSerialNumber());
		someinfo.put("MotherboardSerialNumber", model.getMotherboardSerialNumber());
		result.setExtra(someinfo);
		 
		return result;
	}

}
