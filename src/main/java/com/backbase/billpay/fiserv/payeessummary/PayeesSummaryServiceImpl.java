package com.backbase.billpay.fiserv.payeessummary;

import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.BillPayPayeesSummaryGetResponseBody;
import org.springframework.stereotype.Service;

@Service
public class PayeesSummaryServiceImpl implements PayeesSummaryService {

    @Override
    public BillPayPayeesSummaryGetResponseBody getBillPayPayeesSummary(String subscriberId) {
        // TODO build summary info from other services
        return new BillPayPayeesSummaryGetResponseBody();
    }
}