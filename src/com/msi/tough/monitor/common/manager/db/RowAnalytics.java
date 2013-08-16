/**
 * 
 */
package com.msi.tough.monitor.common.manager.db;

import java.util.Map;

/**
 * Used to sum, Average, get the Maximum and Minimum statistics for the 
 * series of rows that are processed by it for a given metricName.
 * 
 * @author heathm
 *
 */
public class RowAnalytics {
	private int sampleSize = 0;
	private Double sum = 0.0;
	private Double maximum = 0.0;
	private Double minimum;
	private String metricName;
	
	
	public RowAnalytics(String metricName) {
		this.metricName = metricName;
	}
	

	public void processRow(Map<String, Double> row) {
		if (!row.containsKey(metricName)) return;
		sampleSize++;
		Double value = row.get(metricName);
		computeSum(value);
		computeMinimum(value);
		computeMaximum(value);
	}

	private void computeSum(Double value) {
		sum = sum + value;
	}

	private void computeMinimum(Double value) {
		if (minimum == null || minimum.isNaN()) {
			minimum = value;
		} else {
			if (minimum > value) {
				minimum = value;
			}
		}
	}

	private void computeMaximum(Double value) {
		if (maximum < value)
			maximum = value;
	}
	
	public Double getAverage() {
		return sum / sampleSize;
	}

	public int getSampleSize() {
		return sampleSize;
	}

	public Double getSum() {
		return sum;
	}

	public Double getMaximum() {
		return maximum;
	}

	public Double getMinimum() {
		return minimum;
	}

	public String getMetricName() {
		return metricName;
	}
}
