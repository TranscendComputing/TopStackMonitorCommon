package com.msi.tough.monitor.common.manager.db;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.msi.tough.monitor.common.model.exception.MSIMonitorException;

public interface DataStoreClient
{

    public Collection<Datapoint> getDatapoints(String user,
        List<String> statistics, int period, String measureName,
        Map<String, String> dimensions, Calendar startTime, Calendar endTime,
        String unit, String customUnit, String namespace)
        throws MSIMonitorException;

    public Collection<Metric> getUserStatistics(long userId, String nextToken)
        throws MSIMonitorException;

    public void putMetricData(String namespace, MetricDatum[] mDatum)
        throws MSIMonitorException;

    public void setUserStatistics(String user, String requestId,
        Map<String, String> dimensions, String metricName, String namespace)
        throws MSIMonitorException;
}
