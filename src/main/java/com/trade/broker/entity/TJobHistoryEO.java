package com.trade.broker.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.trade.broker.util.DateUtil;
import com.trade.broker.util.TRADEDateUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;


/**
 * The persistent class for the TSPAO_JOB_HISTORY database table.
 * 
 */
@Entity
@Table(name="TRADE_JOB_HISTORY", schema = "TRADE")
public class TJobHistoryEO extends AbstractTradeBaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID",unique = true, nullable = false)
	private int id;
	
	@Temporal(TemporalType.DATE)
	@Column(name="EXECUTION_DATE")
	private Date executionDate;
	
	@Column(name="ITERATION")
	private int iteration;

	@Column(name="STATUS")
	private String status;

	@Column(name="UPD_TIMESTAMP")
	private Timestamp updTimestamp;

	@Column(name="UPD_USR")
	private String updUsr;
	
	@Column(name="CREAT_TIMESTAMP", updatable = false)
	private Timestamp creatTimestamp;

	@Column(name="CREAT_USR")
	private String creatUsr;

	//bi-directional many-to-one association to TspaoJobSchedule
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="JOB_ID", referencedColumnName="ID")
	private TJobScheduleEO spaoJobSchedule;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getExecutionDate() {
		return DateUtil.getDate(executionDate);
	}

	public void setExecutionDate(Date executionDate) {
		this.executionDate = DateUtil.getDate(executionDate);
	}

	public int getIteration() {
		return iteration;
	}

	public void setIteration(int iteration) {
		this.iteration = iteration;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public TJobScheduleEO getSpaoJobSchedule() {
		return null != spaoJobSchedule ? spaoJobSchedule : null;
	}

	public void setSpaoJobSchedule(TJobScheduleEO spaoJobSchedule) {
		this.spaoJobSchedule = null != spaoJobSchedule ? spaoJobSchedule : null;
	}


	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).append(executionDate).append(iteration).append(status).append(spaoJobSchedule.getId())
				.append(creatUsr).append(creatTimestamp).append(updUsr).append(updTimestamp).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TJobHistoryEO) {
			final TJobHistoryEO other = (TJobHistoryEO) obj;
			return new EqualsBuilder().append(id, other.id).append(executionDate, other.executionDate)
					.append(iteration, other.iteration).append(status, other.status)
					.append(spaoJobSchedule.getId(), other.spaoJobSchedule.getId())
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