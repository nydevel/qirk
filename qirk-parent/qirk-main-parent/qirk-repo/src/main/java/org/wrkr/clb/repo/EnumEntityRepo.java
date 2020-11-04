package org.wrkr.clb.repo;

public interface EnumEntityRepo<T, E extends Enum<E>> {

    public T getByNameCode(E enumNameCode);
}
