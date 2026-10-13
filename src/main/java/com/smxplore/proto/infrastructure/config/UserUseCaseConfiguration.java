package com.smxplore.proto.infrastructure.config;

import com.smxplore.proto.application.users.AdminDeleteUserAccountUseCase;
import com.smxplore.proto.application.users.ApproveServiceProviderRegistrationUseCase;
import com.smxplore.proto.application.users.AssignUserRoleUseCase;
import com.smxplore.proto.application.users.BlockOrSuspendUserUseCase;
import com.smxplore.proto.application.users.DeleteOwnAccountUseCase;
import com.smxplore.proto.application.users.GetUserProfileUseCase;
import com.smxplore.proto.application.users.ListUsersUseCase;
import com.smxplore.proto.application.users.LoginUseCase;
import com.smxplore.proto.application.users.RecoverPasswordUseCase;
import com.smxplore.proto.application.users.RegisterUserUseCase;
import com.smxplore.proto.application.users.ResetUserPasswordUseCase;
import com.smxplore.proto.application.users.SearchUserUseCase;
import com.smxplore.proto.application.users.UpdateUserProfileUseCase;
import com.smxplore.proto.application.users.ViewGuideProfileUseCase;
import com.smxplore.proto.application.users.ViewServiceProviderProfileUseCase;
import com.smxplore.proto.domain.ports.repository.GuideRepositoryPort;
import com.smxplore.proto.domain.ports.repository.ServiceProviderRepositoryPort;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.security.PasswordHasherPort;
import com.smxplore.proto.domain.ports.usecases.AdminDeleteUserAccountUseCasePort;
import com.smxplore.proto.domain.ports.usecases.ApproveServiceProviderRegistrationUseCasePort;
import com.smxplore.proto.domain.ports.usecases.AssignUserRoleUseCasePort;
import com.smxplore.proto.domain.ports.usecases.BlockOrSuspendUserUseCasePort;
import com.smxplore.proto.domain.ports.usecases.DeleteOwnAccountUseCasePort;
import com.smxplore.proto.domain.ports.usecases.GetUserProfileUseCasePort;
import com.smxplore.proto.domain.ports.usecases.ListUsersUseCasePort;
import com.smxplore.proto.domain.ports.usecases.LoginUseCasePort;
import com.smxplore.proto.domain.ports.usecases.RecoverPasswordUseCasePort;
import com.smxplore.proto.domain.ports.usecases.RegisterUserUseCasePort;
import com.smxplore.proto.domain.ports.usecases.ResetUserPasswordUseCasePort;
import com.smxplore.proto.domain.ports.usecases.SearchUserUseCasePort;
import com.smxplore.proto.domain.ports.usecases.UpdateUserProfileUseCasePort;
import com.smxplore.proto.domain.ports.usecases.ViewGuideProfileUseCasePort;
import com.smxplore.proto.domain.ports.usecases.ViewServiceProviderProfileUseCasePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class UserUseCaseConfiguration {

    @Bean
    public RegisterUserUseCasePort registerUserUseCase(UserRepositoryPort userRepository,
                                                      PasswordHasherPort passwordHasher) {
        return new RegisterUserUseCase(userRepository, passwordHasher);
    }

    @Bean
    public LoginUseCasePort loginUseCase(UserRepositoryPort userRepository,
                                         PasswordHasherPort passwordHasher) {
        return new LoginUseCase(userRepository, passwordHasher);
    }

    @Bean
    public RecoverPasswordUseCasePort recoverPasswordUseCase(UserRepositoryPort userRepository) {
        return new RecoverPasswordUseCase(userRepository);
    }

    @Bean
    public GetUserProfileUseCasePort getUserProfileUseCase(UserRepositoryPort userRepository) {
        return new GetUserProfileUseCase(userRepository);
    }

    @Bean
    public UpdateUserProfileUseCasePort updateUserProfileUseCase(UserRepositoryPort userRepository) {
        return new UpdateUserProfileUseCase(userRepository);
    }

    @Bean
    public DeleteOwnAccountUseCasePort deleteOwnAccountUseCase(UserRepositoryPort userRepository) {
        return new DeleteOwnAccountUseCase(userRepository);
    }

    @Bean
    public ApproveServiceProviderRegistrationUseCasePort approveServiceProviderRegistrationUseCase(
            ServiceProviderRepositoryPort serviceProviderRepository) {
        return new ApproveServiceProviderRegistrationUseCase(serviceProviderRepository);
    }

    @Bean
    public AssignUserRoleUseCasePort assignUserRoleUseCase(UserRepositoryPort userRepository) {
        return new AssignUserRoleUseCase(userRepository);
    }

    @Bean
    public BlockOrSuspendUserUseCasePort blockOrSuspendUserUseCase(UserRepositoryPort userRepository) {
        return new BlockOrSuspendUserUseCase(userRepository);
    }

    @Bean
    public AdminDeleteUserAccountUseCasePort adminDeleteUserAccountUseCase(UserRepositoryPort userRepository) {
        return new AdminDeleteUserAccountUseCase(userRepository);
    }

    @Bean
    public ListUsersUseCasePort listUsersUseCase(UserRepositoryPort userRepository) {
        return new ListUsersUseCase(userRepository);
    }

    @Bean
    public SearchUserUseCasePort searchUserUseCase(UserRepositoryPort userRepository) {
        return new SearchUserUseCase(userRepository);
    }

    @Bean
    public ResetUserPasswordUseCasePort resetUserPasswordUseCase(UserRepositoryPort userRepository,
                                                                 PasswordHasherPort passwordHasher) {
        return new ResetUserPasswordUseCase(userRepository, passwordHasher);
    }

    @Bean
    public ViewGuideProfileUseCasePort viewGuideProfileUseCase(GuideRepositoryPort guideRepository) {
        return new ViewGuideProfileUseCase(guideRepository);
    }

    @Bean
    public ViewServiceProviderProfileUseCasePort viewServiceProviderProfileUseCase(
            ServiceProviderRepositoryPort serviceProviderRepository) {
        return new ViewServiceProviderProfileUseCase(serviceProviderRepository);
    }
}
