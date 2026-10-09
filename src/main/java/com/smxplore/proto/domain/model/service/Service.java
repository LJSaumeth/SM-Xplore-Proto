package com.smxplore.proto.domain.model.service;

import com.smxplore.proto.domain.exceptions.service.InvalidServiceNameException;
import com.smxplore.proto.domain.exceptions.service.InvalidServicePriceException;
import com.smxplore.proto.domain.exceptions.service.TooManyExtraAttributeException;
import com.smxplore.proto.domain.model.types.Attribute;
import com.smxplore.proto.domain.model.types.AttributeMap;
import com.smxplore.proto.domain.model.types.Location;
import com.smxplore.proto.domain.model.types.ProviderRef;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Builder
@Getter
public class Service {
    private final UUID id;
    private final String name;
    private final ServiceType type;
    private final ProviderRef provider;
    private final Location location;
    private BigDecimal price;

    @Builder.Default
    private final List<String> photosUrls = new ArrayList<>();

    @Builder.Default
    private ServiceStatus status = ServiceStatus.AVAILABLE;

    @Builder.Default
    private final Instant createdAt = Instant.now();

    @Builder.Default
    private final AttributeMap extras = AttributeMap.empty();

    public void validate() {
        validateName();
        validatePrice(price);
        provider.validate();
        location.validate();
    }

    public boolean isAvailable() {
        return ServiceStatus.AVAILABLE.equals(status);
    }

    public void markAvailable() {
        if (isAvailable()) return;
        status = ServiceStatus.AVAILABLE;
    }

    public void markNotAvailable() {
        if (!isAvailable()) return;
        status = ServiceStatus.UNAVAILABLE;
    }

    public void changePrice(BigDecimal newPrice) {
        validatePrice(newPrice);
        price = newPrice;
    }

    public void addPhoto(String photoUrl) {
        if (photoUrl == null || photoUrl.isBlank()) return;
        var url = photoUrl.trim();
        if (photosUrls.contains(url)) return;

        photosUrls.add(url);
    }

    public boolean addPhotos(Collection<String> photoUrls) {
        if (photoUrls == null || photoUrls.isEmpty()) return false;
        photoUrls.forEach(this::addPhoto);
        return true;
    }

    public List<String> getPhotosUrls() {
        return Collections.unmodifiableList(photosUrls);
    }

    public void addExtra(Attribute extra) {
        if (extras.size() >= 15)
            throw new TooManyExtraAttributeException("Too many extra attribute. Max: 15");
        extras.addAttribute(extra);
    }

    public void addExtras(Collection<Attribute> extras) {
        extras.forEach(this::addExtra);
    }

    public void updateExtra(Attribute extra) {
        extras.updateAttribute(extra);
    }

    private void validateName() {
        if (name == null || name.isBlank())
            throw new InvalidServiceNameException("Name is null or blank.");
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0)
            throw new InvalidServicePriceException("Price must be greater than zero.");
    }
}
