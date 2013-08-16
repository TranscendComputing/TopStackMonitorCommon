package com.msi.tough.monitor.common.model;

/**
 * Used as a dividing line to not aggregate across.
 * @author heathm
 *
 */
public class Region {
	private String name;
	private String endPoint;
	
	public String getName() {
		return name;
	}
	public String getEndPoint() {
		return endPoint;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	} 
	
}
