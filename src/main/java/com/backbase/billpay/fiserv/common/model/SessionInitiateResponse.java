package com.backbase.billpay.fiserv.common.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "SessionInitiateResponse")
public class SessionInitiateResponse extends AbstractResponse {

    public enum SubscriberStatus {
        ACTIVE("Active"), 
        INACTIVE("Inactive"), 
        FROZEN_FRAUD("FrozenFraud"), 
        FROZEN_OTHER("FrozenOther"), 
        CANCELLED_FRAUD("CancelledFraud"), 
        CANCELLED_OTHER("CancelledOther"), 
        VERIFICATION_NEEDED("VerificationNeeded");

        private String name;

        private SubscriberStatus(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    @XmlElement(name = "LDDAcceptanceRequired")
    private boolean lddAcceptanceRequired;

    @XmlElement(name = "SessionCorrelationId")
    private String sessionCorrelationId;

    @XmlElement(name = "SubscriberStatus")
    private SubscriberStatus subscriberStatus;

    @Builder
    public SessionInitiateResponse(ResultType result, String sessionCorrelationId) {
        super(result);
        this.sessionCorrelationId = sessionCorrelationId;
    }
}
