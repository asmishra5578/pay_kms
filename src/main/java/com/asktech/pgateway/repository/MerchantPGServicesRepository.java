package com.asktech.pgateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.pgateway.customInterface.IAllMerchantDetailsReport;
import com.asktech.pgateway.customInterface.IMerchantDetailsReport;
import com.asktech.pgateway.model.MerchantPGServices;

public interface MerchantPGServicesRepository extends JpaRepository<MerchantPGServices, String>{

	MerchantPGServices findByMerchantIDAndService(String merchantId, String service);

	List<MerchantPGServices> findByMerchantID(String merchantId);

	List<MerchantPGServices> findByMerchantIDAndPgID(String merchantId, String pgid);

	MerchantPGServices findByMerchantIDAndPgIDAndService(String merchantId, String valueOf, String service);
	
	List<MerchantPGServices> findAllByMerchantIDAndPgIDAndService(String merchantId, String valueOf, String service);

	List<MerchantPGServices> findAllByPgIDAndService(String pgid, String service);

	MerchantPGServices findByMerchantIDAndPgIDAndServiceAndStatus(String merchantID, String pgID, String service,
			String status);
	
	@Query(value = "select a.merchantid merchantId ,a.merchantemail merchantEMail, a.merchant_name merchantName,a.kyc_status kycStatus,a.phone_number phoneNumber, "
			+ "c.service , c.status   "
			+ "from merchant_details a, merchantpgdetails b, merchantpgservices c "
			+ "where a.merchantid =b.merchantid "
			+ "and b.merchantid = c.merchantid "
			+ "and c.merchantid = :merchant_id "
			+ "and c.status = :status "
			+ "group by a.merchantid ,a.merchantemail , a.merchant_name ,a.kyc_status,a.phone_number, "
			+ "c.service , c.status  ",
			nativeQuery = true)
	public List<IMerchantDetailsReport> getMerchantDetailsReport(@Param("merchant_id") String merchant_id , @Param("status") String status) ;

	
	@Query(value = "SELECT e.merchantid, e.merchantemail, e.merchant_name merchantName,e.kyc_status kycStatus,e.phone_number phoneNumber,"
			+ "r.merchantpgname pGName, r.status pGStatus, d.service serviceType , d.status serviceStatus "
			+ "FROM merchant_details e JOIN merchantpgdetails r ON e.merchantid=r.merchantid "
			+ "left OUTER JOIN merchantpgservices d ON d.merchantid=r.merchantid and d.pgid = r.id order by  e.merchantid ,r.merchantpgname",
			nativeQuery = true)
	public List<IAllMerchantDetailsReport> getAllMerchantDetailsReport() ;

	MerchantPGServices findByMerchantIDAndServiceAndStatus(String merchantId, String service, String status);

	MerchantPGServices findByMerchantIDAndStatusAndService(String merchantId, String statusUpdate, String service);

	List<MerchantPGServices> findAllByPgID(String valueOf);

	List<MerchantPGServices> findAllByPgIDAndServiceAndStatus(String pgId, String pgServices, String status);

	List<MerchantPGServices> findAllByUpdatePgIdAndService(String pgId, String pgServices);
}
