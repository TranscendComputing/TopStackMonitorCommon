package com.msi.tough.monitor.common.manager;

import java.util.List;

import com.msi.tough.model.monitor.MeasureBean;
import com.msi.tough.monitor.common.model.exception.MSIMonitorException;

/**
 * Handles storage of Measure data by it's implementation type.
 * 
 * @author heathm
 */
public interface MeasureHandler {
	//
	// public void checkAlarmThresholds(Object session);

	public void store(Object session, MeasureBean measure)
			throws MSIMonitorException;

	public void storeAll(Object session, List<MeasureBean> measures)
			throws MSIMonitorException;
}
