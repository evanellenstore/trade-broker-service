package com.trade.broker.entity;
import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.persistence.Embeddable;
import jakarta.persistence.MappedSuperclass;

/**
 * The abstract Class AbstractTradeBaseEntity.
 */
//@Embeddable
@MappedSuperclass
public abstract class AbstractTradeBaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {

		return new ToStringBuilder(this).toString();

	}

	/**
	 * hashCode.
	 *
	 * @return integer
	 */
	public abstract int hashCode();

	/**
	 * Equals.
	 *
	 * @param obj            the Object
	 * @return boolean
	 */
	public abstract boolean equals(Object obj);

}

