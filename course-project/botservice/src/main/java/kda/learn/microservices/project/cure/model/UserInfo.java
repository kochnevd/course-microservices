package kda.learn.microservices.project.cure.model;

import java.util.Objects;

public class UserInfo {
    private final int id;

    public UserInfo(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo userInfo = (UserInfo) o;
        return id == userInfo.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
