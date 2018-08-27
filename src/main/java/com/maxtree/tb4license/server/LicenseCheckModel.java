package com.maxtree.tb4license.server;

import java.util.Date;

public class LicenseCheckModel {

	/**
	 * 
	 * @param macAddress
	 * @param ipAddress
	 * @param hostName
	 * @param hardDiskSerialNumber
	 * @param motherboardSerialNumber
	 * @param from
	 * @param to
	 */
	public LicenseCheckModel(String macAddress, String ipAddress, String hostName, String hardDiskSerialNumber, String motherboardSerialNumber, Date from, Date to) {
		this.macAddress = macAddress;
		this.ipAddress = ipAddress;
		this.hostName = hostName;
		this.hardDiskSerialNumber = hardDiskSerialNumber;
		this.motherboardSerialNumber = motherboardSerialNumber;
		this.from = from;
		this.to = to;
	}
	
	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getHardDiskSerialNumber() {
		return hardDiskSerialNumber;
	}

	public void setHardDiskSerialNumber(String hardDiskSerialNumber) {
		this.hardDiskSerialNumber = hardDiskSerialNumber;
	}

	public String getMotherboardSerialNumber() {
		return motherboardSerialNumber;
	}

	public void setMotherboardSerialNumber(String motherboardSerialNumber) {
		this.motherboardSerialNumber = motherboardSerialNumber;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

	private String macAddress;
	private String ipAddress;
	private String hostName;
	private String hardDiskSerialNumber;
	private String motherboardSerialNumber;
	private Date from;
	private Date to;

}
