package com.github.provitaliy.userservice.mapper;

import com.github.provitaliy.common.dto.AppUserCreateDTO;
import com.github.provitaliy.common.dto.AppUserDTO;
import com.github.provitaliy.common.grpc.AppUserResponse;
import com.github.provitaliy.common.grpc.GetOrCreateAppUserRequest;
import com.google.protobuf.Timestamp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class AppUserGrpcMapper {

    public abstract AppUserCreateDTO fromGrpc(GetOrCreateAppUserRequest request);

    @Mapping(source = "firstLoginDate", qualifiedByName = "toTimestamp", target = "firstLoginDate")
    public abstract AppUserResponse toGrpc(AppUserDTO appUserData);

    @Named("toTimestamp")
    protected Timestamp toTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) {
            return Timestamp.getDefaultInstance();
        }
        return Timestamp.newBuilder()
                .setSeconds(dateTime.atZone(ZoneId.systemDefault()).toEpochSecond())
                .build();
    }
}
