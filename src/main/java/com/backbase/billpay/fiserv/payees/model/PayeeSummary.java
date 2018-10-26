package com.backbase.billpay.fiserv.payees.model;

import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class PayeeSummary {

    @XmlElement(name = "AccountNumber")
    private String accountNumber;

    @XmlElement(name = "Address")
    private USAddress address;

    @XmlElement(name = "AllowsEbillAutoPayOfAmountDue")
    private Boolean allowsEbillAutoPayOfAmountDue;

    @XmlElement(name = "AllowsEbillAutoPayOfBalanceAmount")
    private Boolean allowsEbillAutoPayOfBalanceAmount;

    @XmlElement(name = "AllowsEbillAutoPayOfMinimumAmountDue")
    private Boolean allowsEbillAutoPayOfMinimumAmountDue;

    @XmlElement(name = "CutoffTime")
    private Date cutoffTime;

    @XmlElement(name = "EarliestPaymentDate")
    private BldrDate earliestPaymentDate;

    @XmlElement(name = "EbillActivationStatus")
    private EbillActivationStatusServiceType ebillActivationStatus;

    @XmlElement(name = "EbillAutopayStatus")
    private EbillAutopayStatusType ebillAutopayStatus;

    @XmlElement(name = "IsAddressAvailable")
    private Boolean isAddressAvailable;

    @XmlElement(name = "IsAutopayEnabled")
    private Boolean isAutopayEnabled;

    @XmlElement(name = "IsEbillIntroduction")
    private Boolean isEbillIntroduction;

    @XmlElement(name = "IsEbillLiteActivationCapable")
    private Boolean isEbillLiteActivationCapable;

    @XmlElement(name = "IsEbillTrialPeriod")
    private Boolean isEbillTrialPeriod;

    @XmlElement(name = "IsInternalPayee")
    private Boolean isInternalPayee;

    @XmlElement(name = "IsPaperPaymentsEnabled")
    private Boolean isPaperPaymentsEnabled;

    @XmlElement(name = "IsPayeePhoneNumberModifiable")
    private Boolean isPayeePhoneNumberModifiable;

    @XmlElement(name = "IsRecurringModelEnabled")
    private Boolean isRecurringModelEnabled;

    @XmlElement(name = "IsReminderEnabled")
    private Boolean isReminderEnabled;

    @XmlElement(name = "IsReversible")
    private Boolean isReversible;

    @XmlElement(name = "IsUdap")
    private Boolean isUdap;

    @XmlElement(name = "LeadDays")
    private Integer leadDays;

    @XmlElement(name = "MerchantId")
    private Integer merchantId;

    @XmlElement(name = "MerchantRelationship")
    private Boolean merchantRelationship;

    @XmlElement(name = "Name")
    private String name;

    @XmlElement(name = "NextPaymentDate")
    private BldrDate nextPaymentDate;

    @XmlElement(name = "NickName")
    private String nickName;

    @XmlElement(name = "OverNightAddress")
    private USAddress overNightAddress;

    @XmlElement(name = "PayeeId")
    private Long payeeId;

    @XmlElement(name = "PaymentProcessingDays")
    private PaymentProcessingDays paymentProcessingDays;

    @XmlElement(name = "PaymentServices")
    private List<PaymentServices> paymentServices;

    @XmlElement(name = "PhoneNumber")
    private String phoneNumber;

    public enum EbillActivationStatusServiceType {
        EBILL_NOT_AVAILABLE("EbillNotAvailable"), EBILL_CAPABLE("EbillCapable"), EBILL_PENDING(
                        "EbillPending"), EBILL_ACTIVE("EbillActive");

        private final String name;

        private EbillActivationStatusServiceType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum EbillAutopayStatusType {
        CAPABLE("Capable"), ENABLED("Enabled"), NOT_ELIGIBLE("NotEligible");

        private final String name;

        private EbillAutopayStatusType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum PaymentProcessingDays {
        MONDAY_FRIDAY_EXC_HOLIDAYS("MondayThroughFridayExcludingHolidays"), 
        MONDAY_FRIDAY_INC_HOLIDAYS("MondayThroughFridayIncludingHolidays"), 
        MONDAY_SATURDAY_EXC_HOLIDAYS("MondayThroughSaturdayExcludingHolidays"), 
        MONDAY_SATURDAY_INC_HOLIDAYS("MondayThroughSaturdayIncludingHolidays"), 
        MONDAY_SUNDAY_EXC_HOLIDAYS("MondayThroughSundayExcludingHolidays"), 
        MONDAY_SUNDAY_INC_HOLIDAYS("MondayThroughSundayIncludingHolidays");

        private final String name;

        private PaymentProcessingDays(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

}
