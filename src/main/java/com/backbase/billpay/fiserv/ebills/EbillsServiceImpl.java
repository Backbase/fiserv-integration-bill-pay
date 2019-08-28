package com.backbase.billpay.fiserv.ebills;

import static com.backbase.billpay.fiserv.utils.FiservUtils.toFiservDate;
import static com.backbase.billpay.fiserv.utils.PaginationUtils.paginateList;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.ebills.model.EbillAccountCancelRequest;
import com.backbase.billpay.fiserv.ebills.model.EbillFileRequest;
import com.backbase.billpay.fiserv.payeessummary.model.Ebill;
import com.backbase.billpay.fiserv.payeessummary.model.Ebill.EbillStatus;
import com.backbase.billpay.fiserv.payeessummary.model.EbillFilter;
import com.backbase.billpay.fiserv.payeessummary.model.EbillListRequest;
import com.backbase.billpay.fiserv.payeessummary.model.EbillListResponse;
import com.backbase.billpay.fiserv.utils.FiservClient;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.BillPayEbillsGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.EbillByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.EbillByIdPutResponseBody;
import java.time.Duration;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Payee eBills service, integrates with the Bill Pay provider.
 */
@Service
public class EbillsServiceImpl implements EbillsService {
    
    protected static final int POSITIVE_MAX_DAYS = 360;
    protected static final int NEGATIVE_MAX_DAYS = -10000;
    private static final String ASC = "ASC";
    private static final String UNPAID = "UNPAID";
    private static final String PAID = "PAID";
    private static final String EBILL_FILE_ACTION = "EbillFile";
    private static final String EBILL_LIST_ACTION = "EbillList";
    private static final String EBILLS_CANCEL_ACTION = "EbillAccountCancel";
    
    private EbillsMapper mapper;
    
    private FiservClient client;
    
    @Autowired
    public EbillsServiceImpl(EbillsMapper mapper, FiservClient client) {
        this.mapper = mapper;
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
    
    @Override
    public BillPayEbillsGetResponseBody getEbills(Header header, String payeeId, String status, Date startDate,
                    Date endDate, Integer from, Integer size, String orderBy, String direction) {
        
        EbillFilter ebillFilter = null;
        if (startDate != null || endDate != null) {
            ebillFilter = new EbillFilter();
            if (startDate != null && endDate != null) {
                ebillFilter.setStartingDate(toFiservDate(startDate));
                ebillFilter.setNumberOfDays(
                                (int) Duration.between(startDate.toInstant(), endDate.toInstant()).toDays());
            } else if (startDate == null) {
                ebillFilter.setStartingDate(toFiservDate(endDate));
                ebillFilter.setNumberOfDays(NEGATIVE_MAX_DAYS);
            } else {
                ebillFilter.setStartingDate(toFiservDate(startDate));
                ebillFilter.setNumberOfDays(POSITIVE_MAX_DAYS);
            }
        }
        
        EbillListRequest ebillListRequest = EbillListRequest.builder()
                        .header(header)
                        .filter(ebillFilter)
                        .build();
        EbillListResponse ebillListResponse = client.call(ebillListRequest, EBILL_LIST_ACTION);
        
        List<Ebill> ebills = ebillListResponse.getEbillList();
        
        // return empty response if no eBills found
        if (ebills == null || ebills.isEmpty()) {
            return new BillPayEbillsGetResponseBody().withTotalCount(Long.valueOf(0));
        }
        
        // filter eBills by status
        if (status.equalsIgnoreCase(PAID)) {
            ebills = ebills.stream()
                            .filter(e -> e.getStatus() == EbillStatus.PAID || e.getStatus() == EbillStatus.FILED)
                            .collect(Collectors.toList());
        } else if (status.equalsIgnoreCase(UNPAID)) {
            ebills = ebills.stream()
                            .filter(e -> e.getStatus() == EbillStatus.UNPAID
                                            || e.getStatus() == EbillStatus.PAYMENT_FAILED
                                            || e.getStatus() == EbillStatus.PAYMENT_CANCELLED)
                            .collect(Collectors.toList());
        }
        
        // sort the eBills
        Comparator<Ebill> comparator = (e1, e2) -> e2.getDueDate().compareTo(e1.getDueDate());
        if (ASC.equals(direction)) {
            ebills.sort(comparator.reversed());
        } else {
            ebills.sort(comparator);
        }
        
        // paginate the data
        List<Ebill> paginatedEbills = paginateList(ebills, from, size);
        
        return new BillPayEbillsGetResponseBody()
                        .withEbills(mapper.toEbillList(paginatedEbills))
                        .withTotalCount(Long.valueOf(ebills.size()));
    }

    @Override
    public EbillByIdPutResponseBody updateEbillStatus(Header header, EbillByIdPutRequestBody request, String ebillId) {
        EbillFileRequest fileRequest = mapper.toEbillFileRequest(request, ebillId);
        fileRequest.setHeader(header);
        client.call(fileRequest, EBILL_FILE_ACTION);
        return new EbillByIdPutResponseBody().withId(ebillId);
    }

}
