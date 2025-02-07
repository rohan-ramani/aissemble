package com.boozallen.aiops.mda.metamodel.element.python;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.metamodel.element.BaseRecordRelationDecorator;
import com.boozallen.aiops.mda.metamodel.element.Relation;
import com.boozallen.aiops.mda.metamodel.element.util.PythonElementUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Decorates RecordRelation with Python-specific functionality.
 */
public class PythonRecordRelation extends BaseRecordRelationDecorator {

    /**
     * {@inheritDoc}
     */
    public PythonRecordRelation(Relation recordToDecorate) {
        super(recordToDecorate);
    }


    /**
     * Returns the record name formatted into lowercase with underscores (Python
     * naming convention).
     * 
     * @return the record name formatted into lowercase with underscores
     */
    public String getSnakeCaseName() {
        return PythonElementUtils.getSnakeCaseValue(getName());
    }

    /**
     * Returns the import for the generating the setters/getters of the reference record.
     * @return generated class import
     */
    public Set<String> getGeneratedClassImport() {
        Set<String> relationImports = new HashSet<>();
        String pythonPackage = "...record." + getSnakeCaseName();
        String generatedClassType = pythonPackage + "." + wrapped.getName();
        relationImports.add(PythonElementUtils.derivePythonImport(generatedClassType));

        if (isOneToManyRelation()) {
            relationImports.add("from typing import List");
        }
        return relationImports;
    }

    /**
     * Returns the reference record python setter functoin signature for generating the base record class
     * @return the reference record python setter function signature
     */
    public String getRelationSetterSignature() {
        String className = getName();
        if (this.isOneToManyRelation()) {
            return String.format("def %s(self, %s:List[%s] = []): ", getSnakeCaseName(), getSnakeCaseName(), className);
        } else {
            return String.format("def %s(self, %s:%s = None):", getSnakeCaseName(), getSnakeCaseName(), className);
        }
    }

    /**
     * Returns the reference record python getter function signature for generating the base record class
     * @return the reference record python getter function signature
     */
    public String getRelationGetterSignature() {
        String className = getName();
        if (this.isOneToManyRelation()) {
            return String.format("def %s(self) -> List[%s]: ", getSnakeCaseName(), className);
        } else {
            return String.format("def %s(self) -> %s:", getSnakeCaseName(), className);
        }
    }

    /**
     * Returns the reference record property declaration for generating the base record class
     * @return the reference record property declaration
     */
    public String getRelationPropDeclaration() {
        String className = getName();
        if (this.isOneToManyRelation()) {
            return String.format("self._%s: List[%s] = []", getSnakeCaseName(), className);
        } else {
            return String.format("self._%s: %s = None", getSnakeCaseName(), className);
        }
    }

    /**
     * Returns the reference record validation logic for generating the base record class
     * @return the reference record validation logic
     */
    public String getRelationValidate() {
        String snakeCaseName = getSnakeCaseName();
        if (this.isOneToManyRelation()) {
            String validate = """
                    for %s in self._%s:
                                    %s.validate()""";
            return String.format(validate, snakeCaseName, snakeCaseName, snakeCaseName);
        } else {
            return String.format("self._%s.validate()", snakeCaseName);
        }
    }

    /**
     * Returns the required reference record validation logic for generating the base record class
     * @return the required reference record validation logic
     */
    public String getRequiredRelationValidate() {
        String snakeCaseName = getSnakeCaseName();
        if (this.isOneToManyRelation()) {
            String validate = """
                    if self._%s is None or len(self._%s) == 0:
                                raise ValueError('Relation record "%s" is required')
                            else:
                                %s""";
            return String.format(validate, snakeCaseName, snakeCaseName, getName(), getRelationValidate());
        } else {
            String validate = """
                    if self._%s is None:
                                raise ValueError('Relation record "%s" is required')
                            else:
                                %s""";
            return String.format(validate, snakeCaseName, getName(), getRelationValidate());
        }
    }

}
