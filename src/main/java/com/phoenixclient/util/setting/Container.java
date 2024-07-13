package com.phoenixclient.util.setting;

public class Container<T> {

    private T value;

    public Container(T value) {
        this.value = value;
    }


    public T get() {
        return value;
    }

    public Container<T> set(T value) {
        this.value = value;
        return this;
    }


    @Override
    public String toString() {
        return "[Container: " + get() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Container<?> container)) return false;
        return container.get().equals(get());
    }
}
