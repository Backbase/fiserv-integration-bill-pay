package com.backbase.billpay.fiserv.payees;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayElectronicPayeesPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayElectronicPayeesPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayPayeesPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayPayeesPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.ElectronicPayeeByIdGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.ElectronicPayeeByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.ElectronicPayeeByIdPutResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PayeeByIdGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PayeeByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PayeeByIdPutResponseBody;

/**
 * Interface for payees operations.
 */
public interface PayeesService {

    /**
     * Creates a payee in the billpay system.
     * @param header fiserv header.
     * @param request payee request.
     * @return response.
     */
    BillPayPayeesPostResponseBody postBillPayPayees(Header header, BillPayPayeesPostRequestBody request);
    
    /**
     * Retrieve a payee by ID for the provided subscriber.
     * @param header fiserv header.
     * @param id payee id.
     * @return payee response.
     */
    PayeeByIdGetResponseBody getBillPayPayee(Header header, String id);
    
    /**
     * Updates an existing payee.
     * @param header fiserv header.
     * @param id payee id.
     * @param request payee request.
     * @return response.
     */
    PayeeByIdPutResponseBody putBillPayPayees(Header header, String id, PayeeByIdPutRequestBody request);
    
    /**
     * Deletes a payee by ID for the provided subscriber.
     * @param header fiserv header.
     * @param id payee id.
     * @param cancelPendingPayments if pending payments should be cancelled.
     */
    void deletePayeeById(Header header, String id, Boolean cancelPendingPayments);

    /**
     * Creates an electronic payee in the billpay system.
     * @param header fiserv header.
     * @param request payee request.
     * @return payee response.
     */
    BillPayElectronicPayeesPostResponseBody postBillPayElectronicPayees(
                    Header header, BillPayElectronicPayeesPostRequestBody request);
    
    /**
     * Retrieve an electronic payee by ID for the provided subscriber.
     * @param header fiserv header.
     * @param id payee id.
     * @return payee response.
     */
    ElectronicPayeeByIdGetResponseBody getBillPayElectronicPayee(Header header, String id);
    
    /**
     * Updates an existing electronic payee.
     * @param header fiserv header.
     * @param id payee id.
     * @param request payee request.
     * @return payees response.
     */
    ElectronicPayeeByIdPutResponseBody putBillPayElectronicPayees(Header header, String id,
                    ElectronicPayeeByIdPutRequestBody request);
    
    /**
     * Deletes an electronic payee by ID for the provided subscriber.
     * @param header fiserv header.
     * @param id payee id.
     * @param cancelPendingPayments cancel all pending payments for this payee.
     */
    void deleteElectronicPayeeById(Header header, String id, Boolean cancelPendingPayments);
}