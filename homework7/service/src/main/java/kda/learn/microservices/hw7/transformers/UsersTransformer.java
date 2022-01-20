package kda.learn.microservices.hw7.transformers;

import kda.learn.microservices.hw7.dto.UserReqDto;
import kda.learn.microservices.hw7.dto.UserRespDto;
import kda.learn.microservices.hw7.storage.entities.User;

public class UsersTransformer {
    public static User transformFromDto(UserReqDto userDto) {
        return new User()
                .setLogin(userDto.getLogin())
                .setFirstName(userDto.getFirstName())
                .setLastName(userDto.getLastName())
                .setEmail(userDto.getEmail());
    }

    public static UserRespDto transformToDto(User user) {
        return new UserRespDto()
                .id(user.getId())
                .accountId(user.getAccountId());
    }
}
