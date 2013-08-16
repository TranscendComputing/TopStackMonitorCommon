package com.msi.tough.monitor.common;

import com.msi.tough.engine.aws.Arn;

/**
 * Common constants used among the monitor / monitor server.
 *
 * @author heathm
 */
public interface MonitorConstants extends com.msi.tough.utils.Constants {
	public static final String EMPTYSTRING = "";
	// XML Namespace
	public static final String ATTRIBUTE_XMLNS = "xmlns";
	public static final String NAMESPACE = "http://monitoring.amazonaws.com/doc/2010-08-01/";
	// monitor metrics names
	public final static String CPU_UTILIZATION_COMMAND = "CPUUtilization";
	public final static String NETWORK_IN_COMMAND = "NetworkIn";
	public final static String NETWORK_OUT_COMMAND = "NetworkOut";
	public final static String DISK_WRITE_OPS_COMMAND = "DiskWriteOps";
	public final static String DISK_READ_OPS_COMMAND = "DiskReadOps";
	public final static String DISK_READ_BYTES_COMMAND = "DiskReadBytes";
	public final static String DISK_WRITE_BYTES_COMMAND = "DiskWriteBytes";
	// unit types
	public final static String PERCENT_UNIT = "Percent";
	public final static String SECOND_UNIT = "Seconds";
	public final static String BYTE_UNIT = "Bytes";
	public final static String BIT_UNIT = "Bits";
	public final static String COUNT_UNIT = "Count";
	public final static String BITS_PER_SECOND_UNIT = "Bits/Second";
	public final static String COUNT_PER_SECOND_UNIT = "Count/Second";
	public final static String NONE_UNIT = "None";
	// aggregate statistics
	public final static String AVERAGE = "Average";
	public final static String SUM = "Sum";
	public final static String MINIMUM = "Minimum";
	public final static String MAXIMUM = "Maximum";
	public final static String SAMPLE_COUNT = "SampleCount";
	// Request and Response Node Names
	public final static String NODE_MEMBER = "member";
	public final static String NODE_GETMETRICSTATICSRESULT = "GetMetricStatisticsResult";
	public final static String NODE_LISTMETRICSRESULT = "ListMetricsResult";
	public final static String NODE_METRICS = "Metrics";
	public final static String NODE_DIMENSIONS = "Dimensions";
	public final static String NODE_DIMENSION = "Dimension";
	public final static String NODE_METRIC = "Metric";
	public final static String NODE_METRICNAME = "MetricName";
	public final static String NODE_NAMESPACE = "Namespace";
	public final static String NODE_NAME = "Name";
	public final static String NODE_VALUE = "Value";
	public final static String NODE_NEXTTOKEN = "NextToken";
	public final static String NODE_ACTIONPREFIX = "ActionPrefix";
	public final static String NODE_ALARMNAMEPREFIX = "AlarmNamePrefix";
	public final static String NODE_ALARMNAMES = "AlarmNames";
	public final static String NODE_MAXRECORDS = "MaxRecords";
	public final static String NODE_TIMESTAMP = "Timestamp";
	public static final String DEFAULTMAXRECORDS = "10";
	public final static String NODE_STATEVALUE = "StateValue";
	public final static String NODE_ACTIONSENABLED = "ActionsEnabled";
	public final static String NODE_ALARMACTIONS = "AlarmActions";
	public final static String NODE_ALARMARN = "AlarmArn";
	public final static String NODE_ALARMCONFIGURATIONUPDATEDTIMESTAMP = "AlarmConfigurationUpdatedTimestamp";
	public final static String NODE_ALARMDESCRIPTION = "AlarmDescription";
	public final static String NODE_ALARMNAME = "AlarmName";
	public final static String NODE_COMPARISONOPERATOR = "ComparisonOperator";
	public final static String NODE_EVALUATIONPERIODS = "EvaluationPeriods";
	public final static String NODE_INSUFFICIENTDATAACTIONS = "InsufficientDataActions";
	public static final String NODE_INSUFFICIENTDATAACTION = "InsufficientDataAction";
	public final static String NODE_OKACTIONS = "OKActions";
	public final static String NODE_PERIOD = "Period";
	public final static String NODE_STATEREASON = "StateReason";
	public final static String NODE_STATEREASONDATA = "StateReasonData";
	public final static String NODE_STATEUPDATEDTIMESTAMP = "StateUpdatedTimestamp";
	public final static String NODE_STATISTIC = "Statistic";
	public final static String NODE_THRESHOLD = "Threshold";
	public final static String NODE_UNIT = "Unit";
	public final static String NODE_STARTTIME = "StartTime";
	public final static String NODE_ENDTIME = "EndTime";
	public static final String NODE_METRICALARMS = "MetricAlarms";
	public static final String NODE_METRICALARM = "MetricAlarm";
	public static final String NODE_DESCRIBEALARMS = "DescribeAlarms";
    public static final String NODE_DESCRIBEALARMSRESPONSE = "DescribeAlarmsResponse";
    public static final String NODE_DESCRIBEALARMSRESULT = "DescribeAlarmsResult";
    public static final String NODE_DESCRIBEALARMHISTORYRESPONSE =
            "DescribeAlarmHistoryResponse";
    public static final String NODE_DESCRIBEALARMHISTORYRESULT =
            "DescribeAlarmHistoryResult";
    public static final String NODE_DESCRIBEALARMSFORMETRICRESPONSE =
            "DescribeAlarmsForMetricResponse";
    public static final String NODE_DESCRIBEALARMSFORMETRICRESULT =
            "DescribeAlarmsForMetricResult";
	public static final String NODE_DESCRIBEALARMHISTORY = "DescribeAlarmHistory";
	public static final String NODE_ALARMACTION = "AlarmAction";
	public static final String NODE_OKACTION = "OkAction";
	public static final String NODE_ALARMHISTORYITEMDATA = "HistoryData";
	public static final String NODE_ALARMHISTORYITEMTYPE = "HistoryItemType";
	public static final String NODE_ALARMHISTORYITEMSUMMARY = "HistorySummary";
	public static final String NODE_ALARMHISTORYITEMTIMESTAMP = NODE_TIMESTAMP;
	public static final String NODE_ALARMHISTORYITEMS = "AlarmHistoryItems";
	public static final String NODE_ALARMHISTORYITEM = "AlarmHistoryItem";
	public static final String NODE_DESCRIBEALARMSMETRIC = "DescribeAlarmsForMetric";
    public static final String NODE_LISTMEMBER = "member";

	public static final String AS_TOPIC = "ASTopic";

	// Internal Actions
	public static final String NODE_SERVICE_HEALTH_EVENT = "ServiceHealthEvent";
	public static final String NODE_SERVICE_HEALTH_EVENT_ID = "ServiceHealthEventID";
	public static final String NODE_SERVICE_HEALTH_SERVICE = "ServiceHealthEventService";
	public static final String NODE_SERVICE_HEALTH_SERVICE_ABBREVIATION = "ServiceHealthEventServiceAbbreviation";
	public static final String NODE_SERVICE_HEALTH_EVENT_DESCRIPTION = "ServiceHealthEventDescription";
	public static final String NODE_SERVICE_HEALTH_EVENT_STATUS = "ServiceHealthEventStatus";
	public static final String NODE_SERVICE_HEALTH_REGION = "ServiceHealthEventRegion";
	public static final String NODE_SERVICE_HEALTH_AVAILABILITY_ZONE = "ServiceHealthEventAvailabilityZone";
	public static final String NODE_SERVICE_HEALTH_TIMESTAMP = "ServiceHealthEventTimestamp";

    public static final Boolean NOT_ENABLED = false;

    // ARNS
    public static final Arn ARN_AUTOMATE =
            new Arn("arn:aws:automate::ec2:");
    public static final Arn ARN_STOP =
            new Arn("arn:aws:automate::ec2:stop");
    public static final Arn ARN_TERMINATE =
            new Arn("arn:aws:automate::ec2:terminate");
}
