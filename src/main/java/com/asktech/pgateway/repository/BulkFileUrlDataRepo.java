package com.asktech.pgateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.BulkFileUrlData;

public interface BulkFileUrlDataRepo extends JpaRepository<BulkFileUrlData, Long>{

    BulkFileUrlData findByfileName(String fileName);

    List<BulkFileUrlData> findByfileType(String fileType);

    List<BulkFileUrlData> findByFileTypeAndCreatedByUuid(String fileType, String merchantUuid);
    
}
