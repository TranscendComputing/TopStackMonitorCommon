package com.msi.tough.monitor.common.manager.account;

import java.util.List;

import com.msi.tough.monitor.common.model.VirtualMachineInstance;

public interface AccountMapper {
	public List<VirtualMachineInstance> getUserVisibleZone(String accountId, String zoneName);
	public List<VirtualMachineInstance> getUserVisibleScaleGroup(String accountId, String groupName);
	public List<VirtualMachineInstance> getUserVisibleInstances(String accountId, List<VirtualMachineInstance> instances);
	public List<VirtualMachineInstance> getAllUserVisibleInstances(String accountId);
	public String getUserByAccountId(long acid);
}
