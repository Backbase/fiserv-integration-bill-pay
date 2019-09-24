package com.backbase.billpay.fiserv.payees;

import com.backbase.billpay.fiserv.utils.AbstractController;
import com.backbase.billpay.integration.rest.spec.serviceapi.v2.billpay.payees.BillPayPayeesApi;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for Bill Pay payees operations.
 */
@RestController
@ConditionalOnProperty(name = "backbase.communication.inbound", havingValue = "HTTP")
public class PayeesController extends AbstractController implements BillPayPayeesApi {
    
    @Autowired
    private PayeesService payeesService;
    
    @Override
    public BillPayPayeesPostResponseBody postBillPayPayees(
                    @Valid BillPayPayeesPostRequestBody request,
                    HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        BillPayPayeesPostResponseBody response = payeesService.postBillPayPayees(
                        fiservUtils.createHeader(httpServletRequest, request.getSubscriberID()), request);
        logger.debug("Post payees with request: {}, response: {}", request, response);
        return response;
    }
    
    @Override
    public PayeeByIdGetResponseBody getPayeeById(String id, String subscriberId, HttpServletRequest httpServletRequest,
                    HttpServletResponse httpServletResponse) {
        PayeeByIdGetResponseBody response = 
                        payeesService.getBillPayPayee(fiservUtils.createHeader(httpServletRequest, subscriberId), id);
        logger.debug("Get payee with id: {} for billpay user: {}, response: {}", id, subscriberId, response);
        return response;
    }
    
    @Override
    public PayeeByIdPutResponseBody putPayeeById(@Valid PayeeByIdPutRequestBody request, String id,
                    HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        PayeeByIdPutResponseBody response = payeesService.putBillPayPayees(
                        fiservUtils.createHeader(httpServletRequest, request.getSubscriberID()), id, request);
        logger.debug("Put payees with id: {}, request: {}, response: {}", id, request, response);
        return response;
    }
    
    @Override
    public void deletePayeeById(String id, String subscriberId, Boolean cancelPendingPayments,
                    HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        payeesService.deletePayeeById(fiservUtils.createHeader(httpServletRequest, subscriberId), id, cancelPendingPayments);
        logger.debug("Delete payee with id: {}", id);
    }
    
    @Override
    public BillPayElectronicPayeesPostResponseBody postBillPayElectronicPayees(
                    @Valid BillPayElectronicPayeesPostRequestBody request,
                    HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        BillPayElectronicPayeesPostResponseBody response = payeesService.postBillPayElectronicPayees(
                        fiservUtils.createHeader(httpServletRequest, request.getSubscriberID()), request);
        logger.debug("Post electronic payees with request: {}, response: {}", request, response);
        return response;
    }
    
    @Override
    public ElectronicPayeeByIdGetResponseBody getElectronicPayeeById(String id, String subscriberId,
                    HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        ElectronicPayeeByIdGetResponseBody response = payeesService
                        .getBillPayElectronicPayee(fiservUtils.createHeader(httpServletRequest, subscriberId), id);
        logger.debug("Get electronic payee with id: {} for billpay user: {}, response: {}", 
                     id, subscriberId, response);
        return response;
    }
    
    @Override
    public ElectronicPayeeByIdPutResponseBody putElectronicPayeeById(
                    @Valid ElectronicPayeeByIdPutRequestBody request, String id,
                    HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        ElectronicPayeeByIdPutResponseBody response = payeesService.putBillPayElectronicPayees(
                        fiservUtils.createHeader(httpServletRequest, request.getSubscriberID()), id, request);
        logger.debug("Put electronic payees with id: {}, request: {}, response: {}", id, request, response);
        return response;
    }
    
    @Override
    public void deleteElectronicPayeeById(String id, String subscriberId, Boolean cancelPendingPayments,
                    HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        payeesService.deleteElectronicPayeeById(fiservUtils.createHeader(httpServletRequest, subscriberId), id,
                        cancelPendingPayments);
        logger.debug("Delete electronic payee with id: {}", id);
    }
    
}
