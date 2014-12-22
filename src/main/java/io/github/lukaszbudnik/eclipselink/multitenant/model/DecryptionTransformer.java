package io.github.lukaszbudnik.eclipselink.multitenant.model;


import io.github.lukaszbudnik.eclipselink.multitenant.encryption.AsymmetricEncryptionUtils;
import io.github.lukaszbudnik.eclipselink.multitenant.encryption.SymmetricEncryptionUtils;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

import javax.xml.bind.DatatypeConverter;
import java.security.PrivateKey;
import java.util.Iterator;

public class DecryptionTransformer implements AttributeTransformer {

    private String fieldName;

    @Override
    public void initialize(AbstractTransformationMapping mapping) {
        this.fieldName = mapping.getAttributeName();
    }

    @Override
    public Object buildAttributeValue(Record record, Object object, Session session) {

        if (record.size() == 1) {
            return null;
        }

        PrivateKey privateKey = (PrivateKey) ((RepeatableWriteUnitOfWork) session).getParent().getProperty("private-key");

        Iterator<DatabaseField> keySet = record.keySet().iterator();

        DatabaseField keyField = null;
        DatabaseField fieldField = null;

        while (keySet.hasNext()) {
            DatabaseField field = keySet.next();
            if (field.getName().toLowerCase().equals("key")) {
                keyField = field;
            }
            if (field.getName().toLowerCase().equals(fieldName.toLowerCase())) {
                fieldField = field;
            }
        }

        byte[] encryptedKey = (byte[]) record.get(keyField);
        byte[] key = null;
        try {
            key = AsymmetricEncryptionUtils.decrypt(encryptedKey, privateKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String encryptedHexValue = (String) record.get(fieldField);
        byte[] encryptedValue = DatatypeConverter.parseHexBinary(encryptedHexValue);


        try {
            byte[] iv = "1234567890123456".getBytes();
            byte[] decryptedValue = SymmetricEncryptionUtils.decrypt(encryptedValue, key, iv);
            String decrypted = new String(decryptedValue);
            return decrypted;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
