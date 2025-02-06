package com.boozallen.aiops.mda.metamodel.element.pyspark;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.aiops.mda.metamodel.element.Record;
import com.boozallen.aiops.mda.metamodel.element.RecordField;
import com.boozallen.aiops.mda.metamodel.element.Relation;
import com.boozallen.aiops.mda.metamodel.element.python.PythonRecord;
import com.boozallen.aiops.mda.metamodel.element.python.PythonRecordRelation;

/**
 * Decorates Record with PySpark-specific functionality.
 */
public class PySparkSchemaRecord extends PythonRecord {

    private static final Logger logger = LoggerFactory.getLogger(PySparkSchemaRecord.class);

    private static final String SCHEMA_PACKAGE = "from ...schema.%s_schema import %sSchema";
    private static final String PYSPARK_ARRAY_IMPORT = "from pyspark.sql.types import ArrayType";
    private Set<String> imports = new TreeSet<>();

    /**
     * {@inheritDoc}
     */
    public PySparkSchemaRecord(Record recordToDecorate) {
        super(recordToDecorate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RecordField> getFields() {
        List<RecordField> fields = new ArrayList<>();

        for (RecordField field : wrapped.getFields()) {
            PySparkRecordField pySparkField = new PySparkRecordField(field);
            PySparkRecordFieldType fieldType = (PySparkRecordFieldType) pySparkField.getType();
            if (fieldType.isDictionaryTyped()) {
                fields.add(pySparkField);
            } else {
                logger.warn(
                        "PySpark schema does not support non-dictionary types - skip adding field '{}' to PySpark schema.",
                        pySparkField.getName());
            }
        }

        return fields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getBaseImports() {
        for (RecordField field : getFields()) {
            PySparkRecordField pySparkField = (PySparkRecordField) field;
            PySparkRecordFieldType fieldType = (PySparkRecordFieldType) pySparkField.getType();
            PySparkDictionaryType dictionaryType = (PySparkDictionaryType) fieldType.getDictionaryType();
            String dictionaryTypeImport = dictionaryType.getSimpleTypeImport();
            if (StringUtils.isNotBlank(dictionaryTypeImport)) {
                imports.add(dictionaryTypeImport);
            }
        }
        boolean isArrayImportAdded = false;
        for (Relation relation : getRelations()) {
            PythonRecordRelation wrappedRelation = new PythonRecordRelation(relation);
            if(wrappedRelation.isOneToManyRelation() && !isArrayImportAdded) {
                isArrayImportAdded = true;
                imports.add(PYSPARK_ARRAY_IMPORT);
            }
            imports.add(String.format(SCHEMA_PACKAGE, wrappedRelation.getSnakeCaseName(), relation.getName()));
        }

        return imports;
    }

}
