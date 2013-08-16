/**
 * 
 */
package com.msi.tough.monitor.common.manager;

import java.util.List;
import java.util.logging.Logger;

import org.hibernate.Session;

import com.msi.tough.model.monitor.MeasureBean;
import com.msi.tough.monitor.common.model.exception.MSIMonitorException;

/**
 * @author heathm
 */
public class RDBMSMeasureHandler implements MeasureHandler {

	Logger logger = Logger.getLogger(RDBMSMeasureHandler.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.msi.tough.monitor.manager.MeasureHandler#store(com.msi.tough.monitor
	 * .common.model.Measure)
	 */
	@Override
	public void store(final Object session, final MeasureBean measure)
			throws MSIMonitorException {
		if (measure == null) {
			return;
		}
		// logger.info("storing measure [" + measure.toString() + "]");
		measure.save((Session) session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.msi.tough.monitor.manager.MeasureHandler#storeAll(java.util.List)
	 */
	@Override
	public void storeAll(final Object session, final List<MeasureBean> measures)
			throws MSIMonitorException {
		if (measures == null) {
			return;
		}
		for (final MeasureBean measure : measures) {
			store(session, measure);
		}
	}
}
