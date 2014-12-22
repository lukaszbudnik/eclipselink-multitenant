package io.github.lukaszbudnik.eclipselink.multitenant.model;


import io.github.lukaszbudnik.eclipselink.multitenant.encryption.SymmetricEncryptionUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.sessions.Session;

public class EncryptionTransformer implements FieldTransformer {

    @Override
    public void initialize(AbstractTransformationMapping mapping) {

    }

    @Override
    public Object buildFieldValue(Object instance, String fieldName, Session session) {
        byte[] key = (byte[]) ((RepeatableWriteUnitOfWork) session).getParent().getProperty("key");

        if (key == null) {
            try {
                return PropertyUtils.getProperty(instance, fieldName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        byte[] iv = "1234567890123456".getBytes();

        try {
            String fieldValue = (String) PropertyUtils.getProperty(instance, fieldName);

            byte[] encryptedValue = SymmetricEncryptionUtils.encrypt(fieldValue.getBytes(), key, iv);

            return encryptedValue;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
