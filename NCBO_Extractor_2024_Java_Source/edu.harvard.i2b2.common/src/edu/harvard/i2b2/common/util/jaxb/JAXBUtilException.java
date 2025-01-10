/*
 * Copyright (c) 2006-2025 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Rajesh Kuttan, Lori Phillips
 */

package edu.harvard.i2b2.common.util.jaxb;

public class JAXBUtilException extends Exception {
    /**
     */
    private static final long serialVersionUID = 1L;

    /**
    * Constructor that takes message and the exception as inputs.
    * @param message
    * @param e
    */
    public JAXBUtilException(String message, Exception e) {
        super(message, e);
    }

    /**
     * Constructor that takes message as input.
     * @param message
     */
    public JAXBUtilException(String message) {
        super(message);
    }
}
