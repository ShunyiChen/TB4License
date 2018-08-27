package com.maxtree.tb4license.client.example;

import java.io.Serializable;
import java.util.Date;

public class DCRuntime implements Serializable {

	public DCRuntime(Date d) {
		this.d = d;
	}
	
	public Date getIdal() {
		return d;
	}
	
	private Date d;
}
