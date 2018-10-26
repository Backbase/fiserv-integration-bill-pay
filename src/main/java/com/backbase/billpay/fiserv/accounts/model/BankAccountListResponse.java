package com.backbase.billpay.fiserv.accounts.model;

import com.backbase.billpay.fiserv.common.model.AbstractResponse;
import com.backbase.billpay.fiserv.common.model.ResultType;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
@XmlRootElement(name="BankAccountListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class BankAccountListResponse extends AbstractResponse {

    @XmlElement(name = "BankAccounts")
    private List<BankAccountSummary> summaries;

    @Builder
    public BankAccountListResponse(List<BankAccountSummary> summaries, ResultType result) {
        super(result);
        this.summaries = summaries;
    } 
}