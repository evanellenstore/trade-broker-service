package com.trade.broker.entity;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.trade.broker.util.DateUtil;
import com.trade.broker.util.TRADEDateUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;


/**
 * The persistent class for the TSPAO_JOB_SCHEDULES database table.
 * 
 */
@Entity
@Table(name="TRADE_JOB_SCHEDULES", schema = "TRADE")
public class TJobScheduleEO extends AbstractTradeBaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID",unique = true, nullable = false)
	private int id;

	@Column(name="JOB_CODE" , unique = true, nullable = false)
	private String jobCode;

	@Column(name="JOB_NAME")
	private String jobName;
	
	@Temporal(TemporalType.DATE)
	@Column(name="LAST_SUCCESS_RUN")
	private Date lastSuccessRun;

	@Column(name="SCHEDULE_PATTERN")
	private String schedulePattern;
	
	@Column(name="MAX_ITERATION")
	private int maxIteration;
	
	@Column(name="CREAT_TIMESTAMP", updatable = false)
	private Timestamp creatTimestamp;

	@Column(name="CREAT_USR")
	private String creatUsr;

	@Column(name="UPD_TIMESTAMP")
	private Timestamp updTimestamp;

	@Column(name="UPD_USR")
	private String updUsr;

	//bi-directional many-to-one association to TspaoJobHistory
	@OneToMany(mappedBy="spaoJobSchedule")
	private List<TJobHistoryEO> spaoJobHistories;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getJobCode() {
		return jobCode;
	}

	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Date getLastSuccessRun() {
		return DateUtil.getDate(lastSuccessRun);
	}

	public void setLastSuccessRun(Date lastSuccessRun) {
		this.lastSuccessRun = DateUtil.getDate(lastSuccessRun);
	}

	public String getSchedulePattern() {
		return schedulePattern;
	}

	public void setSchedulePattern(String schedulePattern) {
		this.schedulePattern = schedulePattern;
	}

	public int getMaxIteration() {
		return maxIteration;
	}

	public void setMaxIteration(int maxIteration) {
		this.maxIteration = maxIteration;
	}

	public Timestamp getCreatTimestamp() {
		return DateUtil.getTimestamp(creatTimestamp);
	}

	public void setCreatTimestamp(Timestamp creatTimestamp) {
		this.creatTimestamp = DateUtil.getTimestamp(creatTimestamp);
	}

	public String getCreatUsr() {
		return creatUsr;
	}

	public void setCreatUsr(String creatUsr) {
		this.creatUsr = creatUsr;
	}

	public Timestamp getUpdTimestamp() {
		return DateUtil.getTimestamp(updTimestamp);
	}

	public void setUpdTimestamp(Timestamp updTimestamp) {
		this.updTimestamp = DateUtil.getTimestamp(updTimestamp);
	}

	public String getUpdUsr() {
		return updUsr;
	}

	public void setUpdUsr(String updUsr) {
		this.updUsr = updUsr;
	}

	public List<TJobHistoryEO> getSpaoJobHistories() {
		return null != spaoJobHistories ? spaoJobHistories : null;
	}

	public void setSpaoJobHistories(List<TJobHistoryEO> spaoJobHistories) {
		this.spaoJobHistories = null != spaoJobHistories ? spaoJobHistories : null;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).append(jobCode).append(jobName).append(lastSuccessRun).append(schedulePattern)
				.append(maxIteration).append(creatUsr).append(creatTimestamp).append(updUsr).append(updTimestamp).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TJobScheduleEO) {
			final TJobScheduleEO other = (TJobScheduleEO) obj;
			return new EqualsBuilder().append(id, other.id).append(jobCode, other.jobCode)
					.append(jobName, other.jobName).append(lastSuccessRun, other.lastSuccessRun)
					.append(schedulePattern, other.schedulePattern).append(maxIteration, other.maxIteration)
					.append(creatUsr, other.creatUsr).append(creatTimestamp, other.creatTimestamp)
					.append(updUsr, other.updUsr).append(updTimestamp, other.updTimestamp).isEquals();
		} else {
			return false;
		}
	}
	
	@PreUpdate
	public void updateTimeStamps() {
	    this.updTimestamp = TRADEDateUtil.getCurrentJavaSqlTimestamp();	    
	}

	@PrePersist
	public void createTimeStamps() {
	    this.creatTimestamp = TRADEDateUtil.getCurrentJavaSqlTimestamp();
	    this.updTimestamp = TRADEDateUtil.getCurrentJavaSqlTimestamp();
	}

}
