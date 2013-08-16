package com.msi.tough.monitor.common.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

/**
 * This is our internal representation of a virtualized instance. We need to be
 * able to identify this by ID, NAME, or UUID, and track it by any of that
 * information.
 * 
 * @author heathm
 * 
 */
public class VirtualMachineInstance {
	private String id;
	private String name;
	private UUID uuid;
	private Region region;
	private boolean initialized = false;
	private long lastInitialized = 0;
	private final List<String> driveIds = new ArrayList<String>();
	private final List<String> networkIds = new ArrayList<String>();

	public VirtualMachineInstance() {
	}
	
	public VirtualMachineInstance(String id, String name, UUID uuid) {
		this.id = id;
		this.name = name;
		this.uuid = uuid;
	}

//	public VirtualMachineInstance(int id, String name, UUID uuid) {
//		this.id = String.valueOf(id);
//		this.name = name;
//		this.uuid = uuid;
//	}

	public List<String> getDriveIds() {
		return driveIds;
	}

//	public int getId() {
//		return Integer.valueOf(id);
//	}
//	

	public long getLastInitialized() {
		return lastInitialized;
	}

	public String getName() {
		return name;
	}

	public List<String> getNetworkIds() {
		return networkIds;
	}

	public Region getRegion() {
		return region;
	}

	public UUID getUuid() {
		return uuid;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setDriveIds(List<String> driveIds) {
		this.driveIds.clear();
		for (String dId : driveIds) {
			this.driveIds.add(dId);
		}
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setInitialized(boolean initialized) {
		if (initialized) {
			lastInitialized = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis();
		}
		this.initialized = initialized;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNetworkIds(List<String> networkIds) {
		this.networkIds.clear();
		for (String nId : networkIds) {
			this.networkIds.add(nId);
		}
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

}
