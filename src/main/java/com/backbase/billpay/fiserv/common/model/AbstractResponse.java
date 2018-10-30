package com.backbase.billpay.fiserv.common.model;

import javax.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractResponse {
    
    @XmlElement(name = "Result", required = true)
    private ResultType result;
}