package kda.learn.microservices.hw2.transformers;

import kda.learn.microservices.hw2.dto.UserDto;
import kda.learn.microservices.hw2.model.UserData;

public class UserDtoTransformer {

    public static UserDto transformToDto(UserData userData) {
        return new UserDto()
                .id(userData.getId())
                .username(userData.getUsername())
                .firstName(userData.getFirstName())
                .lastName(userData.getLastName())
                .email(userData.getEmail())
                .phone(userData.getPhone());
    }

    public static UserData transformFromDto(UserDto userDto) {
        return new UserData()
                .setId(userDto.getId())
                .setUsername(userDto.getUsername())
                .setFirstName(userDto.getFirstName())
                .setLastName(userDto.getLastName())
                .setEmail(userDto.getEmail())
                .setPhone(userDto.getPhone());

    }
}
