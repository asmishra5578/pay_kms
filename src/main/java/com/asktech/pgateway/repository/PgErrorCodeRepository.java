package com.asktech.pgateway.repository;

import com.asktech.pgateway.model.PgErrorCodes;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PgErrorCodeRepository extends JpaRepository<PgErrorCodes, String> {
    PgErrorCodes findByPgNameAndPgStatusCode(String pgname, String errorCode);
}
