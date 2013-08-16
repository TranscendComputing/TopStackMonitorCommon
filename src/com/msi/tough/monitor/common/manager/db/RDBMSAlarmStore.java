package com.msi.tough.monitor.common.manager.db;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.cloudwatch.model.AlarmHistoryItem;
import com.amazonaws.services.cloudwatch.model.HistoryItemType;
import com.amazonaws.services.cloudwatch.model.MetricAlarm;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.monitor.AlarmBean;
import com.msi.tough.model.monitor.AlarmHistoryDetailBean;
import com.msi.tough.model.monitor.DimensionBean;
import com.msi.tough.monitor.common.model.helper.AlarmModelHelper;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.CWUtil;
import com.msi.tough.utils.Constants;
import com.mysql.jdbc.StringUtils;

public class RDBMSAlarmStore implements AlarmStoreClient {
	private static final Logger logger = Appctx
			.getLogger(AlarmStoreClient.class.getName());

	@Override
	@Transactional
	public void deleteAlarms(final List<String> alarmNames, final long acctId)
	    throws AlarmNotFoundException {
		logger.info("---------------deleteAlarms-------------------");
		logger.info("\tAlarmNames [" + alarmNames.toArray() + "]");
		final Session session = HibernateUtil.getSession();
		final Criteria crit = session.createCriteria(AlarmBean.class).add(
		        Restrictions.in("alarmName", alarmNames));
		int numDeleted = 0;
		final Iterator<?> delete = crit.list().iterator();
		Set<String> alarmsToDelete = new HashSet<String>(alarmNames);
		while (delete.hasNext()) {
		    AlarmBean alarm = (AlarmBean) delete.next();
		    alarmsToDelete.remove(alarm.getAlarmName());
		    alarm.setDimensions(null);
		    alarm.save(session);
		    session.delete(alarm);
		    numDeleted++;
		}
		if (alarmsToDelete.size() > 0) {
		    throw new AlarmNotFoundException("Alarm not found:" +
		            alarmsToDelete.iterator().next());
		}
		final Criteria crit2 = session.createCriteria(
		        AlarmHistoryDetailBean.class).add(
		                Restrictions.in("alarmName", alarmNames));
		int detailsDeleted = 0;
		final Iterator<?> delete2 = crit2.list().iterator();
		while (delete2.hasNext()) {
		    session.delete(delete2.next());
		    detailsDeleted++;
		}
		logger.info("deleted [" + numDeleted + "] alarms, [" +
		        detailsDeleted + "] details.");

		logger.info("----------------------------------------------");
	}

	@Override
	public AlarmHistoryItem[] describeAlarmHistory(final String alarmName,
			final Calendar startDate, final Calendar endDate,
			final String histItemType, final BigInteger maxRecords,
			final long acctId) {
		logger.info("---------------describeAlarmHistory-------------------");
		List<AlarmHistoryItem> histItems = new ArrayList<AlarmHistoryItem>();

		final Session session = HibernateUtil.getSession();
		session.beginTransaction();
		try {
			final Criteria crit = session
					.createCriteria(AlarmHistoryDetailBean.class);

			if (!StringUtils.isNullOrEmpty(alarmName)) {
			    crit.add(Restrictions.eq("alarmName", alarmName));
			}
			if (startDate != null) {
				crit.add(Restrictions.gt("timestamp", startDate));
			}
			if (endDate != null) {
				crit.add(Restrictions.lt("timestamp", endDate));
			}
			if (!StringUtils.isNullOrEmpty(histItemType)) {
				crit.add(Restrictions.eq("type", histItemType));
			}
			@SuppressWarnings("unchecked")
			List<AlarmHistoryDetailBean> histBeans = crit.list();
			for (final AlarmHistoryDetailBean detail : histBeans) {
				final AlarmHistoryItem item = new AlarmHistoryItem();
				item.setAlarmName(detail.getAlarmName());
				item.setHistoryData(detail.getData());
				item.setHistoryItemType(HistoryItemType.fromValue(
						detail.getType()).toString());
				item.setHistorySummary(detail.getSummary());
				item.setTimestamp(detail.getTimestamp().getTime());
				histItems.add(item);
			}
		} finally {
			if (session.isOpen()) {
				// session.close();
			}
		}
		logger.info("------------------------------------------------------");
		if (maxRecords != null && histItems.size() > maxRecords.intValue()) {
		    histItems = histItems.subList(0, maxRecords.intValue());
		}
		return histItems.toArray(new AlarmHistoryItem[histItems.size()]);
	}

	/**
	 * TODO: Not used; migrate to this from helper, or eliminate.
	 */
	@Override
	public MetricAlarm[] describeAlarms(final List<String> alarmNames,
			final String actionPrefix, final String alarmNamePrefix,
			final BigInteger maxRecords, final String stateValue,
			final long acctId) {
		logger.info("---------------describeAlarms-------------------");

		final Session session = HibernateUtil.newSession();
		session.beginTransaction();
		List<MetricAlarm> alarms = new ArrayList<MetricAlarm>();
		try {
			logger.info("describeAlarms called");
			final Criteria criteria = session.createCriteria(AlarmBean.class);

			// view into the users alarms only
			// TODO Put back in place after IAM
			// criteria.add(Restrictions.eq("ownerId.id", acctId));
			logger.info("ownerId issues?");

			if (alarmNames == null || alarmNames.size() == 0) {
				// if the alarmnames aren't set then check for an
				// alarmnameprefix.
				if (alarmNamePrefix != null) {
					criteria.add(Restrictions.like("alarmName", alarmNamePrefix
							+ "%"));
				}
			} else {
				criteria.add(Restrictions.in("alarmName", alarmNames));
			}
			// statevalue lookup.
			if (stateValue != null && !stateValue.isEmpty()) {
				criteria.add(Restrictions.eq("state", stateValue));
			}

			if (actionPrefix != null && !actionPrefix.isEmpty()) {
				// TODO: implement actionPrefix
				// This may be tricky with a criteria obj. Need to research
				// the best way to do this given a built up criteria obj.
				// possibly make 2 queries? go for list of distinct ids for
				// each action type and add an in criteria for id?
				// session.createQuery("");
			}
			logger.info("setup criteria");
			// get our alarmbean objects by querying the database.
			@SuppressWarnings("unchecked")
			List<AlarmBean> resAlarms = criteria.list();
			logger.info("listed criteria");
			if (resAlarms == null) {
				resAlarms = new ArrayList<AlarmBean>();
			}

			// assemble the MetricAlarm list from the alarmbeans
			//for (final AlarmBean a : resAlarms) {
				// alarms.add(getMetricAlarmFromAlarmBean(a));
			//}
			logger.info("closing session");
			// session.close();
		} finally {
			if (session.isOpen()) {
				// session.close();
			}
		}
		logger.info("-----------------------------------------------");
		if (maxRecords != null && alarms.size() > maxRecords.intValue()) {
		    alarms = alarms.subList(0, maxRecords.intValue());
		}
		return alarms.toArray(new MetricAlarm[alarms.size()]);
	}

	/**
	 *
	 */
	@Override
	public MetricAlarm[] describeAlarmsForMetric(final String metricName,
			final String namespace, final Map<String, String> dimensions,
			final BigInteger period, final String statistic, final String unit,
			final long acctId) {

		logger.info("---------------describeAlarmsForMetric-------------------");
		final Session session = HibernateUtil.getSession();
		session.beginTransaction();
		final List<MetricAlarm> alarms = new ArrayList<MetricAlarm>();
		try {
			final Criteria criteria = session.createCriteria(AlarmBean.class);
			// criteria.add(Restrictions.eq("ownerId", acctId));
			criteria.add(Restrictions.eq("metricName", metricName));
			if (namespace != null) {
			    criteria.add(Restrictions.eq("namespace", namespace));
			}
			if (period != null) {
				criteria.add(Restrictions.eq("period", period));
			}
			if (statistic != null) {
				criteria.add(Restrictions.eq("statistic", statistic));
			}
			if (unit != null) {
				criteria.add(Restrictions.eq("unit", unit));
			}
			// List<String> dimensionValues = new ArrayList<String>();
			// if (dimensions != null && dimensions.size() > 0) {
			// dimensions.values();
			// }
			// criteria.add(Restrictions.in("dimensions", dimensions))
			if (dimensions != null && dimensions.size() > 0) {
				// System.out.println("1. SIZE: "+ criteria.list().size());
				criteria.createCriteria("dimensions").add(
						Restrictions.in("value", dimensions.values()));
				// System.out.println("2. SIZE: "+criteria.list().size());
			}
			@SuppressWarnings("unchecked")
			final List<AlarmBean> resAlarms = criteria.list();

			for (final AlarmBean alarm : resAlarms) {
			    MetricAlarm ma = AlarmModelHelper.toMetricAlarm(alarm);
			    alarms.add(ma);
			}
		} finally {
			if (session.isOpen()) {
				// session.close();
			}
		}

		logger.info("----------------------------------------------------------");
		return alarms.toArray(new MetricAlarm[alarms.size()]);
	}

	@Override
	public void disableAlarmActions(final List<String> alarmNames,
			final long acctId) {
		logger.info("---------------disableAlarmActions-------------------");
		logger.info("\tAlarmNames [" + alarmNames.toArray() + "]");
		final Session session = HibernateUtil.getSession();
		session.beginTransaction();
		try {
			final int res = session
					.createQuery(
							"update AlarmBean set enabled=false where alarmName in (:alarmNames)")
					.setParameterList("alarmNames", alarmNames).executeUpdate();
			logger.info("Disabled [" + res + "] alarms.");
			session.getTransaction().commit();
		} finally {
			if (session.isOpen()) {
				session.close();
			}
		}
		logger.info("-----------------------------------------------------");
	}

	@Override
	public void enableAlarmActions(final List<String> alarmNames,
			final long acctId) {
		if (alarmNames == null) {
			return;
		}
		final Session session = HibernateUtil.getSession();
		session.beginTransaction();
		logger.info("---------------enableAlarmActions-------------------");
		final StringBuilder sb = new StringBuilder();
		for (final String a : alarmNames) {
			sb.append(a);
		}
		logger.info("\talarmNames : [" + sb.toString() + "]");
		try {
			final int res = session
					.createQuery(
							"update AlarmBean set enabled=true where alarmName in (:alarmNames)")
					.setParameterList("alarmNames", alarmNames).executeUpdate();
			logger.info("Enabled [" + res + "] alarms.");
			session.getTransaction().commit();
		} finally {
			if (session.isOpen()) {
				session.close();
			}
		}
		logger.info("-----------------------------------------------------");
	}

	@Override
	public void putMetricAlarm(final Object s, final boolean actionsEnabled,
			final List<String> actionNames, final String alarmDescription,
			final String alarmName, final String compareOperator,
			final Map<String, String> dimensions,
			final BigInteger evaluationPeriods,
			final List<String> insuffDataActions, final String metricName,
			final String namespace, final List<String> okActs,
			final BigInteger period, final String statistic,
			final double threshold, final String unit, final long acctId)
			throws Exception {
		final Session session = (Session) s;
		logger.info("---------------putMetricAlarm-------------------");
		final AccountBean acct = AccountUtil.readAccount(session, acctId);
		final AlarmBean alarm = new AlarmBean();
		final CommaObject cname = new CommaObject(actionNames);
		alarm.setActionNames(cname.toString());
		final CommaObject cin = new CommaObject(insuffDataActions);
		alarm.setInsufficientDataActions(cin.toString());
		final CommaObject cok = new CommaObject(okActs);
		alarm.setOkActions(cok.toString());

		alarm.setAlarmName(alarmName);
		alarm.setComparator(compareOperator);
		alarm.setDescription(alarmDescription);
		alarm.setEnabled(actionsEnabled);
		alarm.setMetricName(metricName);
		alarm.setNamespace(namespace);
		alarm.setUserId(acctId);
		alarm.setPeriod(period);
		alarm.setEvaluationPeriods(evaluationPeriods);
		alarm.setState(Constants.STATE_INSUFFICIENT_DATA);
		alarm.setStateReason("Initial Creation.");
		alarm.setStateReasonData("{}");
		alarm.setStatistic(statistic);
		alarm.setThreshold(threshold);
		alarm.setUnit(unit);

		final Set<DimensionBean> dims = new HashSet<DimensionBean>();
		for (final Entry<String, String> en : dimensions.entrySet()) {
			final DimensionBean dim = CWUtil.getDimensionBean(session,
					acct.getId(), en.getKey(), en.getValue(), true);
			dims.add(dim);
		}
		alarm.setDimensions(dims);
		session.save(alarm);
		logger.info("-------------------------------------------------");
	}

	// TODO
	@Override
	public void setAlarmState(final String alarmName, final String stateValue,
			final String stateReason, final String stateReasonData,
			final long acctId) {
		logger.info("---------------setAlarmState-------------------");
		logger.info("setting state for alarmName [" + alarmName + "]");
		logger.info("\tState Value [" + stateValue + "]");
		logger.info("\tState Reason [" + stateReason + "]");
		logger.info("\tAcctId is [" + acctId + "]");
		final Session session = HibernateUtil.getSession();
		session.beginTransaction();
		try {
			final int updated = session
					.createQuery(
							"update AlarmBean set state=:state, stateReason=:sReason, stateReasonData=:sReasonData WHERE alarmName=:aName")
					// "update AlarmBean set state=:state, stateReason=:sReason, stateReasonData=:sReasonData WHERE alarmName=:aName AND ownerId_id=:acctId")
					.setParameter("state", stateValue)
					.setParameter("sReason", stateReason)
					.setParameter("sReasonData", stateReasonData)
					.setParameter("aName", alarmName)
					// .setParameter("acctId", acctId)
					.executeUpdate();
			logger.info("set state for  [" + updated + "] alarms");
			if (updated < 1) {
				logger.warn("Resource wasn't found!");
				// throw new MSIMonitorServiceException("ResourceNotFound",
				// "The named alarm does not exist.");
			} else {
				final AlarmHistoryDetailBean addHistory = new AlarmHistoryDetailBean();
				addHistory.setAlarmName(alarmName);
				addHistory.setTimestamp(Calendar.getInstance());
				addHistory.setType("StateUpdate");
				session.save(addHistory);
			}
			session.getTransaction().commit();
		} finally {
			if (session.isOpen()) {
				session.close();
			}
		}
		logger.info("------------------------------------------");
	}
}
