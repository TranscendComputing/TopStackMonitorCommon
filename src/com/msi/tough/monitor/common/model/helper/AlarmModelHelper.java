/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.msi.tough.monitor.common.model.helper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricAlarm;
import com.msi.tough.core.CommaObject;
import com.msi.tough.core.QueryBuilder;
import com.msi.tough.core.StringHelper;
import com.msi.tough.model.monitor.AlarmBean;
import com.msi.tough.model.monitor.AlarmHistoryDetailBean;
import com.msi.tough.model.monitor.DimensionBean;

/**
 * @author jgardner
 *
 */
@Component
public class AlarmModelHelper {

    private static SessionFactory sessionFactory = null;

    @Transactional
    public static List<AlarmBean> describeAlarms(
            final List<String> alarmNames, final String actionPrefix,
            final String alarmNamePrefix, final BigInteger maxRecords,
            final String stateValue, final long acctId) {

        Session session = sessionFactory.getCurrentSession();

        QueryBuilder qb = new QueryBuilder("from AlarmBean");
        qb.equals("userId", acctId);

        if (alarmNames != null && alarmNames.size() > 0) {
            qb.in("alarmName", alarmNames);
        }
        if (!StringHelper.isBlank(alarmNamePrefix)) {
            qb.like("alarmName", alarmNamePrefix + "%");
        }
        if (!StringHelper.isBlank(stateValue)) {
            qb.equals("state", stateValue);
        }

        if (actionPrefix != null && !actionPrefix.isEmpty()) {
            // TODO: implement actionPrefix
            // This may be tricky with a criteria obj. Need to research
            // the best way to do this given a built up criteria obj.
            // possibly make 2 queries? go for list of distinct ids for
            // each action type and add an in criteria for id?
            // session.createQuery("");
        }
        // get our alarm bean objects by querying the database.

        @SuppressWarnings("unchecked")
        List<AlarmBean> alarms = qb.toQuery(session).list();
        if (maxRecords != null && alarms.size() > maxRecords.intValue()) {
            alarms = alarms.subList(0, maxRecords.intValue());
        }
        return alarms;
    }

    public static List<AlarmBean> getAlarms() {
        Session session = sessionFactory.getCurrentSession();
        final List<AlarmBean> alarms = new ArrayList<AlarmBean>();
        @SuppressWarnings("unchecked")
        final List<AlarmBean> a = session.createQuery("from AlarmBean").list();
        if (a != null) {
            alarms.addAll(a);
        }
        return alarms;
    }

    public static AlarmHistoryDetailBean newAction(AlarmBean alarm) {
        Session session = sessionFactory.getCurrentSession();
        final AlarmHistoryDetailBean addAction = new AlarmHistoryDetailBean();
        addAction.setAlarmName(alarm.getAlarmName());
        addAction.setTimestamp(Calendar.getInstance());
        addAction.setType("Action");
        session.save(addAction);
        return addAction;
    }

    public static AlarmHistoryDetailBean newState(AlarmBean alarm,
            String beforeState, String newState) {
        Session session = sessionFactory.getCurrentSession();
        alarm.setState(newState);
        alarm.save(session);
        final AlarmHistoryDetailBean stateUpdate = new AlarmHistoryDetailBean();
        stateUpdate.setAlarmName(alarm.getAlarmName());
        stateUpdate.setTimestamp(Calendar.getInstance());
        stateUpdate.setType("StateUpdate");
        stateUpdate.setSummary("Alarm updated from "+beforeState+" to "+
                newState+".");
        session.save(stateUpdate);
        return stateUpdate;
    }

    public static MetricAlarm toMetricAlarm(final AlarmBean alarm) {
        final MetricAlarm b = new MetricAlarm();
        b.setActionsEnabled(alarm.getEnabled());
        final CommaObject cal = new CommaObject(alarm.getActionNames());
        b.setAlarmActions(cal.toList());
        // b.setAlarmArn(alarmArn);
        b.setAlarmConfigurationUpdatedTimestamp(alarm.getLastUpdate());
        b.setAlarmDescription(alarm.getDescription());
        b.setAlarmName(alarm.getAlarmName());
        b.setComparisonOperator(alarm.getComparator());
        final List<Dimension> dimensions = new ArrayList<Dimension>();
        for (final DimensionBean db : alarm.getDimensions()) {
            final Dimension dim = new Dimension();
            dim.setName(db.getKey());
            dim.setValue(db.getValue());
            dimensions.add(dim);
        }
        b.setDimensions(dimensions);
        b.setEvaluationPeriods(alarm.getEvaluationPeriods().intValue());
        final CommaObject cin = new CommaObject(
                alarm.getInsufficientDataActions());
        b.setInsufficientDataActions(cin.toList());
        b.setMetricName(alarm.getMetricName());
        b.setNamespace(alarm.getNamespace());
        final CommaObject cok = new CommaObject(alarm.getOkActions());
        b.setOKActions(cok.toList());
        b.setPeriod(alarm.getPeriod().intValue());
        b.setStateReason(alarm.getStateReason());
        b.setStateReasonData(alarm.getStateReasonData());
        // b.setStateUpdatedTimestamp(stateUpdatedTimestamp);
        b.setStateValue(alarm.getState());
        b.setStatistic(alarm.getStatistic());
        b.setThreshold(alarm.getThreshold());
        b.setUnit(alarm.getUnit());
        return b;
    }

    @Autowired(required=true)
    public void setSessionFactory(SessionFactory sessionFactory) {
        AlarmModelHelper.sessionFactory = sessionFactory;
    }
}
