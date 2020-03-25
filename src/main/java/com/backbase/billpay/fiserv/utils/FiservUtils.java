package com.backbase.billpay.fiserv.utils;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.payees.model.BldrDate;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
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
     * @param request the http servlet request
     * @param subscriberId the subscriber for the user
     * @return header
     */
    public Header createHeader(HttpServletRequest request, String subscriberId) {
        return Header.builder().subscriberId(subscriberId)
                        .clientAppText(clientAppText)
                        .clientAppVersion(clientAppVersion)
                        .sponsorId(sponsorId)
                        .correlationId(UUID.randomUUID().toString())
                        .subscriberIpAddress(request.getRemoteAddr())
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

    /**
     * Covert a Date to a ZonedDateTime object with Eastern time zone.
     * @param date the date to convert
     * @return a ZonedDateTime object with Eastern time zone
     */
    public static ZonedDateTime toZonedDateTime(Date date) {
        ZoneId est = ZoneId.of("America/New_York");
        return ZonedDateTime.ofInstant(date.toInstant(), est);
    }

    /**
     * Convert LocalDate to Date object with Eastern time zone.
     * @param date the date to be converted
     * @return a Date object with Eastern time zone
     */
    public static Date fromLocalDate(LocalDate date) {
        ZoneId est = ZoneId.of("America/New_York");
        return Date.from(date.atStartOfDay(est).toInstant());
    }
}