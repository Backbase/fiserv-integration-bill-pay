package com.backbase.billpay.fiserv.enrolment.model;

import com.backbase.billpay.fiserv.common.model.AbstractRequest;
import com.backbase.billpay.fiserv.common.model.Header;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@XmlRootElement(name = "SubscriberInfoRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriberInfoRequest extends AbstractRequest {

    /**
     * Constructor with header from abstract request.
     * @param header Fiserv header including the subscriber id and ip address
     */
    @Builder
    public SubscriberInfoRequest(Header header) {
        super(header);
    }

}
