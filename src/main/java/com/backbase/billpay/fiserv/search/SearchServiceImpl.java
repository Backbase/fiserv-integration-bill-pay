package com.backbase.billpay.fiserv.search;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.search.model.PayeeSearchRequest;
import com.backbase.billpay.fiserv.search.model.PayeeSearchResponse;
import com.backbase.billpay.fiserv.utils.FiservClient;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessearch.BillPaySearchGetResponseBody;
import com.backbase.buildingblocks.logging.api.Logger;
import com.backbase.buildingblocks.logging.api.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Payees search service, integrates with the Bill Pay provider.
 */
@Service
public class SearchServiceImpl implements SearchService {

    private static final String SEARCH_ACTION = "PayeeSearch";
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceImpl.class);
    private final SearchMapper mapper;
    private final FiservClient client;

    @Autowired
    public SearchServiceImpl(SearchMapper mapper, FiservClient client) {
        this.mapper = mapper;
        this.client = client;
    }

    @Override
    public BillPaySearchGetResponseBody getBillPayPayeesSearch(Header header, String name, String zipCode,
                                                               String accountNumber) {
        PayeeSearchRequest request = PayeeSearchRequest.builder().header(header).name(name).build();
        LOGGER.debug("Created request: {}", request);
        PayeeSearchResponse response = client.call(request, SEARCH_ACTION);
        LOGGER.debug("Received response: {}", response);
        return mapper.toBillPaySearchGetResponseBody(response);
    }
}
