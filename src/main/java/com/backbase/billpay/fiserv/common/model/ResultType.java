package com.backbase.billpay.fiserv.common.model;

import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.buildingblocks.presentation.errors.Error;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultType {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultType.class);
    public static final String INTERNAL_ERROR_KEY = "billpay.api.internalError";
    public static final String BAD_REQUEST = "Bad request";

    public static final Error GENERAL_ERROR = new Error().withKey(INTERNAL_ERROR_KEY)
                    .withMessage("Sorry, the request could not be completed at this time.");

    public static final BadRequestException GENERAL_EXCEPTION = new BadRequestException()
                    .withErrors(Collections.singletonList(GENERAL_ERROR)).withMessage(BAD_REQUEST);

    @XmlElement(name = "Success")
    private boolean success;

    private List<ResultInfo> resultInfo;

    /**
     * Tests the results for any errors, throws a BadRequestException if error(s) present.
     */
    public void checkForErrors() {
        if (!success) {
            if (CollectionUtils.isEmpty(resultInfo)) {
                LOGGER.warn("Error state but no error found.");
                throw GENERAL_EXCEPTION;
            } else {
                List<Error> errors = new ArrayList<>();
                resultInfo.stream()
                            .filter(ResultInfo::isError)
                            .forEach(item -> errors.add(item.getError()));
                throw new BadRequestException().withMessage(BAD_REQUEST).withErrors(errors);
            }
        }
    }
}
