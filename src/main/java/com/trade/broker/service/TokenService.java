package com.trade.broker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trade.broker.entity.DBTokenDetail;
import com.trade.broker.repository.TokenRepository;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Transactional
    public DBTokenDetail saveToken(DBTokenDetail newDBTokenDetail) {
        DBTokenDetail result = null;
        DBTokenDetail oldDBTokenDetail = tokenRepository.findByAppNameAndTokenexpired(newDBTokenDetail.getAppName(), "N");
        if (oldDBTokenDetail == null) {
            DBTokenDetail expriredDBTokenDetail = tokenRepository.findByAppNameAndTokenexpired(newDBTokenDetail.getAppName(), "Y");
            if (expriredDBTokenDetail != null) {
                expriredDBTokenDetail.setAccesstoken(newDBTokenDetail.getAccesstoken());
                expriredDBTokenDetail.setRefreshtoken(newDBTokenDetail.getRefreshtoken());
                expriredDBTokenDetail.setTokenexpried(newDBTokenDetail.getTokenexpried());
                if (newDBTokenDetail.getLiveOrBacktest() != null) {
                    expriredDBTokenDetail.setLiveOrBacktest(newDBTokenDetail.getLiveOrBacktest());
                }
                if (newDBTokenDetail.getFeedtoken() != null) {
                    expriredDBTokenDetail.setFeedtoken(newDBTokenDetail.getFeedtoken());
                }
                if (newDBTokenDetail.getClientId() != null) {
                    expriredDBTokenDetail.setClientId(newDBTokenDetail.getClientId());
                }
                result = tokenRepository.save(expriredDBTokenDetail);
            } else {
                newDBTokenDetail.setTokenexpried("N");
                result = tokenRepository.save(newDBTokenDetail);
            }
        } else {
            oldDBTokenDetail.setAccesstoken(newDBTokenDetail.getAccesstoken());
            oldDBTokenDetail.setRefreshtoken(newDBTokenDetail.getRefreshtoken());
            oldDBTokenDetail.setTokenexpried(newDBTokenDetail.getTokenexpried());
            if (newDBTokenDetail.getLiveOrBacktest() != null) {
                oldDBTokenDetail.setLiveOrBacktest(newDBTokenDetail.getLiveOrBacktest());
            }
            if (newDBTokenDetail.getFeedtoken() != null) {
                oldDBTokenDetail.setFeedtoken(newDBTokenDetail.getFeedtoken());
            }
            if (newDBTokenDetail.getClientId() != null) {
                oldDBTokenDetail.setClientId(newDBTokenDetail.getClientId());
            }
            result = tokenRepository.save(oldDBTokenDetail);
        }
        return result;
    }

    @Transactional
    public DBTokenDetail getTokenAppName(String appName, String expried) {
        return tokenRepository.findByAppNameAndTokenexpired(appName, expried);
    }

    @Transactional
    public DBTokenDetail updateTokenEntity(Long id, DBTokenDetail tokenDetails) {
        DBTokenDetail tokenDb = tokenRepository.findById(id).orElseThrow(() -> new RuntimeException("Token not found"));
        tokenDb.setAccesstoken(tokenDetails.getAccesstoken());
        tokenDb.setRefreshtoken(tokenDetails.getRefreshtoken());
        return tokenRepository.save(tokenDb);
    }

    public void deleteToken(Long id) {
        tokenRepository.deleteById(id);
    }
}
