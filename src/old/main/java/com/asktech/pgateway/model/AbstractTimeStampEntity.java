package com.asktech.pgateway.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@MappedSuperclass
public abstract class AbstractTimeStampEntity {

	// @Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created", nullable = false)
	private String created;

	// @Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated", nullable = false)
	private String updated;

	@PrePersist
	protected void onCreate() {
		updated = created = formatToIst(new Date(), "dd MMM yyyy hh:mm:ss a", "IST");
	}

	@PreUpdate
	protected void onUpdate() {
		updated = formatToIst(new Date(), "dd MMM yyyy hh:mm:ss a", "IST");
	}

	public String getCreatedAt() {
		return this.created;
	}
	
	public String getUpdatedAt() {
		return this.updated;
	}

	public static String formatToIst(Date date, String format, String timeZone) {
		// null check
		if (date == null)
			return null;
		// create SimpleDateFormat object with input format
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		// default system timezone if passed null or empty
		if (timeZone == null || "".equalsIgnoreCase(timeZone.trim())) {
			timeZone = Calendar.getInstance().getTimeZone().getID();
		}
		// set timezone to SimpleDateFormat
		sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		// return Date in required format with timezone as String
		return sdf.format(date);
	}

}
