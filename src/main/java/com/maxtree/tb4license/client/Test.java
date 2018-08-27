package com.maxtree.tb4license.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.prefs.Preferences;

import com.maxtree.tb4license.server.Getinfo;
import com.maxtree.tb4license.server.LicenseServer;

import de.schlichtherle.license.CipherParam;
import de.schlichtherle.license.KeyStoreParam;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseParam;
import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.util.ObfuscatedString;

/**
 * This class is used to verify the license
 * 
 * @author chens
 *
 */
public class Test {

	public Test() {
		
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
				return publicKeyStoreParam;
			}

			public CipherParam getCipherParam() {
				return cipherParam;
			}
		};
		
		lm = new LicenseManager(licenseParam);
	}
	
	private void install() throws Exception {
		lm.install(new File("output/Customer.lic"));
	}
	
	public boolean verify() {
		try {
			install();
			LicenseContent content = lm.verify();
//			System.out.println(content.getExtra()+","+content.getInfo()+","+content.getNotBefore()+","+content.getNotAfter());
			Map<String, String> info = (Map<String, String>) content.getExtra();
			String macAddr = info.get("MacAddress");
			String ipAddr = info.get("IP");
			String hostname = info.get("HostName");
			String diskSerial = info.get("DiskSerialNumber");
			String motherboardSerial = info.get("MotherboardSerialNumber");
			
			Getinfo getinfo = new Getinfo();
			if (macAddr.equals(getinfo.getMacAddress())
					&& ipAddr.equals(getinfo.getLocalHostLANAddress())
					&& hostname.equals(getinfo.getComputerName())
					&& diskSerial.equals(getinfo.getDriveSerialNumber("C"))
					&& motherboardSerial.equals(getinfo.getMotherboardSN())) {
				return true;
			}
			else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	public static void main(String[] args) {
//		String s = ObfuscatedString.obfuscate("TB4");
//		System.out.println(s);
		
		Test test = new Test();
		test.verify();
	}
	
	// get these from properties file
	private static String appName = new ObfuscatedString(new long[] {0x6B09B86220DA8A98L, 0xA01E0AEBE14F5AF6L}).toString();
	private static String keystoreFilename = new ObfuscatedString(new long[] {0xD5A5D268E12746FFL, 0x22E6DDE70972E4C8L, 0xFA81119917BCC38AL, 0xAC5664A12D707BBDL}).toString();
	private static String alias = new ObfuscatedString(new long[] {0xE1325BDC382749EEL, 0xD1C9C845F8AC6AC4L, 0xE6BD06CF302F1883L}).toString();
	private static String publicCertStorePassword = new ObfuscatedString(new long[] {0x11D9DA41CF72663FL, 0x3CA93D3B48ABAAA4L, 0x47E3B0E94341AF6DL}).toString();
	private static String cipherParamPassword = new ObfuscatedString(new long[] {0xCF899EE49F709ACAL, 0x954921EB531BF4BFL, 0x3D73B0811299826DL}).toString();
	
	// built by our app
	private final KeyStoreParam publicKeyStoreParam;
	private final CipherParam cipherParam;
	private LicenseManager lm = null;

}
