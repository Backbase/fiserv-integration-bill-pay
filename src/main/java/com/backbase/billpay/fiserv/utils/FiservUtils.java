package com.backbase.billpay.fiserv.utils;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.payees.model.BldrDate;
import com.backbase.buildingblocks.backend.communication.event.proxy.RequestProxyWrapper;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Util class for Fiserv working with Fiserv objects.
 */
@Component
public class FiservUtils {
    
    private static final String FISERV_DATE_FORMAT = "yyyy-MM-dd";

    @Value("${backbase.billpay.provider.clientAppText}")
    private String clientAppText;
    
    @Value("${backbase.billpay.provider.clientAppVersion}")
    private String clientAppVersion;
    
    @Value("${backbase.billpay.provider.sponsorId}")
    private String sponsorId;

    /**
     * Creates a header for the specified subscriber from the request context.
     * @param request the request wrapper
     * @param subscriberId the subscriber for the user
     * @return
     */
    public Header createHeader(RequestProxyWrapper<?> request, String subscriberId) {
        return Header.builder().subscriberId(subscriberId)
                               .clientAppText(clientAppText)
                               .clientAppVersion(clientAppVersion)
                               .sponsorId(sponsorId)
                               .correlationId(UUID.randomUUID().toString())
                               .subscriberIpAddress(request.getRequest().getInternalRequestContext().getSourceAddress())
                               .build();
    }
    
    /**
     * Convert a BldrDate to a Date object.
     * @param bldrDate Date to convert
     * @return Converted date
     */
    public static Date fromFiservDate(BldrDate bldrDate) {
        try {
            DateFormat format = new SimpleDateFormat(FISERV_DATE_FORMAT);
            return format.parse(bldrDate.getDate());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * Convert a Date to a BldrDate object.
     * @param date Date to convert
     * @return Converted date
     */
    public static BldrDate toFiservDate(Date date) {
        return BldrDate.builder()
                        .date(new SimpleDateFormat(FISERV_DATE_FORMAT).format(date))
                        .build();
    }
    
    public static BldrDate todayFiservDate() {
        return toFiservDate(new Date());
    }
}