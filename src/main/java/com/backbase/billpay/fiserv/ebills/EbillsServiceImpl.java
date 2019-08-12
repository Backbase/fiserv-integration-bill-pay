package com.backbase.billpay.fiserv.ebills;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.ebills.model.EbillAccountCancelRequest;
import com.backbase.billpay.fiserv.utils.FiservClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Payee eBills service, integrates with the Bill Pay provider.
 */
@Service
public class EbillsServiceImpl implements EbillsService {
    
    private static final String EBILLS_CANCEL_ACTION = "EbillAccountCancel";
    
    private FiservClient client;
    
    @Autowired
    public EbillsServiceImpl(FiservClient client) {
        this.client = client;
    }
    
    @Override
    public void disableEbills(Header header, String payeeId) {
        EbillAccountCancelRequest cancelRequest = EbillAccountCancelRequest.builder()
                        .header(header)
                        .payeeId(Long.valueOf(payeeId))
                        .build();
        client.call(cancelRequest, EBILLS_CANCEL_ACTION);
    }

}
