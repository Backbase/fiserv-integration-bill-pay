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
    private static final Error INVALID_REQUEST = 
                    new Error().withKey("billpay.api.invalidRequest")
                               .withMessage("The request is invalid");
    private static final Error PAYMENT_ALREADY_EXISTS = 
                    new Error().withKey("billpay.api.paymentAlreadyExists")
                               .withMessage("Payment already exists");
    private static final Error INVALID_ZIP_CODE = 
                    new Error().withKey("billpay.api.invalidZipCode")
                               .withMessage("Invalid Zip Code");
    private static final Error INVALID_ADDRESS_INFORMATION = 
                    new Error().withKey("billpay.api.invalidAddressInformation")
                               .withMessage("Invalid address information");
    private static final Error INVALID_PAYMENT_DATE = 
                    new Error().withKey("billpay.api.invalidPaymentDate")
                               .withMessage("Invalid payment date");
    private static final Error INVALID_E_BILL_ID = 
                    new Error().withKey("billpay.api.invalidEBillID")
                               .withMessage("Invalid Ebill ID");
    private static final Error INVALID_PAYEE = 
                    new Error().withKey("billpay.api.invalidPayee")
                               .withMessage("The payee is invalid");
    private static final Error INVALID_BANK_ACCOUNT = 
                    new Error().withKey("billpay.api.invalidBankAccount")
                               .withMessage("Invalid bank account");
    private static final Error PAYMENT_AMOUNT_TOO_SMALL = 
                    new Error().withKey("billpay.api.paymentAmountTooSmall")
                               .withMessage("Payment amount is too small");
    private static final Error DAILY_LIMIT_EXCEEDED = 
                    new Error().withKey("billpay.api.dailyLimitExceeded")
                               .withMessage("The daily limit has been exceeded");
    private static final Error RISK_CHECK_FAILED = 
                    new Error().withKey("billpay.api.riskCheckFailed")
                               .withMessage("Payments could not be taken");
    private static final Error INVALID_CARD_REQUEST = 
                    new Error().withKey("billpay.api.invalidCardRequest")
                               .withMessage("The card is invalid");
    private static final Error FRAUD_CHECK_FAILED = 
                    new Error().withKey("billpay.api.fraudCheckFailed")
                               .withMessage("Payments could not be taken");
    private static final Error PAYMENT_TOO_FAR_IN_ADVANCE = 
                    new Error().withKey("billpay.api.paymentTooFarInAdvance")
                               .withMessage("Payment is too far in advance");
    private static final Error ACCOUNT_SCHEMING_FAILED = 
                    new Error().withKey("billpay.api.accountSchemingFailed")
                               .withMessage("Invalid payee account number or Account scheming failed");
    private static final Error PAYEE_ALREADY_EXISTS = 
                    new Error().withKey("billpay.api.payeeAlreadyExists")
                               .withMessage("The payee already exists");
    
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
                return INVALID_REQUEST;
            case "266":
                return INVALID_ZIP_CODE;
            case "1065":
                return ACCOUNT_SCHEMING_FAILED;
            case "1310":
                return PAYMENT_ALREADY_EXISTS;
            case "1055":
            case "1332":
            case "1038":
            case "1044":
            case "1057":
                return INVALID_ADDRESS_INFORMATION;
            case "1053":
                return PAYEE_ALREADY_EXISTS;
            case "1301": 
            case "1314":
                return PAYMENT_TOO_FAR_IN_ADVANCE;
            case "1318":
                return INVALID_PAYMENT_DATE;
            case "1306":
                return INVALID_E_BILL_ID;
            case "1308":
                return INVALID_PAYEE;
            case "1312":
                return INVALID_BANK_ACCOUNT;
            case "1313":
                return PAYMENT_AMOUNT_TOO_SMALL;
            case "1315":
                return DAILY_LIMIT_EXCEEDED;
            case "1320":
                return RISK_CHECK_FAILED;
            case "1343":
                return INVALID_CARD_REQUEST;
            case "1900":
                return FRAUD_CHECK_FAILED;
            case "2017":
            case "2018":
            case "2019":
                return FiservClient.PROVIDER_UNAVAILABLE_ERROR;
            default:
                return ResultType.GENERAL_ERROR;
        }
    }
}