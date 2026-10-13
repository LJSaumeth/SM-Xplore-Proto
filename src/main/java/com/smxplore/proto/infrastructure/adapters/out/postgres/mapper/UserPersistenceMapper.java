package com.smxplore.proto.infrastructure.adapters.out.postgres.mapper;

import com.smxplore.proto.domain.model.admin.Admin;
import com.smxplore.proto.domain.model.destinationmanager.DestinationManager;
import com.smxplore.proto.domain.model.guide.Guide;
import com.smxplore.proto.domain.model.serviceprovider.ServiceProvider;
import com.smxplore.proto.domain.model.tourist.Tourist;
import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.AdminEntity;
import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.DestinationManagerEntity;
import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.GuideEntity;
import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.ServiceProviderEntity;
import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.TouristEntity;
import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    UserEntity toUserEntity(User user);

    AdminEntity toAdminEntity(Admin admin);

    TouristEntity toTouristEntity(Tourist tourist);

    GuideEntity toGuideEntity(Guide guide);

    DestinationManagerEntity toDestinationManagerEntity(DestinationManager destinationManager);

    ServiceProviderEntity toServiceProviderEntity(ServiceProvider serviceProvider);

    @Mapping(target = "id", source = "user.id")
    Admin toAdmin(UserEntity user, AdminEntity admin);

    @Mapping(target = "id", source = "user.id")
    Tourist toTourist(UserEntity user, TouristEntity tourist);

    @Mapping(target = "id", source = "user.id")
    Guide toGuide(UserEntity user, GuideEntity guide);

    @Mapping(target = "id", source = "user.id")
    DestinationManager toDestinationManager(UserEntity user, DestinationManagerEntity destinationManager);

    @Mapping(target = "id", source = "user.id")
    ServiceProvider toServiceProvider(UserEntity user, ServiceProviderEntity serviceProvider);
}
