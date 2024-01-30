package com.asktech.pgateway.model.DistributionManagement;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DistributorMapping {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
    private String distMerchantid;
    private String retailMerchantid;
}
