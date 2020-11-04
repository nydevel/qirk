package org.wrkr.clb.test.util;

import org.easymock.EasyMock;
import org.springframework.beans.factory.FactoryBean;

public class EasyMockFactoryBean<T> implements FactoryBean<T> {

    private Class<T> mockedClass;

    public void setMockedClass(Class<T> mockedClass) {
        this.mockedClass = mockedClass;
    }

    @Override
    public T getObject() throws Exception {
        return EasyMock.createNiceMock(mockedClass);
    }

    @Override
    public Class<T> getObjectType() {
        return mockedClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}