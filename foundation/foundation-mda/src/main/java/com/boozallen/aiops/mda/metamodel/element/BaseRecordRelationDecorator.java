package com.boozallen.aiops.mda.metamodel.element;

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
import org.technologybrewery.fermenter.mda.metamodel.element.MetamodelUtils;

import com.boozallen.aiops.mda.generator.util.PipelineUtils;

/**
 * Provides baseline decorator functionality for {@link Relation}.
 *
 * The goal is to make it easier to apply the decorator pattern in various implementations of generators (e.g., Java,
 * python, Docker) so that each concrete decorator only has to decorate those aspects of the class that are needed, not
 * all the pass-through methods that each decorator would otherwise need to implement (that add no real value).
 */
public class BaseRecordRelationDecorator implements Relation {

    protected Relation wrapped;

    /**
     * New decorator for {@link Relation}.
     *
     * @param relationToDecorate
     *            instance to decorate
     */
    public BaseRecordRelationDecorator(Relation relationToDecorate) {
        MetamodelUtils.validateWrappedInstanceIsNonNull(getClass(), relationToDecorate);
        wrapped = relationToDecorate;
    }

    @Override
    public String getDocumentation() {
        return wrapped.getDocumentation();
    }

    @Override
    public Multiplicity getMultiplicity() {
        return wrapped.getMultiplicity();
    }

    @Override
    public String getPackage() {
        return wrapped.getPackage();
    }

    @Override
    public String getFileName() {
        return wrapped.getFileName();
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    @Override
    public Boolean isRequired() {
        return wrapped.isRequired();
    }

    @Override
    public String getColumn() {
        return wrapped.getColumn();
    }

    @Override
    public void validate() {
        wrapped.validate();
    }

    /**
     * Check if the relation multiplicity is One to Many
     * @return true or false value
     */
    public boolean isOneToManyRelation() {
        return wrapped.getMultiplicity().equals(Multiplicity.ONE_TO_MANY);
    }

    /**
     * Whether the Spark relation is nullable.
     *
     * @return true if the Spark field is nullable
     */
    public boolean isNullable() {
        return wrapped.isRequired() == null || !wrapped.isRequired();
    }

    /**
     * Returns the column name for the Spark relation.
     *
     * @return column name
     */
    public String getColumnName() {
        String columnName;

        if (StringUtils.isNotBlank(wrapped.getColumn())) {
            columnName = wrapped.getColumn();
        } else {
            columnName = wrapped.getName();
        }
        return columnName;
    }

    /**
     * Returns the relation name formatted to uppercase with underscores.
     *
     * @return name formatted to uppercase with underscores
     */
    public String getUpperSnakecaseName() {
        return PipelineUtils.deriveUpperUnderscoreNameFromUpperCamelName(getName());
    }

    /**
     * Returns the relation name, capitalized.
     *
     * @return capitalized name
     */
    public String getCapitalizedName() {
        return StringUtils.capitalize(getName());
    }

    /**
     * Returns the relation name, uncapitalized.
     *
     * @return uncapitalized name
     */
    public String getUncapitalizeName() {
        return StringUtils.uncapitalize(getName());
    }
}
