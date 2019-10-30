package com.backbase.billpay.fiserv.autopay;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.utils.AbstractController;
import com.backbase.billpay.integration.rest.spec.serviceapi.v2.billpay.payees.electronic.id.autopay.BillPayAutopayApi;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.autopay.BillPayAutopayPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.autopay.BillPayAutopayPutResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for Autopay operations.
 */
@RestController
public class AutopayController extends AbstractController implements BillPayAutopayApi {

    private AutopayService service;

    @Autowired
    public AutopayController(AutopayService service) {
        this.service = service;
    }

    @Override
    public BillPayAutopayPutResponseBody putBillPayAutopay(
                    @Valid BillPayAutopayPutRequestBody request, String payeeId,
                    HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        logger.debug("Enable or modify autopay for payee with id: {}, request: {}", payeeId, request);
        Header header = fiservUtils.createHeader(httpServletRequest, request.getSubscriberID());
        BillPayAutopayPutResponseBody response = service.putAutopayByPayeeId(header, payeeId, request);
        logger.debug("Enabled or modified autopay for payee with id: {}", payeeId);
        return response;
    }

    @Override
    public void deleteBillPayAutopay(String payeeId, String subscriberId, HttpServletRequest httpServletRequest,
                    HttpServletResponse httpServletResponse) {
        Header header = fiservUtils.createHeader(httpServletRequest, subscriberId);
        service.deleteAutopayByPayeeId(header, payeeId);
        logger.debug("Disabled autopay for payee with id: {}", payeeId);
    }
}
