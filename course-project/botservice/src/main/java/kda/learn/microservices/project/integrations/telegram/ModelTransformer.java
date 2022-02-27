package kda.learn.microservices.project.integrations.telegram;

import kda.learn.microservices.project.cure.model.UserInfo;
import org.telegram.telegrambots.meta.api.objects.User;

public final class ModelTransformer {

    public static UserInfo userTgToModel(User tgUser) {
        var res = new UserInfo(tgUser.getId());
        return res;
    }

}
