package com.msi.tough.monitor.common.manager.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.DateHelper;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.monitor.common.MonitorConstants;
import com.msi.tough.monitor.common.model.exception.MSIMonitorException;

public class RDBMSDataStore implements DataStoreClient {
	private static final Logger logger = Appctx.getLogger(RDBMSDataStore.class
			.getName());

	/**
	 * Reduce the Dimensions down to mapping instanceIds AutoScalingGroupName is
	 * mapped to their matching instances within the as_group table (join table
	 * as_inst) ImageId is currently not mapped, but should be similarly looked
	 * up.
	 *
	 * @param dimensions
	 * @return
	 */
	private List<String> convertToInstanceIds(
			final Map<String, String> dimensions) {
		final List<String> instances = new ArrayList<String>();
		for (final Map.Entry<String, String> dim : dimensions.entrySet()) {
			if ("InstanceId".equalsIgnoreCase(dim.getKey())) {
				instances.add(dim.getValue());
			} else if ("AutoScalingGroupName".equalsIgnoreCase(dim.getKey())) {
				final Session session = HibernateUtil.getSession();
				session.beginTransaction();
				try {
				    // Commented out the query, since results weren't being used. -JHG
					//final Criteria crit = session.createCriteria(
					//		ASGroupBean.class).add(
					//		Restrictions.eq("name", dim.getValue()));
					//final List<ASGroupBean> asgroups = crit.list();
					// TODO
					/*
					 * for (ASGroupBean group : asgroups) { for (InstanceBean
					 * inst : group.getInstances()) {
					 * instances.add(inst.getInstanceId()); } }
					 */
				} finally {
					if (session.isOpen()) {
						session.close();
					}
				}
			} else if ("ImageId".equalsIgnoreCase(dim.getKey())) {
				// TODO: Lookup by ImageId first to resolve the instanceIds who
				// are running them.
			}
		}
		return instances;
	}

	@Override
	public Collection<Datapoint> getDatapoints(final String user,
			final List<String> statistics, final int period,
			final String measureName, final Map<String, String> dimensions,
			final Calendar startTime, final Calendar endTime,
			final String unit, final String customUnit, final String namespace)
			throws MSIMonitorException {
		startTime.setTimeZone(TimeZone.getTimeZone("GMT"));
		endTime.setTimeZone(TimeZone.getTimeZone("GMT"));
		logger.info("Calling getDatapoints('" + user + "', '"
				+ statistics.toString() + "', " + period + ", '" + measureName
				+ "', '" + dimensions.toString() + "', "
				+ startTime.getTimeInMillis() + " ("
				+ DateHelper.getISO8601Date(startTime.getTime()) + "), "
				+ endTime.getTimeInMillis() + " ("
				+ DateHelper.getISO8601Date(endTime.getTime()) + "), " + unit
				+ ", " + customUnit + ", " + namespace);
		final ArrayList<Datapoint> dpts = new ArrayList<Datapoint>();
		final List<String> measureNames = new ArrayList<String>();
		final String[] names = measureName.split(",");
		for (final String name : names) {
			measureNames.add(name);
		}
		final List<String> instances = convertToInstanceIds(dimensions);

		final Session session = HibernateUtil.getSession();
		session.beginTransaction();
		Date startDate = startTime.getTime();
		startTime.add(Calendar.SECOND, period);
		Date endTick = startTime.getTime();

		// Grab all instance statistics for the time interval in period
		// increments.
		try {
			// while (tick < endTime) {
			// Select sum(value), max(value), min(value), avg(value), count(*)
			// from measures WHERE timestmp > startTime AND timestmp < endTick
			// AND name=measureName GROUP BY instance_id;
			// }
			while (endTick.before(endTime.getTime())) {

				final String hql = "SELECT sum(value), max(value), min(value), avg(value), count(*) from MeasureBean where timestmp > :date1 AND timestamp < :date2 AND instance_id in (:instanceList) GROUP BY name";
				logger.info("Actual SQL executed to gather measures : "
						+ session.createQuery(hql).setDate("date1", startDate)
								.setDate("date2", endTick)
								.setParameterList("instanceList", instances)
								.getQueryString());
				final Iterator<?> rows = session.createQuery(hql)
						.setDate("date1", startDate).setDate("date2", endTick)
						.setParameterList("instanceList", instances).list()
						.iterator();

				while (rows.hasNext()) {
					final Datapoint dp = new Datapoint();
					final Object[] row = (Object[]) rows.next();
					dp.setSum(row[0] != null ? Double.valueOf(row[0].toString())
							: 0.0);
					dp.setMaximum(row[1] != null ? Double.valueOf(row[1]
							.toString()) : 0.0);
					dp.setMinimum(row[2] != null ? Double.valueOf(row[2]
							.toString()) : 0.0);
					dp.setAverage(row[3] != null ? Double.valueOf(row[3]
							.toString()) : 0.0);
					dp.setSampleCount(row[4] != null ? new Double(row[4]
							.toString()) : 0.0);
					dp.setUnit(StandardUnit.fromValue(unit).toString());
					final Calendar ts = Calendar.getInstance(TimeZone
							.getTimeZone("GMT"));
					ts.setTime(endTick);
					dp.setTimestamp(ts.getTime());
					dpts.add(dp);
				}

				startTime.add(Calendar.SECOND, period);
				startDate = endTick;
				endTick = startTime.getTime();
			}

		} finally {
			if (session.isOpen()) {
				session.close();
			}

		}
		if (dpts.isEmpty()) {
			logger.debug("No data found in call to getDatapoints.");
			final Datapoint dp = new Datapoint();
			dp.setAverage(0.00);
			dp.setMaximum(0.00);
			dp.setMinimum(0.0);
			dp.setSampleCount(0.0);
			dp.setSum(0.00);
			final StandardUnit su = StandardUnit
					.fromValue(MonitorConstants.NONE_UNIT);
			dp.setUnit(su.toString());
			dp.setTimestamp(Calendar.getInstance(TimeZone.getTimeZone("GMT"))
					.getTime());
			dpts.add(dp);
		}

		return dpts;
	}

	@Override
	public Collection<Metric> getUserStatistics(final long userId,
			final String nextToken) throws MSIMonitorException {
		final ArrayList<Metric> metrics = new ArrayList<Metric>();
		//
		// Session session = HibernateUtil.getSession();
		// session.beginTransaction();
		// try
		// {
		// Criteria accCrit =
		// session.createCriteria(AccountBean.class).add(
		// Restrictions.eq("id", userId));
		// List<AccountBean> acclist = accCrit.list();
		// if (acclist == null || acclist.isEmpty())
		// {
		// throw new MSIMonitorException("Unknown account for userId ["
		// + userId + "]");
		// }
		// AccountBean account = acclist.get(0);
		// Criteria crit =
		// session.createCriteria(UserStatisticBean.class);
		// // .add(Restrictions.eq("account", account));
		// List<UserStatisticBean> stats = crit.list();
		// for (UserStatisticBean stat : stats)
		// {
		// Metric m = new Metric();
		// String name = new String();
		// Collection<Dimension> dims = new ArrayList<Dimension>();
		// for (DimensionBean dimension : stat.getDimensions()
		// .getDimensions())
		// {
		// Dimension dim = new Dimension();
		// dim.setName(dimension.getType().toString());
		// dim.setValue(dimension.getValue());
		// dims.add(dim);
		// }
		// m.setMetricName(stat.getMetricName());
		// m.setNamespace(stat.getNamespace());
		// m.setDimensions(dims);
		// metrics.add(m);
		// }
		// }
		// finally
		// {
		// if (session.isOpen())
		// {
		// session.close();
		// }
		// }
		return metrics;
	}

	public Collection<Metric> getUserStatistics(final String metricName,
			final String namespace, final List<String> dimensions,
			final long userId, final String nextToken)
			throws MSIMonitorException {
		final ArrayList<Metric> metrics = new ArrayList<Metric>();
		//
		// final Session session = HibernateUtil.getSession();
		// session.beginTransaction();
		// try {
		// final Criteria accCrit = session.createCriteria(AccountBean.class)
		// .add(Restrictions.eq("id", userId));
		//
		// final List<AccountBean> acclist = accCrit.list();
		// if (acclist == null || acclist.isEmpty()) {
		// throw new MSIMonitorException("Unknown account for userId ["
		// + userId + "]");
		// }
		// final AccountBean account = acclist.get(0);
		// final Criteria crit = session
		// .createCriteria(UserStatisticBean.class);
		// // .add(Restrictions.eq("account", account));
		//
		// if (metricName != null && !metricName.equals(Constants.EMPTYSTRING))
		// {
		// crit.add(Restrictions.eq("metricName", metricName));
		// }
		// if (namespace != null && !namespace.equals(Constants.EMPTYSTRING)) {
		// crit.add(Restrictions.eq("namespace", namespace));
		// }
		// if (dimensions != null && dimensions.size() > 0) {
		// crit.createCriteria("dimensions").createCriteria("dimensions")
		// .add(Restrictions.in("value", dimensions));
		// }
		//
		// final List<UserStatisticBean> stats = crit.list();
		// for (final UserStatisticBean stat : stats) {
		// final Metric m = new Metric();
		// final String name = new String();
		// final Collection<Dimension> dims = new ArrayList<Dimension>();
		// for (final DimensionBean dimension : stat.getDimensions()
		// .getDimensions()) {
		// final Dimension dim = new Dimension();
		// dim.setName(dimension.getType().toString());
		// dim.setValue(dimension.getValue());
		// dims.add(dim);
		// }
		// m.setMetricName(stat.getMetricName());
		// m.setNamespace(stat.getNamespace());
		// m.setDimensions(dims);
		// metrics.add(m);
		// }
		// } finally {
		// if (session.isOpen()) {
		// session.close();
		// }
		// }
		return metrics;
	}

	/**
	 * TODO: Test this function as it's not truely published by Amazon yet...
	 * just showed up in their WSDL, no documentation so what I have here is an
	 * assumption currently.
	 */
	@Override
	public void putMetricData(final String namespace, final MetricDatum[] mDatum)
			throws MSIMonitorException {
		if (mDatum == null) {
			return;
		}
		// final List<MeasureBean> data = new ArrayList<MeasureBean>();
		// for (final MetricDatum md : mDatum) {
		// final MeasureBean d = new MeasureBean();
		// final DimensionGroupBean dims = new DimensionGroupBean();
		// final Collection<Dimension> mDims = md.getDimensions();
		// if (mDims != null) {
		// final Set<DimensionBean> dimBeanSet = new HashSet<DimensionBean>();
		// for (final Dimension mDim : mDims) {
		// final DimensionBean dBean = new DimensionBean();
		// dBean.setType(DimensionType.valueOf(mDim.getName()));
		// dBean.setValue(mDim.getValue());
		// dimBeanSet.add(dBean);
		// }
		// dims.setDimensions(dimBeanSet);
		// }
		// d.setDimensions(dims);
		// // d.setInstanceId();
		// d.setNamespace(namespace);
		// d.setUnit(md.getUnit());
		// d.setValue(String.valueOf(md.getValue()));
		// d.setTimestamp(md.getTimestamp());
		// data.add(d);
		// }
		//
		// final Session session = HibernateUtil.getSession();
		// session.beginTransaction();
		// try {
		// for (final MeasureBean m : data) {
		// session.save(m);
		// }
		//
		// } finally {
		// if (session.isOpen()) {
		// session.close();
		// }
		// }

	}

	@Override
	public void setUserStatistics(final String user, final String requestId,
			final Map<String, String> dimensions, final String metricName,
			final String namespace) throws MSIMonitorException {
		//
		// Session session = HibernateUtil.getSession();
		// session.beginTransaction();
		// AccountBean account;
		// try {
		// final Criteria criteria = session.createCriteria(AccountBean.class)
		// .add(Restrictions.eq("name", user));
		// final List<AccountBean> users = criteria.list();
		// if (users.size() < 1) {
		// throw new MSIMonitorException("Unknown user");
		// }
		// account = users.get(0);
		// } finally {
		// session.close();
		// }
		//
		// session = HibernateUtil.getSession();
		// final org.hibernate.Transaction tx = session.beginTransaction();
		// try {
		// final UserStatisticBean stats = new UserStatisticBean();
		// final DimensionGroupBean dims = new DimensionGroupBean();
		// dims.setName(user + ":" + requestId);
		// session.save(dims);
		// final Set<DimensionBean> dset = new HashSet<DimensionBean>();
		// for (final Map.Entry<String, String> entry : dimensions.entrySet()) {
		// final DimensionBean d = new DimensionBean();
		// d.setType(DimensionBean.DimensionType.valueOf(entry.getKey()));
		// d.setValue(entry.getValue());
		// d.setDimensionId(dims.getId());
		// session.save(d);
		// dset.add(d);
		// }
		// dims.setDimensions(dset);
		// stats.setDimensions(dims);
		// stats.setMetricName(metricName);
		// stats.setNamespace(namespace);
		// stats.setRequestId(requestId);
		// stats.setTimestamp(Calendar
		// .getInstance(TimeZone.getTimeZone("GMT")).getTime());
		// stats.setAccount(account);
		//
		// session.save(stats);
		// tx.commit();
		// } finally {
		// if (session.isOpen()) {
		// session.close();
		// }
		// }
		//
	}

}
