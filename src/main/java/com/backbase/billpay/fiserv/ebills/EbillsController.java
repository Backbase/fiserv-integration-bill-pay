package com.backbase.billpay.fiserv.ebills;

import static com.backbase.billpay.fiserv.utils.FiservUtils.fromLocalDate;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.utils.AbstractController;
import com.backbase.billpay.integration.rest.spec.serviceapi.v2.billpay.payees.electronic.id.ebills.BillPayEbillsApi;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.BillPayEbillStatementsGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.BillPayEbillsGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.EbillByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.EbillByIdPutResponseBody;
import java.time.LocalDate;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for Bill Pay eBill functionality.
 */
@RestController
public class EbillsController extends AbstractController implements BillPayEbillsApi {

    private EbillsService ebillsService;

    @Autowired
    public EbillsController(EbillsService ebillsService) {
        this.ebillsService = ebillsService;
    }

    @Override
    public void deleteBillPayEbills(String payeeId, String subscriberId, HttpServletRequest httpServletRequest,
                    HttpServletResponse httpServletResponse) {
        Header header = fiservUtils.createHeader(httpServletRequest, subscriberId);
        ebillsService.disableEbills(header, payeeId);
        logger.debug("Disable eBills for payee with id: {}", payeeId);
    }
    
    @Override
    public BillPayEbillsGetResponseBody getBillPayEbills(String payeeId, String subscriberId, String status, LocalDate startDate,
                    LocalDate endDate, Integer from, Integer size, String orderBy, String direction,
                    HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        logger.debug("Retrieving eBills for payee with id: {}, status: {}, startDate: {}, endDate: {}, from: {}, "
                        + "size: {}, orderBy: {}, direction: {}", payeeId, status, startDate, endDate, from, size,
                        orderBy, direction);
        Header header = fiservUtils.createHeader(httpServletRequest, subscriberId);
        BillPayEbillsGetResponseBody response = ebillsService.getEbills(header, payeeId, status, fromLocalDate(startDate), fromLocalDate(endDate),
                        from, size, orderBy, direction);
        logger.debug("Retrieved eBills for payee with id: {}, status: {}, startDate: {}, endDate: {}, from: {}, "
                        + "size: {}, orderBy: {}, direction: {}, response: {}", payeeId, status, startDate, endDate,
                        from, size, orderBy, direction, response);
        return response;
    }
    
    @Override
    public BillPayEbillStatementsGetResponseBody getBillPayEbillStatements(String payeeId, String ebillId,
                    String subscriberId, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        logger.debug("Unable to retrieve statement for eBill with id: {} for payee: {}, this functionality is not "
                        + "supported by the Bill Pay provider", ebillId, payeeId);
        throw new UnsupportedOperationException(
                        "Retrieval of eBill statements is not supported by the Bill Pay provider");
    }
    
    @Override
    public EbillByIdPutResponseBody putEbillById(@Valid EbillByIdPutRequestBody request, String payeeId,
                    String ebillId, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        logger.debug("Updating eBill with id: {} for payee: {} with request: {}", ebillId, payeeId, request);
        
        if (!request.getPaid()) {
            throw new UnsupportedOperationException("Unfiling an eBill is not supported by the Bill Pay provider");
        }
        
        Header header = fiservUtils.createHeader(httpServletRequest, request.getSubscriberID());
        EbillByIdPutResponseBody response = ebillsService.updateEbillStatus(header, request, ebillId);
        logger.debug("Updated eBill with id: {} for payee: {} with request: {} and response: {}", ebillId, payeeId,
                        request, response);
        return response;
    }

}
