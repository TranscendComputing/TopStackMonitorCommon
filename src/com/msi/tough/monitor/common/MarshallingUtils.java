/**
 *
 */
package com.msi.tough.monitor.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.cloudwatch.model.AlarmHistoryItem;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.amazonaws.services.cloudwatch.model.MetricAlarm;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.model.ServiceBean;
import com.msi.tough.model.monitor.ServiceHealthEventBean;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.utils.MarshalUtil;

/**
 * Marshal utilities, extends the base implementation in core.
 *
 * @author tdhite
 */
public class MarshallingUtils extends MarshalUtil
{
    /**
     * @param nodeDimensions
     * @param nodeDimension
     * @param dimension
     */
    private static void marshallDimension(XMLNode nodeParent, String nodeName,
        Dimension dimension)
    {
        final XMLNode nodeDimension = new XMLNode(nodeName);
        nodeParent.addNode(nodeDimension);
        MarshallingUtils.marshallString(nodeDimension, MonitorConstants.NODE_NAME,
            dimension.getName());
        MarshallingUtils.marshallString(nodeDimension, MonitorConstants.NODE_VALUE,
            dimension.getValue());
    }

    /**
     * @param nodeParent
     * @param nodeName
     * @param dimensions
     */
    public static void marshallDimensionList(XMLNode nodeParent,
        String nodeName, List<Dimension> dimensions)
    {
        final XMLNode nodeDimensions = new XMLNode(nodeName);
        nodeParent.addNode(nodeDimensions);

        for (Dimension dimension : dimensions)
        {
            MarshallingUtils.marshallDimension(nodeDimensions,
                MonitorConstants.NODE_LISTMEMBER, dimension);
        }
    }

    /**
     * @param nodeParent
     * @param nodeName
     * @param in
     */
    public static void marshallMetric(XMLNode nodeParent, String nodeName,
        Metric metric)
    {
        XMLNode nodeMetric = new XMLNode(nodeName);
        nodeParent.addNode(nodeMetric);

        MarshallingUtils.marshallDimensionList(nodeMetric,
            MonitorConstants.NODE_DIMENSIONS, metric.getDimensions());
        MarshallingUtils.marshallString(nodeMetric, MonitorConstants.NODE_METRICNAME,
            metric.getMetricName());
        MarshallingUtils.marshallString(nodeMetric, MonitorConstants.NODE_NAMESPACE,
            metric.getNamespace());
    }

    /**
     * @param nodeParent
     * @param nodeName
     * @param alarm
     */
    public static void marshallMetricAlarm(XMLNode nodeParent, String nodeName,
        MetricAlarm alarm)
    {
        XMLNode nodeMetricAlarm = new XMLNode(nodeName);
        nodeParent.addNode(nodeMetricAlarm);

        MarshallingUtils.marshallString(nodeMetricAlarm,
                MonitorConstants.NODE_ALARMNAME, alarm.getAlarmName());
        MarshallingUtils.marshallBoolean(nodeMetricAlarm,
            MonitorConstants.NODE_ACTIONSENABLED, alarm.getActionsEnabled());
        MarshallingUtils.marshallStringList(nodeMetricAlarm,
            MonitorConstants.NODE_ALARMACTION, alarm.getAlarmActions(),
            MonitorConstants.NODE_ALARMACTIONS);
        MarshallingUtils.marshallString(nodeMetricAlarm,
            MonitorConstants.NODE_ALARMARN, alarm.getAlarmArn());
        QueryUtil.addNode(nodeMetricAlarm,
        	            MonitorConstants.NODE_ALARMCONFIGURATIONUPDATEDTIMESTAMP,
        	            alarm.getAlarmConfigurationUpdatedTimestamp());
      //  MarshallingUtils.marshallTimestamp(nodeMetricAlarm,
      //      Constants.NODE_ALARMCONFIGURATIONUPDATEDTIMESTAMP,
      //      alarm.getAlarmConfigurationUpdatedTimestamp());
        MarshallingUtils.marshallString(nodeMetricAlarm,
            MonitorConstants.NODE_ALARMDESCRIPTION, alarm.getAlarmDescription());
        MarshallingUtils.marshallString(nodeMetricAlarm,
            MonitorConstants.NODE_COMPARISONOPERATOR, alarm.getComparisonOperator());
        MarshallingUtils.marshallDimensionList(nodeMetricAlarm,
            MonitorConstants.NODE_DIMENSIONS, alarm.getDimensions());
        MarshallingUtils.marshallInteger(nodeMetricAlarm,
            MonitorConstants.NODE_EVALUATIONPERIODS, alarm.getEvaluationPeriods());
        MarshallingUtils.marshallStringList(nodeMetricAlarm,
            MonitorConstants.NODE_INSUFFICIENTDATAACTIONS,
            alarm.getInsufficientDataActions(),
            MonitorConstants.NODE_INSUFFICIENTDATAACTION);
        MarshallingUtils.marshallString(nodeMetricAlarm,
            MonitorConstants.NODE_METRICNAME, alarm.getMetricName());
        MarshallingUtils.marshallString(nodeMetricAlarm,
            MonitorConstants.NODE_NAMESPACE, alarm.getNamespace());
        MarshallingUtils.marshallStringList(nodeMetricAlarm,
            MonitorConstants.NODE_OKACTIONS, alarm.getOKActions(),
            MonitorConstants.NODE_OKACTION);
        MarshallingUtils.marshallInteger(nodeMetricAlarm,
            MonitorConstants.NODE_PERIOD, alarm.getPeriod());
        MarshallingUtils.marshallString(nodeMetricAlarm,
            MonitorConstants.NODE_STATEREASON, alarm.getStateReason());
        MarshallingUtils.marshallString(nodeMetricAlarm,
            MonitorConstants.NODE_STATEREASONDATA, alarm.getStateReasonData());
        QueryUtil.addNode(nodeMetricAlarm,
        	            MonitorConstants.NODE_STATEUPDATEDTIMESTAMP,
        	            alarm.getStateUpdatedTimestamp());
     //   MarshallingUtils.marshallTimestamp(nodeMetricAlarm,
     //       Constants.NODE_STATEUPDATEDTIMESTAMP,
     //       alarm.getStateUpdatedTimestamp());
        MarshallingUtils.marshallString(nodeMetricAlarm,
            MonitorConstants.NODE_STATEVALUE, alarm.getStateValue());
        MarshallingUtils.marshallString(nodeMetricAlarm,
            MonitorConstants.NODE_STATISTIC, alarm.getStatistic());
        MarshallingUtils.marshallDouble(nodeMetricAlarm,
            MonitorConstants.NODE_THRESHOLD, alarm.getThreshold());
        MarshallingUtils.marshallString(nodeMetricAlarm, MonitorConstants.NODE_UNIT,
            alarm.getUnit());
    }

    /**
     * @param nodeParent
     * @param nodeName
     * @param listMetricAlarms
     */
    public static void marshallMetricAlarmList(XMLNode nodeParent,
        String nodeName, List<MetricAlarm> listMetricAlarms)
    {
        XMLNode nodeList = new XMLNode(nodeName);
        nodeParent.addNode(nodeList);

        for (MetricAlarm alarm : listMetricAlarms)
        {
            MarshallingUtils.marshallMetricAlarm(nodeList,
                MonitorConstants.NODE_LISTMEMBER, alarm);
        }
    }

    public static void marshallAlarmHistoryItemList(XMLNode nodeParent,
            String nodeName, List<AlarmHistoryItem> listAlarmHistoryItem)
    {
            XMLNode nodeList = new XMLNode(nodeName);
            nodeParent.addNode(nodeList);

            for (AlarmHistoryItem ahi : listAlarmHistoryItem)
            {
                MarshallingUtils.marshallAlarmHistoryItem(nodeList,
                        MonitorConstants.NODE_LISTMEMBER, ahi);
            }
    }

    public static void marshallAlarmHistoryItem(XMLNode nodeParent,
            String nodeName, AlarmHistoryItem ahi)
    {
        XMLNode nodeAlarmHistoryItem= new XMLNode(nodeName);
        nodeParent.addNode(nodeAlarmHistoryItem);

        MarshallingUtils.marshallString(nodeAlarmHistoryItem,
                MonitorConstants.NODE_ALARMNAME, ahi.getAlarmName());
        MarshallingUtils.marshallString(nodeAlarmHistoryItem,
                MonitorConstants.NODE_ALARMHISTORYITEMDATA, ahi.getHistoryData());
        MarshallingUtils.marshallString(nodeAlarmHistoryItem,
                MonitorConstants.NODE_ALARMHISTORYITEMTYPE, ahi.getHistoryItemType());
        MarshallingUtils.marshallString(nodeAlarmHistoryItem,
                MonitorConstants.NODE_ALARMHISTORYITEMSUMMARY, ahi.getHistorySummary());
        QueryUtil.addNode(nodeAlarmHistoryItem,
	            MonitorConstants.NODE_ALARMHISTORYITEMTIMESTAMP,
	            ahi.getTimestamp());
        ahi.getTimestamp();
    }

    /**
     * @param nodeParent
     * @param nodeName
     * @param in
     */
    public static void marshallMetricList(XMLNode nodeParent, String nodeName,
        List<Metric> listMetrics)
    {
        XMLNode nodeList = new XMLNode(nodeName);
        nodeParent.addNode(nodeList);

        for (Metric metric : listMetrics)
        {
            MarshallingUtils.marshallMetric(nodeList, MonitorConstants.NODE_METRIC,
                metric);
        }
    }

    /** MSI Specific */
    public static void marshallServiceHealthEvent(XMLNode nodeParent, ServiceHealthEventBean event)
    {
    	XMLNode eventNode = new XMLNode(MonitorConstants.NODE_SERVICE_HEALTH_EVENT);
    	nodeParent.addNode(eventNode);

    	ServiceBean sb = event.getService();

    	QueryUtil.addNode(eventNode, MonitorConstants.NODE_SERVICE_HEALTH_EVENT_ID, event.getId() );
    	QueryUtil.addNode(eventNode, MonitorConstants.NODE_SERVICE_HEALTH_SERVICE, sb.getServiceName());
    	QueryUtil.addNode(eventNode, MonitorConstants.NODE_SERVICE_HEALTH_SERVICE_ABBREVIATION, sb.getServiceNameAbbreviation());
    	QueryUtil.addNode(eventNode, MonitorConstants.NODE_SERVICE_HEALTH_EVENT_DESCRIPTION, event.getDescription());
    	QueryUtil.addNode(eventNode, MonitorConstants.NODE_SERVICE_HEALTH_EVENT_STATUS, event.getStatus().toString());
    	QueryUtil.addNode(eventNode, MonitorConstants.NODE_SERVICE_HEALTH_REGION, event.getRegion());
    	QueryUtil.addNode(eventNode, MonitorConstants.NODE_SERVICE_HEALTH_AVAILABILITY_ZONE, event.getAvailablityZone());
    	//marshallTimestamp(eventNode, Constants.NODE_SERVICE_HEALTH_TIMESTAMP, event.getCreatedTime());
    	QueryUtil.addNode(eventNode, MonitorConstants.NODE_SERVICE_HEALTH_TIMESTAMP, event.getCreatedTime()) ;

    }

    public static List<Dimension> unmarshallDimensions(Map<String, String[]> in) {
		List<Dimension> dims = new ArrayList<Dimension>();
		int i = 0;
		while (true) {
			i++;
			final String n[] = in.get("Dimensions.member." + i + ".Name");
			if (n == null) {
				break;
			}
			final String v[] = in.get("Dimensions.member." + i + ".Value");
			Dimension d = new Dimension();
			d.setName(n[0]);
			d.setValue(((v != null) ? v[0] : ""));
			dims.add(d);
		}
		return dims;
	}
}
