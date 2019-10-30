package com.backbase.billpay.fiserv.autopay.model;

import java.math.BigDecimal;
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
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "AutoPayInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class AutoPayInfo {

    public enum AutoPayDaysBefore {

        ONE("One"),
        TWO("Two"),
        THREE("Three"),
        FOUR("Four"),
        FIVE("Five");

        private String name;

        AutoPayDaysBefore(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum AutoPayAmount {

        AMOUNT_DUE("AmountDue"),
        MINIMUM_AMOUNT_DUE("MinimumAmountDue"),
        ACCOUNT_BALANCE("AccountBalance"),
        FIXED_AMOUNT("FixedAmount");

        private String name;

        AutoPayAmount(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum AutoPayOn {

        DUE_DATE("DueDate"),
        UPON_RECEIPT("UponReceipt"),
        BEFORE_DUE_DATE("BeforeDueDate");

        private String name;

        AutoPayOn(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    @XmlElement(name = "DaysBefore")
    private AutoPayDaysBefore daysBefore;

    @XmlElement(name = "FixedAmount")
    private BigDecimal fixedAmount;

    @XmlElement(name = "MaxAuthorizedAmount")
    private BigDecimal maxAuthorizedAmount;

    @XmlElement(name = "PayAmount")
    private AutoPayAmount autoPayAmount;

    @XmlElement(name = "PayOn")
    private AutoPayOn autoPayOn;

}
