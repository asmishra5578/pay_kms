package com.asktech.pgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.FileLoading;

public interface FileUploadRepo  extends JpaRepository<FileLoading, String> {

}
