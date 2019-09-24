package com.backbase.billpay.fiserv.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract class for Bill Pay controllers.
 */
public abstract class AbstractController {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    protected FiservUtils fiservUtils;

}
