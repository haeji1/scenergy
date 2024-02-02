package com.wbm.scenergyspring.domain.post.jobPost.service;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.wbm.scenergyspring.domain.post.jobPost.controller.request.UpdateJobPostRequest;
import com.wbm.scenergyspring.domain.post.jobPost.controller.response.GetJobPostCommandResponse;
import com.wbm.scenergyspring.domain.post.jobPost.entity.IsActive;
import com.wbm.scenergyspring.domain.post.jobPost.entity.JobPost;
import com.wbm.scenergyspring.domain.post.jobPost.entity.JobPostGenreTag;
import com.wbm.scenergyspring.domain.post.jobPost.entity.JobPostInstrumentTag;
import com.wbm.scenergyspring.domain.post.jobPost.entity.JobPostLocationTag;
import com.wbm.scenergyspring.domain.post.jobPost.repository.JobPostRepository;
import com.wbm.scenergyspring.domain.post.jobPost.service.Command.CreateJobPostCommand;
import com.wbm.scenergyspring.domain.post.jobPost.service.Command.DeleteJobPostCommand;
import com.wbm.scenergyspring.domain.post.jobPost.service.Command.GetJobPostCommand;
import com.wbm.scenergyspring.domain.user.entity.User;
import com.wbm.scenergyspring.domain.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
@Transactional
class JobPostServiceTest {

	@Autowired
	JobPostService jobPostService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	JobPostRepository jobPostRepository;

	@Test
	@DisplayName("게시글 추가")
	void createJobPost() {

		// given
		User user = User.createNewUser(
			"aaa@naver.com",
			"aaaaa",
			"aaaaa"
		);
		User saveUser = userRepository.save(user);

		JobPost newJobPost = JobPost.createNewJobPost(
			user,
			"aaa",
			"aaaa",
			LocalDateTime.parse("2024-02-01T10:00:00"),
			4L,
			0L,
			IsActive.active
		);

		List<Long> genreTags = Arrays.asList(1L, 2L, 3L);
		List<Long> instrumentTags = Arrays.asList(1L, 2L, 3L);
		List<Long> locationTags = Arrays.asList(1L, 2L, 3L);

		CreateJobPostCommand command = CreateJobPostCommand.builder()
			.userId(saveUser.getId())
			.title(newJobPost.getTitle())
			.content(newJobPost.getContent())
			.expirationDate(newJobPost.getExpirationDate())
			.peopleRecruited(newJobPost.getPeopleRecruited())
			.bookMark(newJobPost.getBookMark())
			.isActive(newJobPost.getIsActive())
			.locationTagIds(locationTags)
			.genreTagIds(genreTags)
			.instrumentTagIds(instrumentTags)
			.build();

		// when
		Long id = jobPostService.createJobPost(command);

		// then
		JobPost findJobPost = jobPostRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("없는 게시글"));

		Assertions.assertThat(id).isEqualTo(findJobPost.getId());

		List<JobPostGenreTag> genreTagsInJobPost = findJobPost.getJobPostGenreTags();
		Assertions.assertThat(genreTagsInJobPost).isNotNull();;
		Assertions.assertThat(3).isEqualTo(genreTagsInJobPost.size());

		List<JobPostInstrumentTag> instrumentTagsInJobPost  = findJobPost.getJobPostInstrumentTags();
		Assertions.assertThat(instrumentTagsInJobPost).isNotNull();;
		Assertions.assertThat(3).isEqualTo(instrumentTagsInJobPost.size());

		List<JobPostLocationTag> locationTagInJobPost  = findJobPost.getJobPostLocationTags();
		Assertions.assertThat(locationTagInJobPost).isNotNull();;
		Assertions.assertThat(3).isEqualTo(locationTagInJobPost.size());

		jobPostRepository.deleteById(id);
	}

	@Test
	@DisplayName("게시글 수정")
	void updateJobPost() {
		// given
		User user = User.createNewUser(
			"aaa@naver.com",
			"aaaaa",
			"aaaaa"
		);

		User saveUser = userRepository.save(user);

		JobPost newJobPost = JobPost.createNewJobPost(
			user,
			"aaa",
			"aaaa",
			LocalDateTime.parse("2024-02-01T10:00:00"),
			4L,
			0L,
			IsActive.active
		);

		List<Long> genreTags = Arrays.asList(1L, 2L, 3L);
		List<Long> instrumentTags = Arrays.asList(1L, 2L, 3L);
		List<Long> locationTags = Arrays.asList(1L, 2L, 3L);

		CreateJobPostCommand createCommand = CreateJobPostCommand.builder()
			.userId(saveUser.getId())
			.title(newJobPost.getTitle())
			.content(newJobPost.getContent())
			.expirationDate(newJobPost.getExpirationDate())
			.peopleRecruited(newJobPost.getPeopleRecruited())
			.bookMark(newJobPost.getBookMark())
			.isActive(newJobPost.getIsActive())
			.locationTagIds(locationTags)
			.genreTagIds(genreTags)
			.instrumentTagIds(instrumentTags)
			.build();

		Long id = jobPostService.createJobPost(createCommand);

		UpdateJobPostRequest updateRequest = UpdateJobPostRequest.builder()
			.title("bbb")
			.content("bbb")
			.expirationDate(LocalDateTime.parse("2024-02-02T10:00:00"))
			.peopleRecruited(5L)
			.bookMark(1L)
			.isActive(IsActive.active)
			.locationTags(Arrays.asList(1L, 2L))
			.genreTags(Arrays.asList(1L))
			.instrumentTags(Arrays.asList(2L))
			.build();

		// when
		boolean result = jobPostService.updateJobPost(id, updateRequest);

		// then
		Assertions.assertThat(result).isTrue();

		JobPost updatedJobPost = jobPostRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("없는 게시글"));

		Assertions.assertThat(updatedJobPost.getTitle()).isEqualTo("bbb");
		Assertions.assertThat(updatedJobPost.getContent()).isEqualTo("bbb");
		Assertions.assertThat(updatedJobPost.getExpirationDate()).isEqualTo(LocalDateTime.parse("2024-02-02T10:00:00"));
		Assertions.assertThat(updatedJobPost.getPeopleRecruited()).isEqualTo(5L);
		Assertions.assertThat(updatedJobPost.getBookMark()).isEqualTo(1L);
		Assertions.assertThat(updatedJobPost.getIsActive()).isEqualTo(IsActive.active);

		List<JobPostGenreTag> updatedGenreTagsInJobPost = updatedJobPost.getJobPostGenreTags();
		Assertions.assertThat(updatedGenreTagsInJobPost).isNotNull();
		Assertions.assertThat(updatedGenreTagsInJobPost).hasSize(1);

		List<JobPostInstrumentTag> updatedInstrumentTagsInJobPost = updatedJobPost.getJobPostInstrumentTags();
		Assertions.assertThat(updatedInstrumentTagsInJobPost).isNotNull();
		Assertions.assertThat(updatedInstrumentTagsInJobPost).hasSize(1);

		List<JobPostLocationTag> updatedLocationTagsInJobPost = updatedJobPost.getJobPostLocationTags();
		Assertions.assertThat(updatedLocationTagsInJobPost).isNotNull();
		Assertions.assertThat(updatedLocationTagsInJobPost).hasSize(2);

		jobPostRepository.deleteById(id);
	}

	@Test
	@DisplayName("게시글 삭제")
	void deleteJobPost() {

		long now = jobPostRepository.count();

		// given
		User user = User.createNewUser(
			"aaa@naver.com",
			"aaaaa",
			"aaaa"
		);

		User saveUser = userRepository.save(user);

		JobPost newJobPost = JobPost.createNewJobPost(
			user,
			"aaa",
			"aaaa",
			LocalDateTime.parse("2024-02-01T10:00:00"),
			4L,
			0L,
			IsActive.active
		);

		List<Long> genreTags = Arrays.asList(1L, 2L, 3L);
		List<Long> instrumentTags = Arrays.asList(1L, 2L);
		List<Long> locationTags = Arrays.asList(2L, 3L);

		CreateJobPostCommand command1 = CreateJobPostCommand.builder()
			.userId(saveUser.getId())
			.title(newJobPost.getTitle())
			.content(newJobPost.getContent())
			.expirationDate(newJobPost.getExpirationDate())
			.peopleRecruited(newJobPost.getPeopleRecruited())
			.bookMark(newJobPost.getBookMark())
			.isActive(newJobPost.getIsActive())
			.locationTagIds(locationTags)
			.genreTagIds(genreTags)
			.instrumentTagIds(instrumentTags)
			.build();

		Long id = jobPostService.createJobPost(command1);
		System.out.println(id);
		// when
		DeleteJobPostCommand command2 = new DeleteJobPostCommand();
		command2.setJobPostId(id);
		jobPostService.deleteJobPost(command2);

		// then
		Assertions.assertThat(now).isEqualTo(jobPostRepository.count());
	}

	@Test
	@DisplayName("상세 게시글 조회")
	void getJobPost() {
		// given
		GetJobPostCommand command = new GetJobPostCommand();
		command.setJobPostId(3L);
		GetJobPostCommandResponse response = jobPostService.getJobPost(command);

		// when
		JobPost findJob = jobPostRepository.findById(3L)
			.orElseThrow(() -> new EntityNotFoundException("없는 게시글"));

		// then
		Assertions.assertThat(response.getTitle()).isEqualTo(findJob.getTitle());
	}

	@Test
	@DisplayName("전체 게시글 조회")
	void getAllJobPostList() {
		List<GetJobPostCommandResponse> result = jobPostService.getAllJobPostList();
		int resultSize = result.size();
		Assertions.assertThat(resultSize).isEqualTo(jobPostRepository.count());
	}
}