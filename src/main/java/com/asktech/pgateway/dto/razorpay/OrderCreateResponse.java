package  com.asktech.pgateway.dto.razorpay;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderCreateResponse {

	public String id;
    public String entity;
    public int amount;
    public int amount_paid;
    public int amount_due;
    public String currency;
    public String receipt;
    public Object offer_id;
    public String status;
    public int attempts;
    public ArrayList<Object> notes;
    public int created_at;
    @JsonProperty("error")
    public RazError error;
}
