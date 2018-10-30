package com.backbase.billpay.fiserv.payments.recurring.model;

import com.backbase.billpay.fiserv.common.model.AbstractRequest;
import com.backbase.billpay.fiserv.common.model.Header;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "RecurringModelCancelRequest")
public class RecurringModelCancelRequest extends AbstractRequest {
    
    private Boolean cancelPendingPayments;
    private String recurringModelId;
    
    @Builder
    public RecurringModelCancelRequest(String recurringModelId, Boolean cancelPendingPayments, Header header) {
        super(header);
        this.cancelPendingPayments = cancelPendingPayments;
        this.recurringModelId = recurringModelId;
    }
}