// connect to User
import ApiUtil from "../ApiUtil";

// data
// {
//   userId: "danny1234@naver.com", // 아이디
//       userPassword: "abcdefg1234!", //비밀번호
//     userName: "강대니", //이름
//     userNickname: "대니", //닉네임
//     userBirth: "19980711" /*생년월일, 숫자 8자리*/,
//     userGender: "female" /*남:1, 여:2*/,
// };
// data

class UserApi {
  createUser = async (data) => {
    return await ApiUtil.post(`/users`, {
      email: data.email,
      password: data.password,
      gender: data.gender,
      username: data.username,
      nickname: data.nickname,
    });
  };

  deleteUser = async (data) => {
    return await ApiUtil.delete(
      `/users?password=${data.password}&username=${data.username}`,
    );
  };

  //토큰 사용
  getUser = async () => {
    // return {
    //   userId: "danny1234@naver.com", // 아이디
    //   userPassword: "abcdefg1234!", //비밀번호
    //   userName: "강대니", //이름
    //   userNickname: "대니", //닉네임
    //   userBirth: "19980711" /*생년월일, 숫자 8자리*/,
    //   userGender: "female" /*남:1, 여:2*/,
    // };
    let userIdFromToken = ApiUtil.getUserIdFromToken();
    return (await ApiUtil.get(`/users/${userIdFromToken}`)).data.data;
  };

  getUserById = async (userId) => {
    return (await ApiUtil.get(`/users/${userId}`)).data.data;
  }

  updateUserInfo = async (data) => {
    return await ApiUtil.put(`/users/update-info`, {
      userId: data.userId,
      userName: data.userName,
      nickname: data.nickname,
    });
  };

  // getUser = async (userId) => {
  //   return await ApiUtil.get(`/users/${userId}`);
  // };

  uploadProfileS3 = async (userId, profileImage) => {
    try {
      const formData = new FormData();
      formData.append(`userId`, userId);
      formData.append(`profile`, profileImage);
      console.log("=====formData====", formData);
      const response = await ApiUtil.formDataPost("/users/profile", formData);
      console.log("*******", response.data.data);
      return response.data.data;
    } catch (error) {
      console.error("사진 업로드에 실패했습니다.");
    }
  };

  uploadBio = async (userId, bio) => {
    return ApiUtil.post(`/users/bio`, {
      userId: userId,
      bio: bio,
    });
  };
}

const userApi = new UserApi();
export default userApi;
