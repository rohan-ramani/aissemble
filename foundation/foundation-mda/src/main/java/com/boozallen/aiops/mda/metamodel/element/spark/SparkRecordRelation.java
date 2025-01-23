package com.boozallen.aiops.mda.metamodel.element.spark;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */


import org.apache.commons.lang3.StringUtils;

import com.boozallen.aiops.mda.metamodel.element.BaseRecordRelationDecorator;
import com.boozallen.aiops.mda.metamodel.element.Relation;

/**
 * Decorates RecordField with Spark-specific functionality.
 */
public class SparkRecordRelation extends BaseRecordRelationDecorator {

    /**
     * {@inheritDoc}
     */
    public SparkRecordRelation(Relation recordRelationToDecorate) {
        super(recordRelationToDecorate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDocumentation() {
        return StringUtils.isNotBlank(super.getDocumentation()) ? super.getDocumentation() : "";
    }
}
