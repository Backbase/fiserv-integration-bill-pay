package com.backbase.billpay.fiserv.common.model;

import com.backbase.billpay.fiserv.utils.FiservClient;
import com.backbase.buildingblocks.presentation.errors.Error;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Builder
@ToString
public class ResultInfo {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultInfo.class);

    /**
     * Bill Pay API errors to map provider errors from.
     */
    static final String INVALID_REQUEST_MESSAGE = "The request is invalid.";
    static final String INVALID_REQUEST_KEY = "billpay.api.invalidRequest";
    static final String INVALID_CARD_REQUEST_KEY = "billpay.api.invalidCardRequest";
    static final String PAYMENT_QUANTITY_EXCEEDED_KEY = "billpay.api.paymentQuantityExceeded";
    static final String FINAL_DATE_BEFORE_FIRST_DATE_KEY = "billpay.api.finalDateBeforeFirstDate";
    static final String FRAUD_CHECK_FAILED_KEY = "billpay.api.fraudCheckFailed";
    static final String FAILED_TO_CHANGE_PAYMENT_STATE_KEY = "billpay.api.failedToChangePaymentState";
    static final String RISK_CHECK_FAILED_KEY = "billpay.api.riskCheckFailed";
    static final String DAILY_LIMIT_EXCEEDED_KEY = "billpay.api.dailyLimitExceeded";
    static final String PAYMENT_TOO_FAR_IN_ADVANCE_KEY = "billpay.api.paymentTooFarInAdvance";
    static final String PAYMENT_AMOUNT_TOO_SMALL_KEY = "billpay.api.paymentAmountTooSmall";
    static final String INVALID_BANK_ACCOUNT_KEY = "billpay.api.invalidBankAccount";
    static final String INVALID_PAYEE_KEY = "billpay.api.invalidPayee";
    static final String INACTIVE_PAYEE_KEY = "billpay.api.inactivePayee";
    static final String INVALID_E_BILL_ID_KEY = "billpay.api.invalidEBillID";
    static final String INVALID_PAYMENT_DATE_KEY = "billpay.api.invalidPaymentDate";
    static final String PAYMENT_AMOUNT_TOO_LARGE_KEY = "billpay.api.paymentAmountTooLarge";
    static final String INVALID_PAYMENT_AMOUNT_KEY = "billpay.api.invalidPaymentAmount";
    static final String PAYMENT_ALREADY_EXISTS_KEY = "billpay.api.paymentAlreadyExists";
    static final String INVALID_NEXT_PAYMENT_DATE_KEY = "billpay.api.invalidNextPaymentDate";
    static final String MISSING_PAYMENT_INFORMATION_KEY = "billpay.api.missingPaymentInformation";
    static final String TOO_MANY_PAYMENTS_REQUESTED_KEY = "billpay.api.tooManyPaymentsRequested";
    static final String ACCOUNT_SCHEMING_FAILED_KEY = "billpay.api.accountSchemingFailed";
    static final String INVALID_STATUS_KEY = "billpay.api.invalidStatus";
    static final String INVALID_STATE_CODE_KEY = "billpay.api.invalidStateCode";
    static final String INVALID_CITY_NAME_KEY = "billpay.api.invalidCityName";
    static final String INVALID_ADDRESS_INFORMATION_KEY = "billpay.api.invalidAddressInformation";
    static final String INVALID_ACCOUNT_NUMBER_KEY = "billpay.api.invalidAccountNumber";
    static final String INVALID_ZIP_CODE_KEY = "billpay.api.invalidZipCode";
    static final String PAYEE_ALREADY_EXISTS_KEY = "billpay.api.payeeAlreadyExists";

    private String code;
    private String description;
    private String field;
    private String fieldPath;
    private String listItemId;
    private ResultCategory resultCategory;

    public enum ResultCategory {
        ERROR, WARNING, INFO;
    }

    public boolean isError() {
        return ResultCategory.ERROR == resultCategory;
    }

    /**
     * Returns the error or null if not error.
     * @return
     */
    public Error getError() {
        
        if (!isError()) {
            return null;
        }
        
        LOGGER.warn("Error from Fiserv, error {}.", this);
        
        switch (code) {
            // general errors
            case "100":
            case "101":
            case "102":
            case "103":
            case "104":
            case "105":
            case "300":
            case "301":
            case "370":
            case "375":
            case "380":
                return new Error().withKey(INVALID_REQUEST_KEY)
                                  .withMessage(INVALID_REQUEST_MESSAGE);
            case "2017":
            case "2018":
            case "2019":
                return FiservClient.PROVIDER_UNAVAILABLE_ERROR;
                
            default:
                return ResultType.GENERAL_ERROR;
        }
    }
}