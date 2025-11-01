package com.github.provitaliy.node.mapper;

import com.github.provitaliy.common.dto.AppUserCreateDTO;
import com.github.provitaliy.common.grpc.AppUserResponse;
import com.github.provitaliy.common.grpc.GetOrCreateAppUserRequest;
import com.github.provitaliy.node.user.NodeUser;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class NodeUserMapper {
    public abstract GetOrCreateAppUserRequest toGrpc(AppUserCreateDTO userCreateDto);
    public abstract NodeUser fromGrpc(AppUserResponse appUserResponse);
}
