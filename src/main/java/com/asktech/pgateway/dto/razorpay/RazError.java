package com.asktech.pgateway.dto.razorpay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RazError {

	public String code;
    public String description;
    public String source;
    public String step;
    public String reason;   
    public String field;
}
