package com.wbm.scenergyspring.domain.follow.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wbm.scenergyspring.config.auth.PrincipalDetails;
import com.wbm.scenergyspring.domain.follow.controller.request.CreateFollowRequest;
import com.wbm.scenergyspring.domain.follow.controller.request.FindAllFollowersRequest;
import com.wbm.scenergyspring.domain.follow.controller.request.FindAllFollowingRequest;
import com.wbm.scenergyspring.domain.follow.controller.response.DeleteFollowResponse;
import com.wbm.scenergyspring.domain.follow.controller.response.FindAllFollowerResponse;
import com.wbm.scenergyspring.domain.follow.entity.Follow;
import com.wbm.scenergyspring.domain.follow.service.FollowService;
import com.wbm.scenergyspring.domain.follow.service.command.UnFollowUserCommand;
import com.wbm.scenergyspring.domain.follow.service.commandresult.FollowCommandResult;
import com.wbm.scenergyspring.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowController {

	private final FollowService followService;
	private final KafkaTemplate<String, Object> kafkaTemplate;

	@PostMapping
	public ResponseEntity<ApiResponse<FollowCommandResult>> createFollow(
		@RequestBody CreateFollowRequest request,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		request.setFromUserId(principalDetails.getUser().getId());

		FollowCommandResult commandResult = followService.followUser(request.toCreateFollow());
		kafkaTemplate.send("follow", commandResult);
		return ResponseEntity.ok(ApiResponse.createSuccess(commandResult));
	}

	@DeleteMapping("/{followId}")
	public ResponseEntity<ApiResponse<DeleteFollowResponse>> unFollow(
		@PathVariable("followId") Long followId,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {

		UnFollowUserCommand unFollowUserCommand = UnFollowUserCommand.builder()
			.followId(followId)
			.fromUserId(principalDetails.getUser().getId())
			.build();

		followService.unFollowUser(unFollowUserCommand);

		DeleteFollowResponse deleteFollowResponse = DeleteFollowResponse.builder()
			.isSuccess(true)
			.build();

		return ResponseEntity.ok(ApiResponse.createSuccess(deleteFollowResponse));
	}

	@GetMapping("/followers")
	public ResponseEntity<ApiResponse<FindAllFollowerResponse>> getAllFollowers(
		@RequestBody FindAllFollowersRequest request
	) {
		List<Follow> followers = followService.findAllFollowers(request.getAllFollowers());
		FindAllFollowerResponse followersResponse = FindAllFollowerResponse.builder()
			.findAllResponseList(FindAllFollowerResponse.fromList(followers))
			.build();

		return ResponseEntity.ok(ApiResponse.createSuccess(followersResponse));
	}

	@GetMapping("/followings")
	public ResponseEntity<ApiResponse<FindAllFollowerResponse>> getAllFollowing(
		@RequestBody FindAllFollowingRequest request
	) {
		List<Follow> followings = followService.findAllFollowing(request.getAllFollowing());
		FindAllFollowerResponse followingResponse = FindAllFollowerResponse.builder()
			.findAllResponseList(FindAllFollowerResponse.fromList(followings))
			.build();
		return ResponseEntity.ok(ApiResponse.createSuccess(followingResponse));
	}

}
