package shop.cazait.domain.master.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import shop.cazait.domain.master.dto.get.GetMasterRes;
import shop.cazait.domain.master.dto.patch.PutMasterReq;
import shop.cazait.domain.master.dto.post.PostMasterReq;
import shop.cazait.domain.master.dto.post.PostMasterRes;
import shop.cazait.domain.master.error.MasterException;
import shop.cazait.domain.master.service.MasterService;
import shop.cazait.global.common.response.BaseResponse;
import shop.cazait.global.common.status.BaseStatus;

@RestController
@RequestMapping("api/Masters")
@RequiredArgsConstructor
public class MasterController {

	@Autowired
	private final MasterService masterService;

	@PostMapping
	@ApiOperation(value = "마스터 회원가입", notes = "마스터 사용자의 정보들을 이용해서 회원가입을 진행한다.")
	public BaseResponse<PostMasterRes> registerMaster(@Validated @RequestBody PostMasterReq dto) throws
		MasterException {
		PostMasterRes postCreateMasterRes = masterService.registerMaster(dto);
		return new BaseResponse<>(postCreateMasterRes);
	}

	@GetMapping("/all")
	@ApiOperation(value = "마스터 계정 전체 조회", notes = "ACTIVE한 마스터 계정을 조회한다.")
	public BaseResponse<List<GetMasterRes>> getMasterByStatus() throws MasterException {
		List<GetMasterRes> masterResList = this.masterService.getMasterByStatus(BaseStatus.ACTIVE);
		return new BaseResponse<>(masterResList);
	}

	@PutMapping("/update/{cafeId}")
	@ApiOperation(value = "마스터 정보 수정", notes = "특정 ID의 마스터 관련 정보를 수정한다.")
	public BaseResponse<String> updateMaster(@PathVariable Long masterId, @RequestBody PutMasterReq masterReq) {
		this.masterService.updateMaster(masterId, masterReq);
		return new BaseResponse<>("카페 수정 완료");
	}

	@DeleteMapping
	@ApiOperation(value = "마스터 계정 탈퇴(상태  변경)", notes = "특정 ID의 마스터 상태를 INACTIVE로 변경한다.")
	public BaseResponse<String> deleteMaster(@Validated @PathVariable int id) throws MasterException {
		masterService.removeMaster(id);
		String response = "회원 탈퇴가 성공하였습니다.";
		return new BaseResponse<>(response);
	}

}
