package com.backbase.billpay.fiserv.payeessummary;

import com.backbase.billpay.fiserv.utils.AbstractController;
import com.backbase.billpay.integration.rest.spec.serviceapi.v2.billpay.payeessummary.BillPayPayeesSummaryApi;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.BillPayPayeesSummaryGetResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for Bill Pay payees summary operations.
 */
@RestController
public class PayeesSummaryController extends AbstractController implements BillPayPayeesSummaryApi {
    
    private PayeesSummaryService payeesSummaryService;

    @Autowired
    public PayeesSummaryController(PayeesSummaryService payeesSummaryService) {
        this.payeesSummaryService = payeesSummaryService;
    }

    @Override
    public BillPayPayeesSummaryGetResponseBody getBillPayPayeesSummary(String subscriberId,
                    HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        BillPayPayeesSummaryGetResponseBody response = payeesSummaryService
                        .getBillPayPayeesSummary(fiservUtils.createHeader(httpServletRequest, subscriberId));
        logger.debug("Get payees summary for subscriberID: {}, found summaries: {}", subscriberId, response); 
        return response;
    }

}
