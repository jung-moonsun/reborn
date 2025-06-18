export const kakaoLogin = () => {
  if (!window.Kakao.isInitialized()) {
    window.Kakao.init("8602e4e9cddf04af854ef3c196e7ed5a"); // 👉 여기에 REST API 키 넣어
  }

window.Kakao.Auth.authorize({
  redirectUri: "http://localhost:8080/login/oauth2/code/kakao",
  scope: "profile_nickname account_email"
});
};