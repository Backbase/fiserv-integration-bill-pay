package com.backbase.billpay.fiserv.autopay;

import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayAddRequest;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayCancelRequest;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayFilter;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayListRequest;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayListResponse;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayModifyRequest;
import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.utils.FiservClient;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.autopay.BillPayAutopayPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.autopay.BillPayAutopayPutResponseBody;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AutopayServiceImpl implements AutopayService {

    public static final String AUTOPAY_ADD_ACTION = "EbillAutoPayAdd";
    public static final String AUTOPAY_MODIFY_ACTION = "EbillAutoPayModify";
    public static final String AUTOPAY_CANCEL_ACTION = "EbillAutoPayCancel";
    public static final String AUTOPAY_LIST_ACTION = "EbillGetAutoPayList";

    private FiservClient client;
    private AutopayMapper mapper;

    @Autowired
    public AutopayServiceImpl(FiservClient client, AutopayMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public BillPayAutopayPutResponseBody putAutopayByPayeeId(Header header, String payeeId,
                    BillPayAutopayPutRequestBody request) {
        EbillAutoPayFilter filter = new EbillAutoPayFilter();
        filter.setPayeeId(Long.valueOf(payeeId));
        EbillAutoPayListRequest listRequest = EbillAutoPayListRequest.builder()
                        .header(header)
                        .filter(filter)
                        .build();
        EbillAutoPayListResponse response = client.call(listRequest, AUTOPAY_LIST_ACTION);

        if (CollectionUtils.isEmpty(response.getEbillAutoPayList())) {
            EbillAutoPayAddRequest addRequest = mapper.toEbillAutoPayAddRequest(request.getAutopay());
            addRequest.getAutopayInfo().setPayeeId(Long.valueOf(payeeId));
            addRequest.setHeader(header);
            client.call(addRequest, AUTOPAY_ADD_ACTION);
        } else {
            EbillAutoPayModifyRequest modifyRequest = mapper.toEbillAutoPayModifyRequest(request.getAutopay());
            modifyRequest.getAutopayInfo().setPayeeId(Long.valueOf(payeeId));
            modifyRequest.setHeader(header);
            client.call(modifyRequest, AUTOPAY_MODIFY_ACTION);
        }
        return new BillPayAutopayPutResponseBody().withId(payeeId);
    }

    @Override
    public void deleteAutopayByPayeeId(Header header, String payeeId) {
        EbillAutoPayCancelRequest cancelRequest = EbillAutoPayCancelRequest.builder()
                        .header(header)
                        .payeeId(Long.valueOf(payeeId))
                        .build();
        client.call(cancelRequest, AUTOPAY_CANCEL_ACTION);
    }
}
