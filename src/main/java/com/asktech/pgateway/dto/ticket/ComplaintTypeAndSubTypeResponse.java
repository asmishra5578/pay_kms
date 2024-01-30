package com.asktech.pgateway.dto.ticket;

import java.util.ArrayList;
import java.util.List;

import com.asktech.pgateway.model.TicketComplaintSubType;
import com.asktech.pgateway.model.TicketComplaintType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ComplaintTypeAndSubTypeResponse {

    private String ticketComplaintType;
    private List<String> ticketComplaintSubType = new ArrayList<>();
    
}
