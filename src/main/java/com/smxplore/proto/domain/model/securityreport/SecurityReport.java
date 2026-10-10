package com.smxplore.proto.domain.model.securityreport;

import com.smxplore.proto.domain.exceptions.securityreport.InvalidStatusTransitionException;
import com.smxplore.proto.domain.exceptions.securityreport.ReportWithoutContentException;
import com.smxplore.proto.domain.model.types.Location;
import com.smxplore.proto.domain.model.types.UserRef;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.*;

@Builder(toBuilder = true)
@Getter
public class SecurityReport {
    private final UUID id;
    private UserRef user;
    private String text;
    private final Location location;

    @Builder.Default
    private final List<String> photosUrls = new ArrayList<>();

    @Builder.Default
    private SecurityReportStatus status = SecurityReportStatus.ONGOING;

    @Builder.Default
    private final Instant createdAt = Instant.now();

    public void validate() {
        validateText(text);
        if (location != null){
            location.validate();
        }
    }

    public void changeText(String text) {
        validateText(text);
        this.text = text;
    }

    public boolean addPhotos(Collection<String> photos) {
        if (photos == null) return false;
        photos.forEach(this::addPhoto);
        return true;
    }

    public void addPhoto(String photoUrl) {
        if (photoUrl == null || photoUrl.isBlank()) return;
        var url = photoUrl.trim();
        if (photosUrls.contains(url)) return;

        photosUrls.add(url);
    }

    public boolean removePhoto(String photo) {
        return photosUrls.remove(photo);
    }

    public boolean isOngoing() {
        return SecurityReportStatus.ONGOING.equals(status);
    }

    public boolean isReported() {
        return SecurityReportStatus.REPORTED.equals(status);
    }

    public boolean isSolved(){
        return SecurityReportStatus.SOLVED.equals(status);
    }

    public void markAsSolved(){
        if (isSolved()) throw new InvalidStatusTransitionException("Already solved");
        status = SecurityReportStatus.SOLVED;
    }

    public List<String> getPhotosUrls() {
        return Collections.unmodifiableList(photosUrls);
    }

    public void markAsReported(){
        if (!isOngoing()) throw new InvalidStatusTransitionException("SecurityReport is not ongoing");
        status = SecurityReportStatus.REPORTED;
    }

    private void validateText(String text) {
        if (text == null || text.isBlank())
            throw new ReportWithoutContentException("text is empty");
    }
}
