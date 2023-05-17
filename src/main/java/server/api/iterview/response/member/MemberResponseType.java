package server.api.iterview.response.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import server.api.iterview.response.BaseResponseType;

@Getter
@AllArgsConstructor
public enum MemberResponseType implements BaseResponseType {
    NO_AUTHORIZATION(4020, "권한이 없습니다.", HttpStatus.UNAUTHORIZED),
    NOT_FOUND_USER(4021,"사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_AUTHENTICATION(4022,"인증 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_USER(4023,"이미 존재하는 사용자입니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_PASSWORD(4024,"비밀번호를 입력해주세요",HttpStatus.BAD_REQUEST),
    LOGOUT_MEMBER(4025,"로그아웃된 사용자입니다.",HttpStatus.BAD_REQUEST),
    DUPLICATE_NICKNAME(4026, "이미 사용중인 닉네임입니다.", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD(4027,"비밀번호를 잘못 입력하였습니다.", HttpStatus.UNAUTHORIZED),
    SESSION_EXPIRED(4028,"세션이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    PROFILE_IMAGE_URL_SYNC_FAIL(4029, "데이터베이스에 사용자 프로필이미지 동기화 실패", HttpStatus.INTERNAL_SERVER_ERROR),
    PROFILE_IMAGE_DOES_NOT_EXIST(4030, "프로필 이미지가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    SIGNUP_FAILED(4031, "회원가입에 실패하였습니다", HttpStatus.INTERNAL_SERVER_ERROR),

    WITHDRAWAL_SUCCESS(2020, "회원 탈퇴 성공", HttpStatus.NO_CONTENT),
    LOGIN_SUCCESS(2021, "로그인 성공", HttpStatus.OK),
    MEMBER_INFO_GET_SUCCESS(2022, "사용자 정보 조회 성공", HttpStatus.OK),
    MEMBER_INFO_UPDATE_SUCCESS(2023, "사용자 정보 수정 성공", HttpStatus.OK),
    NOT_DUPLICATE_NICKNAME(2024, "사용 가능한 닉네임입니다.", HttpStatus.OK),
    PROFILE_IMAGE_URL_SYNC_SUCCESS(2025, "데이터베이스에 사용자 프로필이미지 동기화 성공", HttpStatus.OK),
    MEMBER_CATEGORY_UPDATE_SUCCESS(2026, "사용자 관심사 업데이트 성공", HttpStatus.OK),
    PROFILE_IMAGE_REMOVE_SUCCESS(2027, "사용자 프로필이미지 삭제 성공", HttpStatus.OK),
    PROFILE_IMAGE_GET_SUCCESS(2028, "사용자 프로필이미지 url 응답 성공", HttpStatus.OK),
    CONFER_ADMIN_ROLE_SUCCESS(2029, "관리자 권한 부여 성공", HttpStatus.OK),
    SIGNUP_SUCCESS(2030, "회원가입 성공", HttpStatus.NO_CONTENT),
    ;

    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;
}
