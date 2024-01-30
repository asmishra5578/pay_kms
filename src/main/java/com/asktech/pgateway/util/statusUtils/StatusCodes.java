package com.asktech.pgateway.util.statusUtils;

import com.asktech.pgateway.model.PgErrorCodes;
import com.asktech.pgateway.repository.PgErrorCodeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatusCodes {
    @Autowired
    PgErrorCodeRepository errorCodeRepository;

    public String checkStatus(String pg, String errorCode) {
        PgErrorCodes err = errorCodeRepository.findByPgNameAndPgStatusCode(pg, errorCode);
        if (err != null) {
            return err.getResponseStatus();
        }
        return null;
    }
}
