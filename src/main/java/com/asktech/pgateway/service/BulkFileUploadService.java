package com.asktech.pgateway.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.asktech.pgateway.constants.BucketNameConstant;
import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.dto.FileResponseDto;
import com.asktech.pgateway.dto.payout.merchant.AccountTransferMerReq;
import com.asktech.pgateway.dto.payout.merchant.AccountTransferUPIMerReq;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.model.BulkFileUrlData;
import com.asktech.pgateway.model.MerchantDetails;
import com.asktech.pgateway.repository.BulkFileUrlDataRepo;
import com.asktech.pgateway.util.Utility;

import kong.unirest.Unirest;

@Service
public class BulkFileUploadService implements BucketNameConstant, ErrorValues {

    @Autowired
    FileUploadManagmentService fileUploadManagmentService;
    @Autowired
    BulkFileUrlDataRepo bulkFileUrlDataRepo;
    @Value("${apiPayoutEndPoint.payoutUrl}")
    String payoutUrl;

    static Logger logger = LoggerFactory.getLogger(BulkFileUploadService.class);

    public FileResponseDto accountandUPITransferUisingBulkFileUpload(MultipartFile file) throws IOException {
        FileResponseDto fileResponseDto = fileUploadManagmentService.accountAndUpiTransferFileUpload(file,
                ACCOUNT_AND_UPI_TSF_BULK_UPLOAD);
        return fileResponseDto;
    }

    public BulkFileUrlData saveFile(FileResponseDto url, MerchantDetails user, String fileType) {
        BulkFileUrlData blkFileUrlData = new BulkFileUrlData();
        blkFileUrlData.setCreatedByUuid(user.getUuid());
        blkFileUrlData.setUrl(url.getFileUrl());
        blkFileUrlData.setFileName(url.getFileName());
        blkFileUrlData.setParsingStatus("false");
        blkFileUrlData.setFileType(fileType.toUpperCase());
        return bulkFileUrlDataRepo.save(blkFileUrlData);
    }

    @SuppressWarnings({ "deprecation", "resource" })
    public List<AccountTransferMerReq> bulkRegistrationParser(File filedata, String fileExtension,
            BulkFileUrlData data) throws ParseException, InvalidFormatException, IOException {
        List<AccountTransferMerReq> rdto = new ArrayList<>();
        if (fileExtension.equalsIgnoreCase(".xlsx")) {
            XSSFWorkbook workbook = new XSSFWorkbook(filedata);
            XSSFSheet worksheet = workbook.getSheetAt(0);
            int i = 1;
            while (i <= worksheet.getLastRowNum()) {
                AccountTransferMerReq dataSource = new AccountTransferMerReq();
                XSSFRow row = worksheet.getRow(i++);
                if (row.getCell(0) == null) {
                    dataSource.setPhonenumber("");
                } else {
                    if (row.getCell(0).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        dataSource.setPhonenumber(NumberToTextConverter.toText(row.getCell(0).getNumericCellValue())
                                .replaceAll("\\s+", ""));
                    } else {
                        dataSource.setPhonenumber(row.getCell(0).getStringCellValue().replaceAll("\\s+", ""));
                    }
                }
                if (row.getCell(1) == null) {
                    dataSource.setAmount("");
                } else {
                    if (row.getCell(1).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        dataSource.setAmount(NumberToTextConverter.toText(row.getCell(1).getNumericCellValue())
                                .replaceAll("\\s+", ""));
                    } else {
                        dataSource.setAmount(row.getCell(1).getStringCellValue().replaceAll("\\s+", ""));
                    }
                }
                if (row.getCell(2) == null) {
                    dataSource.setBankaccount("");
                } else {
                    if (row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        dataSource.setBankaccount(NumberToTextConverter.toText(row.getCell(2).getNumericCellValue())
                                .toLowerCase().replaceAll("\\s+", ""));
                    } else {
                        dataSource.setBankaccount(
                                row.getCell(2).getStringCellValue().toLowerCase().replaceAll("\\s+", ""));
                    }
                }
                if (row.getCell(3) == null) {
                    dataSource.setIfsc("");
                } else {
                    if (row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        dataSource.setIfsc(NumberToTextConverter
                                .toText(row.getCell(3).getNumericCellValue()).replaceAll("\\s+", ""));
                    } else {
                        dataSource.setIfsc(row.getCell(3).getStringCellValue().replaceAll("\\s+", ""));
                    }
                }
                if (row.getCell(4) == null) {
                    dataSource.setBeneficiaryName("");
                } else {
                    if (row.getCell(4).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        dataSource.setBeneficiaryName(NumberToTextConverter
                                .toText(row.getCell(4).getNumericCellValue()).replaceAll("\\s+", ""));
                    } else {
                        dataSource.setBeneficiaryName(row.getCell(4).getStringCellValue().replaceAll("\\s+", ""));
                    }
                }

                if (row.getCell(5) == null) {
                    dataSource.setRequestType("");
                } else {
                    if (row.getCell(5).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        dataSource.setRequestType(NumberToTextConverter.toText(row.getCell(5).getNumericCellValue())
                                .replaceAll("\\s+", ""));
                    } else {
                        dataSource.setRequestType(row.getCell(5).getStringCellValue().replaceAll("\\s+", ""));
                    }
                }

                if (row.getCell(6) == null) {
                    dataSource.setPurpose("");
                } else {
                    if (row.getCell(6).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        dataSource.setPurpose(NumberToTextConverter.toText(row.getCell(6).getNumericCellValue())
                                .replaceAll("\\s+", ""));
                    } else {
                        dataSource.setPurpose(row.getCell(6).getStringCellValue().replaceAll("\\s+", ""));
                    }
                }
                dataSource.setOrderid(Utility.getRandomId());
                rdto.add(dataSource);
            }
        }
        if (fileExtension.equalsIgnoreCase(".csv")) {
            Files.lines(filedata.toPath()).skip(1).forEach(o -> {
                AccountTransferMerReq dataSource = getAccountTramsferDetails(o);
                try {
                    dataSource.setOrderid(Utility.getRandomId());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                rdto.add(dataSource);
            });
        }
        filedata.delete();
        return rdto;
    }

    private static AccountTransferMerReq getAccountTramsferDetails(String line) {
        String[] fields = line.split(",", -1);
        AccountTransferMerReq resD = new AccountTransferMerReq();
        if (fields[0] == null || fields[0].length() == 0) {
            resD.setPhonenumber("");
        } else {
            resD.setPhonenumber(fields[0]);
        }
        if (fields[1] == null || fields[1].length() == 0) {
            resD.setAmount("");
        } else {
            resD.setAmount(fields[1]);
        }
        if (fields[2] == null || fields[2].length() == 0) {
            resD.setBankaccount("");
        } else {
            resD.setBankaccount(fields[2]);
        }
        if (fields[3] == null || fields[3].length() == 0) {
            resD.setIfsc("");
        } else {
            resD.setIfsc(fields[3]);
        }
        if (fields[4] == null || fields[4].length() == 0) {
            resD.setBeneficiaryName("");
        } else {
            resD.setBeneficiaryName(fields[4]);
        }
        if (fields[5] == null || fields[5].length() == 0) {
            resD.setRequestType("");
        } else {
            resD.setRequestType(fields[5]);
        }
        if (fields[6] == null || fields[6].length() == 0) {
            resD.setPurpose("");
        } else {
            resD.setPurpose(fields[6]);
        }
        return resD;
    }

    public void callBulkUploadAccountTransfer(List<AccountTransferMerReq> dto, String merchantid,
            BulkFileUrlData data) {
        Unirest.post(payoutUrl + "bulk/accountTransfer/" + merchantid)
                .header("Content-Type", "application/json").body(dto).asString();
        data.setParsingStatus("true");
        bulkFileUrlDataRepo.save(data);
    }

    @SuppressWarnings({ "deprecation", "resource" })
    public List<AccountTransferUPIMerReq> bulkParserForUPI(File filedata, String fileExtension, BulkFileUrlData data)
            throws IOException, ParseException, InvalidFormatException {
        List<AccountTransferUPIMerReq> rdto = new ArrayList<>();
        if (fileExtension.equalsIgnoreCase(".xlsx")) {
            XSSFWorkbook workbook = new XSSFWorkbook(filedata);
            XSSFSheet worksheet = workbook.getSheetAt(0);
            int i = 1;
            while (i <= worksheet.getLastRowNum()) {
                AccountTransferUPIMerReq dataSource = new AccountTransferUPIMerReq();
                XSSFRow row = worksheet.getRow(i++);
                if (row.getCell(0) == null) {
                    dataSource.setPhonenumber("");
                } else {
                    if (row.getCell(0).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        dataSource.setPhonenumber(NumberToTextConverter.toText(row.getCell(0).getNumericCellValue())
                                .replaceAll("\\s+", ""));
                    } else {
                        dataSource.setPhonenumber(row.getCell(0).getStringCellValue().replaceAll("\\s+", ""));
                    }
                }
                if (row.getCell(1) == null) {
                    dataSource.setAmount("");
                } else {
                    if (row.getCell(1).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        dataSource.setAmount(NumberToTextConverter.toText(row.getCell(1).getNumericCellValue())
                                .replaceAll("\\s+", ""));
                    } else {
                        dataSource.setAmount(row.getCell(1).getStringCellValue().replaceAll("\\s+", ""));
                    }
                }
                if (row.getCell(2) == null) {
                    dataSource.setBeneficiaryVPA("");
                } else {
                    if (row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        dataSource.setBeneficiaryVPA(NumberToTextConverter.toText(row.getCell(2).getNumericCellValue())
                                .toLowerCase().replaceAll("\\s+", ""));
                    } else {
                        dataSource.setBeneficiaryVPA(
                                row.getCell(2).getStringCellValue().toLowerCase().replaceAll("\\s+", ""));
                    }
                }
                if (row.getCell(3) == null) {
                    dataSource.setBeneficiaryName("");
                } else {
                    if (row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        dataSource.setBeneficiaryName(NumberToTextConverter
                                .toText(row.getCell(3).getNumericCellValue()).replaceAll("\\s+", ""));
                    } else {
                        dataSource.setBeneficiaryName(row.getCell(3).getStringCellValue().replaceAll("\\s+", ""));
                    }
                }
                if (row.getCell(4) == null) {
                    dataSource.setRequestType("");
                } else {
                    if (row.getCell(4).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        dataSource.setRequestType(NumberToTextConverter
                                .toText(row.getCell(4).getNumericCellValue()).replaceAll("\\s+", ""));
                    } else {
                        dataSource.setRequestType(row.getCell(4).getStringCellValue().replaceAll("\\s+", ""));
                    }
                }

                if (row.getCell(5) == null) {
                    dataSource.setPurpose("");
                } else {
                    if (row.getCell(5).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        dataSource.setPurpose(NumberToTextConverter.toText(row.getCell(5).getNumericCellValue())
                                .replaceAll("\\s+", ""));
                    } else {
                        dataSource.setPurpose(row.getCell(5).getStringCellValue().replaceAll("\\s+", ""));
                    }
                }
                dataSource.setOrderid(Utility.getRandomId());
                rdto.add(dataSource);
            }
        }
        if (fileExtension.equalsIgnoreCase(".csv")) {
            Files.lines(filedata.toPath()).skip(1).forEach(o -> {
                AccountTransferUPIMerReq dataSource = getUpiTramsferDetails(o);
                try {
                    dataSource.setOrderid(Utility.getRandomId());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                rdto.add(dataSource);
            });
        }
        filedata.delete();
        return rdto;
    }

    private AccountTransferUPIMerReq getUpiTramsferDetails(String line) {
        String[] fields = line.split(",", -1);
        AccountTransferUPIMerReq resD = new AccountTransferUPIMerReq();
        if (fields[0] == null || fields[0].length() == 0) {
            resD.setPhonenumber("");
        } else {
            resD.setPhonenumber(fields[0]);
        }
        if (fields[1] == null || fields[1].length() == 0) {
            resD.setAmount("");
        } else {
            resD.setAmount(fields[1]);
        }
        if (fields[2] == null || fields[2].length() == 0) {
            resD.setBeneficiaryVPA("");
        } else {
            resD.setBeneficiaryVPA(fields[2]);
        }
        if (fields[3] == null || fields[3].length() == 0) {
            resD.setBeneficiaryName("");
        } else {
            resD.setBeneficiaryName(fields[3]);
        }
        if (fields[4] == null || fields[4].length() == 0) {
            resD.setRequestType("");
        } else {
            resD.setRequestType(fields[4]);
        }
        if (fields[5] == null || fields[5].length() == 0) {
            resD.setPurpose("");
        } else {
            resD.setPurpose(fields[5]);
        }
        return resD;
    }

    public void callBulkUploadUPITransfer(List<AccountTransferUPIMerReq> dto, String merchantid, BulkFileUrlData data) {
        Unirest.post(payoutUrl + "bulk/accountTransferUPI/" + merchantid)
                .header("Content-Type", "application/json").body(dto).asString();
        data.setParsingStatus("true");
        bulkFileUrlDataRepo.save(data);
    }

    public boolean checkParsingStatus(String fileName) throws ValidationExceptions {
        BulkFileUrlData data = bulkFileUrlDataRepo.findByfileName(fileName);
        if (data == null) {
            throw new ValidationExceptions(FILE_NAME_NOT_FOUND, FormValidationExceptionEnums.FILE_NAME_NOT_FOUND);
        }
        if (data.getParsingStatus().equals("true"))
            return true;
        return false;
    }

    public List<BulkFileUrlData> getAllUploadedFilesBulkFile(String fileType, String merchantUuid) throws ValidationExceptions {
        if (!(fileType.equals("UPI") || fileType.equals("ACCOUNT"))) {
            throw new ValidationExceptions(FILE_TYPE_ERROR, FormValidationExceptionEnums.FILE_TYPE_ERROR);
        }
        List<BulkFileUrlData> data = bulkFileUrlDataRepo.findByFileTypeAndCreatedByUuid(fileType,merchantUuid);
        return data;
    }
}
