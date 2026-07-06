package com.trade.broker.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.trade.broker.entity.AbstractTradeBaseEntity;
@NoRepositoryBean
public interface ITradeBaseRepository<T extends AbstractTradeBaseEntity,ID extends Serializable> extends JpaRepository<T, ID>{

}
