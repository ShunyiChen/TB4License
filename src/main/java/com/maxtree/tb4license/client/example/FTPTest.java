package com.maxtree.tb4license.client.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import com.maxtree.tb4license.server.LicenseServer;

import de.schlichtherle.license.CipherParam;
import de.schlichtherle.license.KeyStoreParam;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.ftp.LicenseManager;
import de.schlichtherle.license.ftp.LicenseParam;
import de.schlichtherle.util.ObfuscatedString;

public class FTPTest {

	public FTPTest() {
		
		
		initFtpKeyStoreParam();
		
		
		// set up an implementation of the KeyStoreParam interface that returns
		// the information required to work with the keystore containing the private
		// key:
		publicKeyStoreParam = new KeyStoreParam() {
			public InputStream getStream() throws IOException {
				final String resourceName = keystoreFilename;
				final InputStream in = LicenseServer.class.getResourceAsStream(resourceName);
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
				return publicCertStorePassword;
			}

			public String getKeyPwd() {
				return null;
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
		licenseParam = new LicenseParam() {
			public String getSubject() {
				return appName;
			}

			public Preferences getPreferences() {
				// TODO why is this needed for the app that creates the license?
				// return Preferences.userNodeForPackage(LicenseServer.class);
				Preferences preference = Preferences.userNodeForPackage(LicenseServer.class);
				return preference;
			}

			public KeyStoreParam getKeyStoreParam() {
				return publicKeyStoreParam;
			}

			public CipherParam getCipherParam() {
				return cipherParam;
			}

			public KeyStoreParam getFTPKeyStoreParam() {
				// TODO Auto-generated method stub
				return ftpKeyStoreParam;
			}

			public int getFTPDays() {
				// TODO Auto-generated method stub
				return 1;
			}

			public boolean isFTPEligible() {
				// TODO Auto-generated method stub
				return false;
			}

			public LicenseContent createFTPLicenseContent() {
				// TODO Auto-generated method stub
				/*
				 * I found in the TLC source code that they just used a simple string for this,
				 * so that's what I did here.
				 */
				/*
				 * =>
				 * "This is a Free Trial Period (FTP) license for the devdaily.com Hyde application, v1.x"
				 */
				String LICENSE_CONTENT_INFO = new ObfuscatedString(
						new long[] { 0x65C7CC3ADE5CDB2EL, 0x1B2A5CF871C1BBAFL, 0xFBBFFE0B09A8B2D9L, 0x48F224E807AA9897L,
								0x1AA5E8307BC02667L, 0xF01FF56A26658F79L, 0x6B38BB8DDE53CC69L, 0xCBF34E28A2BC6E85L,
								0x59626A2A2B0B8FBL, 0xD4E0ACC31AC8BC4DL, 0x56341EAEAA8AF522L, 0xBA6D890689A9017L })
										.toString();

				LicenseContent content = new LicenseContent();
				content.setInfo(LICENSE_CONTENT_INFO);
				return content;
			}

			public void removeFTPEligibility() {
				// TODO Auto-generated method stub
				createAllCookieFilesDirsAndStrings();
			}

			public void ftpGranted(LicenseContent content) {
				// TODO Auto-generated method stub
				JOptionPane.showMessageDialog(null, 
			            "A 10-day Free Trial Period license has been installed. Enjoy!", 
			            "Hyde Trial License", 
			            JOptionPane.INFORMATION_MESSAGE);
			}
		};

		lm = new LicenseManager(licenseParam);
	}

	/**
	 * 
	 * @throws Exception
	 */
	private void install() throws Exception {
		lm.install(new File("output/Customer.lic"));
	}

	/**
	 * 
	 * @return
	 */
	private boolean verify() {
		try {
			install();
			LicenseContent content = lm.verify();

			System.out.println(content.getExtra() + "," + content.getInfo() + "," + content.getNotBefore() + ","
					+ content.getNotAfter());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * ********** FTP *********** As near as I can tell from the docs, I have to
	 * implement this so the FTP package can install a temporary "FTP" license. I've
	 * created an "ftp alias" just for this; everything else is the same as the
	 * 'publicKeyStoreParam', as we share the same public key store file.
	 */
	private void initFtpKeyStoreParam() {
		// the same thing as 'publicKeyStoreParam', but do this for the ftpKey.
		ftpKeyStoreParam = new KeyStoreParam() {
			public InputStream getStream() throws IOException {
				// same as other class, as we are using the same keystore
				// return licenseableClass.getPublicKeystoreAsInputStream();
				final String resourceName = keystoreFilename;
				final InputStream in = LicenseServer.class.getResourceAsStream(resourceName);
				if (in == null) {
					System.err.println("Could not load file: " + resourceName);
					throw new FileNotFoundException(resourceName);
				}
				return in;
			}

			public String getAlias() {
				// this is the alias for the ftp key
				return ftpAlias;
			}

			public String getStorePwd() {
				// same as 'publicKeyStoreParam' (using same public key store)
				return publicCertStorePassword;
			}

			public String getKeyPwd() {
				// for ftp purposes we need the password for this keystore
				return ftpKeyPwd;
			}
		};
	}
	
	  // ***************** COOKIE FILES AND SERIALIZATION STUFF *********************//
	  
	private void writeSerializedFiles() {
		DCRuntime di1 = new DCRuntime(new Date());
		serializeObjectToFile(di1, varTmpFullFilename1);
		File f1 = new File(varTmpFullFilename1);
		f1.setLastModified(getRandomTimestampForFile());

		DCRuntime di2 = new DCRuntime(new Date());
		serializeObjectToFile(di2, varTmpFullFilename2);
		File f2 = new File(varTmpFullFilename2);
		f2.setLastModified(getRandomTimestampForFile());
	}

	/**
	 * This will return a date from one of our serialized files, or it will return
	 * null if everything fails.
	 */
	private Date getInstallationDateFromSerializedFiles() {
		try {
			DCRuntime d1 = (DCRuntime) getObjectBackFromSerializedFile(varTmpFullFilename1);
			if (d1 != null && d1.getIdal() != null)
				return d1.getIdal();

			DCRuntime d2 = (DCRuntime) getObjectBackFromSerializedFile(varTmpFullFilename1);
			if (d2 != null && d2.getIdal() != null)
				return d2.getIdal();
		} catch (RuntimeException re) {
			// ignore
		}
		return null;
	}

	/**
	 * Call this when the application is first "initialized".
	 */
	private void createAllCookieFilesDirsAndStrings() {
		writeSerializedFiles();
		createDirectoryAndInitFile(homeLibraryDirname, homeLibraryFilename);
		createDirectoryAndInitFile(homeLibraryLogsDirname, homeLibraryLogsFilename);
		createDirectoryAndInitFile(homeLibraryCachesDirname, homeLibraryCachesFilename);
		createDirectoryAndInitFile(homeLibraryApplicationSupportDirname, homeLibraryApplicationSupportFilename);

		// create the "Sounds" folder; this was a late addition, and was moved here (a)
		// so it would happen
		// after licensing, and (b) not interfere with our license file/dir checks
		makeDirectories(CANON_SOUNDS_DIR);

		// intentionally write to /var/log/system.log
		logger.logDebug(VAR_SYSTEM_LOG_FTP_INSTALLED_STRING);
	}

	/**
	 * If *any* of our known application files exist (our "cookies"), return true.
	 */
	private boolean appFilesOrFoldersExist() {
		String absPath = getAbsoluteUserHomeDir(homeLibraryDirname);
		File file = new File(absPath);
		if (file.exists())
			return true;

		absPath = getAbsoluteUserHomeDir(homeLibraryLogsDirname);
		file = new File(absPath);
		if (file.exists())
			return true;

		absPath = getAbsoluteUserHomeDir(homeLibraryCachesDirname);
		file = new File(absPath);
		if (file.exists())
			return true;

		absPath = getAbsoluteUserHomeDir(homeLibraryApplicationSupportDirname);
		file = new File(absPath);
		if (file.exists())
			return true;

		absPath = getAbsoluteUserHomeDir(varTmpFullFilename1);
		file = new File(absPath);
		if (file.exists())
			return true;

		absPath = getAbsoluteUserHomeDir(varTmpFullFilename2);
		file = new File(absPath);
		if (file.exists())
			return true;

		return false;
	}

	/**
	 * Build a String that contains the full path to folder in the user's home
	 * directory.
	 */
	private String getAbsoluteUserHomeDir(String relativeDir) {
		String homeDir = System.getProperty("user.home");
		return homeDir + FILE_PATH_SEPARATOR + relativeDir;
	}

	/**
	 * Returns true if creating the relativeDir succeeded.
	 * 
	 * @param relativeDir
	 *            A directory path that is relative to the user's home directory.
	 *            Should not begin with a "/".
	 * @param relativeFilename
	 *            Just a filename, with no leading directory information.
	 * @return
	 */
	private boolean createDirectoryAndInitFile(String relativeDir, String relativeFilename) {
		String homeDir = System.getProperty("user.home");
		String canonDirname = homeDir + FILE_PATH_SEPARATOR + relativeDir;
		File folderToCreate = new File(canonDirname);
		System.out.println(folderToCreate.getPath());
		boolean succeeded = folderToCreate.mkdirs();
		// System.out.format("Creating %s succeeded: %s\n", canonDirname, succeeded);
		String canonFilename = canonDirname + FILE_PATH_SEPARATOR + relativeFilename;
		File f = new File(canonFilename);
		writeToFile(f, "");
		return succeeded;
	}

	/**
	 * Create the directory given by the absolute path.
	 */
	private boolean makeDirectories(String absoluteDirPath) {
		File folderToCreate = new File(absoluteDirPath);
		boolean succeeded = folderToCreate.mkdirs();
		return succeeded;
	}

	private Date getCurrentDate() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTime();
	}

	private void writeToFile(File file, String content) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(content);
			out.close();
		} catch (IOException e) {
			// do nothing
		}
	}

	// this method has been changed
	private long getRandomTimestampForFile() {
		Random r = new Random(getCurrentDate().getTime());
		// get a random number between 0 and 44
		int randomInt = r.nextInt(45);
		// make this 30-75 days ago
		int daysAgo = randomInt + 30;
		daysAgo = 0 - daysAgo;

		// use this value to write the time stamp on the file
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, daysAgo);
		Date semiRandomDate = calendar.getTime();
		return semiRandomDate.getTime();
	}

	private void serializeObjectToFile(Serializable s, String filename) {
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(filename);
			out = new ObjectOutputStream(fos);
			out.writeObject(s);
			out.close();
		} catch (IOException ex) {
			logger.logError("License:serialize(), exception thrown.");
		}
	}

	private Object getObjectBackFromSerializedFile(String filename) {
		Object object = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(filename);
			in = new ObjectInputStream(fis);
			object = in.readObject();
			in.close();
			return object;
		} catch (IOException ex) {
			logger.logError("License:deSerialize(), IOException thrown.");
			return null;
		} catch (ClassNotFoundException ex) {
			logger.logError("License:deSerialize(), ClassNotFoundException thrown.");
			return null;
		}
	}

	/**
	 * Returns true if there are actual preferences stored for the given class,
	 * otherwise, return false. Note that at least one preference must be stored for
	 * the given class for this method to return true.
	 */
	private boolean preferencesExistAndContainKeys(Class theClass) {
		Preferences preferences = Preferences.userNodeForPackage(theClass);

		if (preferences == null)
			return false;

		try {
			String[] keys = preferences.keys();
			if (keys == null)
				return false;
			if (keys.length <= 0)
				return false;
			// made it through those checks; keys must exist and have length > 0
			return true;
		} catch (BackingStoreException e) {
			// err on the conservative side, and say that the prefs do not exist
			return false;
		}
	}
	
	public static void main(String[] args) {
		// String s = ObfuscatedString.obfuscate("Wonderful2018");
		// System.out.println(s);
		FTPTest test = new FTPTest();
		test.verify();
	}

	// get these from properties file
	private static String appName = new ObfuscatedString(new long[] { 0xFCB6F2690E6E9362L, 0x9EBBE88B5445DAAAL })
			.toString();
	private static String keystoreFilename = new ObfuscatedString(
			new long[] { 0xD5A5D268E12746FFL, 0x22E6DDE70972E4C8L, 0xFA81119917BCC38AL, 0xAC5664A12D707BBDL })
					.toString();
	private static String alias = new ObfuscatedString(
			new long[] { 0xE1325BDC382749EEL, 0xD1C9C845F8AC6AC4L, 0xE6BD06CF302F1883L }).toString();
	private static String publicCertStorePassword = new ObfuscatedString(
			new long[] { 0x11D9DA41CF72663FL, 0x3CA93D3B48ABAAA4L, 0x47E3B0E94341AF6DL }).toString();
	private static String cipherParamPassword = new ObfuscatedString(
			new long[] { 0xCF899EE49F709ACAL, 0x954921EB531BF4BFL, 0x3D73B0811299826DL }).toString();

	// built by our app
	private final KeyStoreParam publicKeyStoreParam;
	// FTP config
	private KeyStoreParam ftpKeyStoreParam;
	private static String ftpAlias = new ObfuscatedString(
			new long[] { 0xA4B0FBA523B3F63AL, 0x65D5A72B8657E00BL, 0x9E71F6D2395FF6FL })
					.toString(); /* => "dcftpkeyv1.x" */
	private static String ftpKeyPwd = new ObfuscatedString(
			new long[] { 0xA2B75E0F17691BD4L, 0xEB9DB7C881078526L, 0x5ED8D7B254BC11BEL, 0x4422FAD5CD5DC734L })
					.toString(); /* => "J4V D3RB 88743 1411" */

	private LicenseParam licenseParam;
	private final CipherParam cipherParam;
	private LicenseManager lm = null;
	// logging
	private DDLoggerInterface logger = new DDSimpleLogger("log.txt", 0, false, false);
	
	// this is a cheesy way to see if the app has been used before with a free trial
	// period.
	// serialize the DateInfo class to these two files; use these as a check to see
	// if the app
	private String varTmpFullFilename1 = new ObfuscatedString(
			new long[] { 0xD31C760B010F2759L, 0x3F6C53524EBB278BL, 0xA3CFC17D069F19A9L, 0xF582D86B9A3638FDL })
					.toString(); /* => "/var/tmp/.dc41026417" */
	private String varTmpFullFilename2 = new ObfuscatedString(new long[] { 0x7E8826DCC70F33A2L, 0x465D20C13295A4B5L,
			0x64C40A1904AB9EFEL, 0xE0EC6D90574B89BCL, 0xBFFB1B1A52BC9489L })
					.toString(); /* => "/var/tmp/.dd45f488df95c99" */
	// ~/Library/DevDaily/DesktopCurtain
	private static final String homeLibraryDirname = "Library/DevDaily/Hyde";
	private static final String homeLibraryFilename = "Hyde.library";

	// ~/Library/Logs/DevDaily/DesktopCurtain
	private static final String homeLibraryLogsDirname = "Library/Logs/DevDaily/Hyde";
	private static final String homeLibraryLogsFilename = "Hyde.log";

	// ~/Library/Caches/DevDaily/DesktopCurtain
	private static final String homeLibraryCachesDirname = "Library/Caches/DevDaily/Hyde";
	private static final String homeLibraryCachesFilename = "Hyde.caches";

	// ~/Library/Caches/DevDaily/DesktopCurtain
	private static final String homeLibraryApplicationSupportDirname = "Library/Support/DevDaily/Hyde";
	private static final String homeLibraryApplicationSupportFilename = "Hyde.support";
  
  
	private static String FILE_PATH_SEPARATOR = "/";
	private static String CANON_SOUNDS_DIR = "Sounds";
	private static final String VAR_SYSTEM_LOG_FTP_INSTALLED_STRING = new ObfuscatedString(new long[] {0xAD6AB058091EF6E0L, 0xE86EB2CB0572AAB4L, 0x36CF7BC7763B4E18L, 0xC00C58D879B611FBL, 0xE519A9AB44BF1E04L}).toString(); /* => "HYDE FTP License v1.1 installed" */
	 
}
