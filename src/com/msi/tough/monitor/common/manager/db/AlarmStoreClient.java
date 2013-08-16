package com.msi.tough.monitor.common.manager.db;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.cloudwatch.model.AlarmHistoryItem;
import com.amazonaws.services.cloudwatch.model.MetricAlarm;

public interface AlarmStoreClient {

	void deleteAlarms(List<String> alarmNames, long acctId)
	        throws AlarmNotFoundException;

	AlarmHistoryItem[] describeAlarmHistory(String alarmName,
			Calendar startDate, Calendar endDate, String histItemType,
			BigInteger maxRecords, long acctId);

	MetricAlarm[] describeAlarms(List<String> alarmNames, String actionPrefix,
			String alarmNamePrefix, BigInteger maxRecords, String stateValue,
			long acctId);

	MetricAlarm[] describeAlarmsForMetric(String metricName, String namespace,
			Map<String, String> dimensions, BigInteger period,
			String statistic, String unit, long acctId);

	void disableAlarmActions(List<String> alarmNames, long acctId);

	void enableAlarmActions(List<String> alarmNames, long acctId);

	void putMetricAlarm(Object session, boolean actionsEnabled,
			List<String> actionNames, String alarmDescription,
			String alarmName, String compareOperator,
			Map<String, String> dimensions, BigInteger evaluationPeriods,
			List<String> insuffDataActions, String metricName,
			String namespace, List<String> okActs, BigInteger period,
			String statistic, double threshold, String unit, long acctId)
			throws Exception;

	void setAlarmState(String alarmName, String alarmValue, String stateReason,
			String stateReasonData, long acctId);

	public static class AlarmNotFoundException extends Exception {
	    /**
         *
         */
        public AlarmNotFoundException() {
            super();
        }

        /**
         * @param message
         * @param cause
         */
        public AlarmNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * @param message
         */
        public AlarmNotFoundException(String message) {
            super(message);
        }

        /**
         * @param cause
         */
        public AlarmNotFoundException(Throwable cause) {
            super(cause);
        }

        /**
	     *
	     */
	    private static final long serialVersionUID = -1166037276104299565L;
	}
}
