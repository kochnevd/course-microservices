package kda.learn.microservices.hw2.storage.entities;

import kda.learn.microservices.hw2.model.UserData;

import javax.validation.constraints.Null;

public class Model2EntityTransformer {
    public static User UserModel2Entity(@Null UserData userData) {
        if (userData == null)
            return null;

        return new User()
                .setId(userData.getId())
                .setUserName(userData.getUsername())
                .setFirstName(userData.getFirstName())
                .setLastName(userData.getLastName())
                .setEmail(userData.getEmail())
                .setPhone(userData.getPhone());
    }

    public static UserData UserEntity2Model(@Null User user) {
        if (user == null)
            return null;

        return new UserData()
                .setId(user.getId())
                .setUsername(user.getUserName())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setEmail(user.getEmail())
                .setPhone(user.getPhone());
    }
}
