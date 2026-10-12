package com.smxplore.proto.infrastructure.config;

import com.smxplore.proto.application.activities.CancelActivityUseCase;
import com.smxplore.proto.application.activities.ChangeActivityStatusUseCase;
import com.smxplore.proto.application.activities.ConsultActivitiesUseCase;
import com.smxplore.proto.application.activities.FindActivitiesByCategoryUseCase;
import com.smxplore.proto.application.activities.FindActivitiesBySiteUseCase;
import com.smxplore.proto.application.activities.ManageAvailabilityUseCase;
import com.smxplore.proto.application.activities.ManageSlotsUseCase;
import com.smxplore.proto.application.activities.RegisterActivityUseCase;
import com.smxplore.proto.application.activities.UpdateActivityUseCase;
import com.smxplore.proto.application.activities.ViewActivityDetailsUseCase;
import com.smxplore.proto.domain.ports.repository.ActivityRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.activity.CancelActivityUseCasePort;
import com.smxplore.proto.domain.ports.usecases.activity.ChangeActivityStatusUseCasePort;
import com.smxplore.proto.domain.ports.usecases.activity.ConsultActivitiesUseCasePort;
import com.smxplore.proto.domain.ports.usecases.activity.FindActivitiesByCategoryUseCasePort;
import com.smxplore.proto.domain.ports.usecases.activity.FindActivitiesBySiteUseCasePort;
import com.smxplore.proto.domain.ports.usecases.activity.ManageAvailabilityUseCasePort;
import com.smxplore.proto.domain.ports.usecases.activity.ManageSlotsUseCasePort;
import com.smxplore.proto.domain.ports.usecases.activity.RegisterActivityUseCasePort;
import com.smxplore.proto.domain.ports.usecases.activity.UpdateActivityUseCasePort;
import com.smxplore.proto.domain.ports.usecases.activity.ViewActivityDetailsUseCasePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ActivityUseCaseConfiguration {

    @Bean
    public RegisterActivityUseCasePort registerActivityUseCase(ActivityRepositoryPort activityRepository) {
        return new RegisterActivityUseCase(activityRepository);
    }

    @Bean
    public ViewActivityDetailsUseCasePort viewActivityDetailsUseCase(ActivityRepositoryPort activityRepository) {
        return new ViewActivityDetailsUseCase(activityRepository);
    }

    @Bean
    public ConsultActivitiesUseCasePort consultActivitiesUseCase(ActivityRepositoryPort activityRepository) {
        return new ConsultActivitiesUseCase(activityRepository);
    }

    @Bean
    public FindActivitiesByCategoryUseCasePort findActivitiesByCategoryUseCase(ActivityRepositoryPort activityRepository) {
        return new FindActivitiesByCategoryUseCase(activityRepository);
    }

    @Bean
    public FindActivitiesBySiteUseCasePort findActivitiesBySiteUseCase(ActivityRepositoryPort activityRepository) {
        return new FindActivitiesBySiteUseCase(activityRepository);
    }

    @Bean
    public UpdateActivityUseCasePort updateActivityUseCase(ActivityRepositoryPort activityRepository) {
        return new UpdateActivityUseCase(activityRepository);
    }

    @Bean
    public ManageAvailabilityUseCasePort manageAvailabilityUseCase(ActivityRepositoryPort activityRepository) {
        return new ManageAvailabilityUseCase(activityRepository);
    }

    @Bean
    public ManageSlotsUseCasePort manageSlotsUseCase(ActivityRepositoryPort activityRepository) {
        return new ManageSlotsUseCase(activityRepository);
    }

    @Bean
    public CancelActivityUseCasePort cancelActivityUseCase(ActivityRepositoryPort activityRepository) {
        return new CancelActivityUseCase(activityRepository);
    }

    @Bean
    public ChangeActivityStatusUseCasePort changeActivityStatusUseCase(ActivityRepositoryPort activityRepository) {
        return new ChangeActivityStatusUseCase(activityRepository);
    }
}
