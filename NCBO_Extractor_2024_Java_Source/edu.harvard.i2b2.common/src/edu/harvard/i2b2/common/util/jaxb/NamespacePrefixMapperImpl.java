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

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;


class NamespacePrefixMapperImpl extends NamespacePrefixMapper {
    public String getPreferredPrefix(String namespaceUri, String suggestion,
        boolean requirePrefix) {
        if ("http://www.w3.org/2001/XMLSchema-instance".equals(namespaceUri)) {
            return "xsi";
        } else if ("http://i2b2.mgh.harvard.edu/message".equals(namespaceUri)) {
            return "i2b2";
        } else {
            return suggestion;
        }
    }

    public String[] getPreDeclaredNamespaceUris() {
        return new String[] {  };
    }
}
