package com.smxplore.proto.domain.model.types;

import com.smxplore.proto.domain.exceptions.attribute.AttributeAlreadyAddedException;
import com.smxplore.proto.domain.exceptions.attribute.NullAttributeException;
import lombok.NoArgsConstructor;

import java.util.*;


@NoArgsConstructor
public final class AttributeMap {
    private final Map<String, Attribute> attributes = new LinkedHashMap<>();

    public static AttributeMap empty() {
        return new AttributeMap();
    }

    public static AttributeMap of(Collection<Attribute> attrs) {
        var map = new AttributeMap();
        if (attrs != null) attrs.forEach(map::addAttribute);
        return map;
    }

    public void addAttribute(Attribute attr) {
        if (attr == null) throw new NullAttributeException("Null attribute");

        attr.validate();
        if (attributes.containsKey(attr.name())) {
            throw new AttributeAlreadyAddedException();
        }
        attributes.put(attr.name(), attr);
    }

    public void updateAttribute(Attribute attr) {
        if (attr == null) throw new NullAttributeException("Null attribute");
        attr.validate();
        attributes.put(attr.name(), attr);
    }

    public boolean removeAttribute(String name) {
        return attributes.remove(name) != null;
    }

    public Optional<Attribute> get(String name) {
        return Optional.ofNullable(attributes.get(name));
    }

    public boolean contains(String name) {
        return attributes.containsKey(name);
    }

    public Collection<Attribute> all() {
        return Collections.unmodifiableCollection(attributes.values());
    }

    public int size() {
        return attributes.size();
    }

    public boolean isEmpty() {
        return attributes.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AttributeMap other)) return false;
        return attributes.equals(other.attributes);
    }

    @Override
    public int hashCode() {
        return attributes.hashCode();
    }

}
