/*
 * Copyright (c) 2006-2025 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Rajesh Kuttan, Lori Phillips
 */

package edu.harvard.i2b2.common.util;

import org.springframework.beans.factory.BeanFactory;


/**
 * This class encapsulates Spring's BeanFactory.
 * @author Rajesh Kuttan
 */
public class PropertyUtil {
    private BeanFactory beanFactory = null;

    public PropertyUtil() {
    }

    public PropertyUtil(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    /**
     * Call's BeanFactory's getBean
     * @param beanName
     * @return
     */
    public Object getProperty(String beanName) {
        return beanFactory.getBean(beanName);
    }
}
