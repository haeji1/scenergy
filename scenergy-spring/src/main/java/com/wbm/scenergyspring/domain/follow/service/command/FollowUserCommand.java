package com.wbm.scenergyspring.domain.follow.service.command;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FollowUserCommand {
	Long fromUserId;
	Long toUserId;
}
