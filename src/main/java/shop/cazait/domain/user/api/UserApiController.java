package shop.cazait.domain.user.api;

import static shop.cazait.global.error.status.SuccessStatus.CREATE_USER;
import static shop.cazait.global.error.status.SuccessStatus.SUCCESS;
import static shop.cazait.global.error.status.SuccessStatus.VALID_USER_INFO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import shop.cazait.domain.user.dto.*;
import shop.cazait.domain.user.exception.UserException;
import shop.cazait.domain.user.service.UserService;
import shop.cazait.global.common.dto.response.SuccessResponse;
import shop.cazait.global.config.encrypt.NoAuth;

@Tag(name = "유저 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserApiController {

    private final UserService userService;

    @NoAuth
    @Operation(summary = "회원 가입", description = "User 정보를 추가하여 회원가입을 진행")
    @PostMapping("/sign-up")
    public SuccessResponse<UserCreateOutDTO> createUser(@RequestBody @Valid UserCreateInDTO userCreateInDTO)
            throws UserException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        UserCreateOutDTO userCreateOutDTO = userService.createUser(userCreateInDTO);
        return new SuccessResponse<>(CREATE_USER, userCreateOutDTO);
    }

    @GetMapping("/all")
    @Operation(summary = "모든 회원을 조회", description = "회원가입된 모든 회원 정보를 조회")
    public SuccessResponse<List<UserFindOutDTO>> getUsers(){
        List<UserFindOutDTO> allUserRes = userService.getUsers();
        return new SuccessResponse<>(SUCCESS, allUserRes);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "특정 회원 정보를 조회", description ="자신의 계정 정보를 조회")
    @Parameter(name = "userId", description = "response로 발급 받은 계정 ID번호")
    public SuccessResponse<UserFindOutDTO> getUser(@PathVariable(name = "userId") UUID userIdx) throws UserException {
        UserFindOutDTO userInfoRes = userService.getUser(userIdx);
        return new SuccessResponse<>(SUCCESS, userInfoRes);
    }

    @PatchMapping("/{userId}")
    @Operation(summary="특정한 회원 정보를 수정", description = "자신의 계정 정보를 수정")
    @Parameter(name = "userId", description = "response로 발급 받은 계정 ID번호")
    public SuccessResponse<UserUpdateOutDTO> updateUserProfile(
            @PathVariable(name = "userId") UUID userIdx,
            @RequestBody @Valid UserUpdateInDTO userUpdateInDTO) throws UserException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

            UserUpdateOutDTO userUpdateOutDTO = userService.updateUserProfile(userIdx, userUpdateInDTO);
            return new SuccessResponse<>(SUCCESS, userUpdateOutDTO);

    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "특정한 회원 정보를 삭제", description = "자신의 계정 정보를 삭제")
    @Parameter(name = "userId", description = "response로 발급 받은 계정 ID번호")
    public SuccessResponse<UserDeleteOutDTO> deleteUserById(@PathVariable(name = "userId") UUID userIdx) throws UserException {
        UserDeleteOutDTO userDeleteOutDTO = userService.deleteUserById(userIdx);
        return new SuccessResponse<>(SUCCESS, userDeleteOutDTO);
    }

    @NoAuth
    @PostMapping ("/exist/accountname")
    @Operation(summary = "아이디 조회", description = "입력한 아이디를 통해 회원 DB를 통해 존재/존재하지 않음 여부를 조회")
    public SuccessResponse<String> findUserDuplicateAccountName(@RequestBody @Valid UserFindExistAccountNameInDTO userFindDuplicateEmailInDTO) throws UserException {
        SuccessResponse<String> accountNameSuccessResponse = userService.findUserDuplicateAccountName(userFindDuplicateEmailInDTO);
        return accountNameSuccessResponse;
    }

    @NoAuth
    @PostMapping ("/exist/nickname")
    @Operation(summary = "닉네임 조회", description = "입력한 비밀번호를 통해 회원 DB를 통해 존재/존재하지 않음 여부를 조회")
    public SuccessResponse<String> findUserDuplicateNickname(@RequestBody @Valid UserFindExistNicknameInDTO userFindExistNicknameInDTO) throws UserException {
        SuccessResponse<String> nicknameDuplicateSuccessResponse = userService.findUserDuplicateNickname(userFindExistNicknameInDTO);
        return nicknameDuplicateSuccessResponse;
    }

    @NoAuth
    @PostMapping ("/exist/phonenumber")
    @Operation(summary = "전화번호 조회", description = "입력한 전화번호를 통해 회원 DB를 통해 존재/존재하지 않음 여부를 조회")
    public SuccessResponse<String> findUserDuplicatePhoneNumber(@RequestBody @Valid UserFindExistPhonenumberInDTO userFindExistPhonenumberInDTO) throws UserException {
        SuccessResponse<String> phoneNumberDuplicateSuccessResponse = userService.findUserDuplicatePhoneNumber(userFindExistPhonenumberInDTO);
        return phoneNumberDuplicateSuccessResponse;
    }

    @NoAuth
    @PostMapping ("/find-accountname")
    @Operation(summary = "아이디 찾기", description = "문자 인증 완료 후 아이디 반환")
    public SuccessResponse<UserFindAccountNameOutDTO> findUserAccountName(@RequestBody UserFindAccountNameInDTO userFindAccountNameInDTO) throws UserException {
        UserFindAccountNameOutDTO userAccountName = userService.findUserAccountName(userFindAccountNameInDTO.getUserPhoneNumber());
        return new SuccessResponse<>(SUCCESS,userAccountName);
    }

    @NoAuth
    @PatchMapping("/reset-password/password")
    @Operation(summary = "비밀번호 변경 (새 비밀번호 입력)", description = "변경하려는 새로운 비밀번호를 입력")
    public SuccessResponse<UserUpdatePasswordInResetPasswordOutDTO> updateUserPasswordInResetPassword(@RequestBody UserUpdatePasswordInResetPasswordInDTO userUpdatePasswordInResetPasswordInDTO) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, UserException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        UserUpdatePasswordInResetPasswordOutDTO userUpdatePasswordInResetPasswordOutDTO = userService.updateUserPasswordInResetPassword(userUpdatePasswordInResetPasswordInDTO.getUserPhoneNumber(), userUpdatePasswordInResetPasswordInDTO.getPassword());
        return new SuccessResponse<>(SUCCESS, userUpdatePasswordInResetPasswordOutDTO);
    }

    @NoAuth
    @PostMapping("/reset-password/checkuserinfo")
    @Operation(summary = "비밀번호 변경 (유저정보 검증)", description = "아이디와 전화번호가 일치하는지")
    public SuccessResponse<UserVerifyUserInfoInResetPasswordOutDTO> verifyUserInfoInResetPassword(@RequestBody UserVerifyUserInfoInResetPasswordInDTO userVerifyUserInfoInResetPasswordInDTO) throws UserException {
        UserVerifyUserInfoInResetPasswordOutDTO userVerifyUserInfoInResetPasswordOutDTO = userService.verifyUserInfoInResetPassword(userVerifyUserInfoInResetPasswordInDTO);
        return new SuccessResponse<>(VALID_USER_INFO,userVerifyUserInfoInResetPasswordOutDTO);
    }
}


