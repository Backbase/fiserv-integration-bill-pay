package com.backbase.billpay.fiserv.payments.recurring.model;

import com.backbase.billpay.fiserv.common.model.AbstractResponse;
import com.backbase.billpay.fiserv.common.model.ResultType;
import com.backbase.billpay.fiserv.payees.model.BldrDate;
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
@XmlRootElement(name = "RecurringModelAddResponse")
public class RecurringModelAddResponse extends AbstractResponse {

    private BldrDate adjustedNextPaymentDate;
    private String recurringModelId;

    @Builder
    public RecurringModelAddResponse(ResultType result, BldrDate adjustedNextPaymentDate, String recurringModelId) {
        super(result);
        this.adjustedNextPaymentDate = adjustedNextPaymentDate;
        this.recurringModelId = recurringModelId;
    } 
}
