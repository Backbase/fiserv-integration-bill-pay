package com.backbase.billpay.fiserv.search;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.utils.AbstractController;
import com.backbase.billpay.integration.rest.spec.serviceapi.v2.billpay.payeessearch.BillPaySearchApi;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessearch.BillPaySearchGetResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for Bill Pay payees search operations.
 */
@RestController
@ConditionalOnProperty(name = "backbase.communication.inbound", havingValue = "HTTP")
public class SearchController extends AbstractController implements BillPaySearchApi {
    
    @Autowired
    private SearchService searchService;
    
    @Override
    public BillPaySearchGetResponseBody getBillPaySearch(String subscriberId, String name, String zipCode,
                    String accountNumber, HttpServletRequest httpServletRequest,
                    HttpServletResponse httpServletResponse) {
        Header header = fiservUtils.createHeader(httpServletRequest, subscriberId);
        BillPaySearchGetResponseBody response = 
                        searchService.getBillPayPayeesSearch(header, name, zipCode, accountNumber);
        logger.debug("Search payees matching name: {}, zipCode:{}, payeeAccountNumber:{}. Found matching payees: {}", 
                        name, zipCode, accountNumber, response);
        return response;
    }

}
