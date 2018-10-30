package com.backbase.billpay.fiserv.payments.recurring.model;

import com.backbase.billpay.fiserv.payees.model.BldrDate;
import com.backbase.billpay.fiserv.payments.recurring.model.RecurringModelAddInfo.ModelFrequency;
import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class ModelInfo {

    private ModelFrequency frequency;
    private Boolean isIndefinite;
    private String memo;
    private BldrDate nextPaymentDate;
    private Integer numberOfPayments;
    private BigDecimal recurringPaymentAmount;
    
}
