package com.backbase.billpay.fiserv.payments.recurring.model;

import com.backbase.billpay.fiserv.common.model.AbstractResponse;
import com.backbase.billpay.fiserv.common.model.ResultType;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@XmlRootElement(name = "RecurringModelListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class RecurringModelListResponse extends AbstractResponse {

    @XmlElement(name = "RecurringModelList")
    private List<RecurringModel> recurringPayments;

    @Builder
    public RecurringModelListResponse(List<RecurringModel> recurringPayments, ResultType result) {
        super(result);
        this.recurringPayments = recurringPayments;
    }
}