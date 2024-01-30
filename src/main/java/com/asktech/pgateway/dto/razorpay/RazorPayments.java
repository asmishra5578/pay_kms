package com.asktech.pgateway.dto.razorpay;
import lombok.NoArgsConstructor;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class RazorPayments {
    private String count;
    private List<Items> items;
    private String entity;
}
