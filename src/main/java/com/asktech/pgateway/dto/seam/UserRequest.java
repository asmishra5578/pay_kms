package com.asktech.pgateway.dto.seam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
	private String userName;
	private String userPhone;
	private String userEmail;
	private Integer amount;
}
