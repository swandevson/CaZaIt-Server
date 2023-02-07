package shop.cazait.domain.cafe.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shop.cazait.domain.cafe.dto.GetCafeRes;
import shop.cazait.domain.cafe.dto.GetCafesRes;
import shop.cazait.domain.cafe.dto.PostCafeReq;
import shop.cazait.domain.cafe.dto.PostDistanceReq;
import shop.cazait.domain.cafe.exception.CafeException;
import shop.cazait.domain.cafe.service.CafeService;
import shop.cazait.domain.user.exception.UserException;
import shop.cazait.global.common.dto.response.SuccessResponse;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "카페 정보 API")
@RestController
@RequestMapping("/api/cafes")
@RequiredArgsConstructor
public class CafeController {

    private final CafeService cafeService;

    @PostMapping(value = "/add/master/{masterId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "카페 등록", notes = "master가 카페를 등록한다.")
    @ApiImplicitParam(name = "masterId", value = "마스터 ID")
    public SuccessResponse<String> addCafe(@PathVariable Long masterId,
                                           @RequestParam String json,
                                           @RequestPart(value = "cafeImages", required = false) List<MultipartFile> cafeImage) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        PostCafeReq postCafeReq = objectMapper.readValue(json, new TypeReference<>() {});
        cafeService.addCafe(masterId, postCafeReq, cafeImage);
        return new SuccessResponse<>("카페 등록 완료");

    }

    @GetMapping("/all/user/{userId}")
    @ApiOperation(value = "카페 전체 조회", notes = "ACTIVE한 카페를 조회한다.")
    @ApiImplicitParam(name = "userId", value = "유저 ID")
    public SuccessResponse<List<List<GetCafesRes>>> getCafeByStatus(@PathVariable Long userId,
                                                              @RequestBody PostDistanceReq distanceReq) throws CafeException {
        try {
            List<List<GetCafesRes>> cafeResList = cafeService.getCafeByStatus(userId, distanceReq);
            return new SuccessResponse<>(cafeResList);
        } catch (CafeException e) {
            throw new CafeException(e.getError());
        }
    }

    @GetMapping("/id/{cafeId}/user/{userId}")
    @ApiOperation(value = "카페 ID 조회", notes = "특정 ID의 카페를 조회한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "유저 ID"),
            @ApiImplicitParam(name = "cafeId", value = "카페 ID")
    })
    public SuccessResponse<GetCafeRes> getCafeById(@PathVariable Long userId,
                                                   @PathVariable Long cafeId) throws CafeException, UserException {
        try {
            GetCafeRes cafeRes = cafeService.getCafeById(userId, cafeId);
            return new SuccessResponse<>(cafeRes);
        } catch (CafeException e) {
            throw new CafeException(e.getError());
        }
    }

    @GetMapping("/name/{cafeName}/user/{userId}")
    @ApiOperation(value = "카페 이름 조회", notes = "특정 이름의 카페를 조회한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cafeName", value = "카페 이름"),
            @ApiImplicitParam(name = "userId", value = "유저 ID")
    })
    public SuccessResponse<List<List<GetCafesRes>>> getCafeByName(@PathVariable String cafeName,
                                                            @PathVariable Long userId,
                                                            @RequestBody PostDistanceReq distanceReq) throws CafeException {
        try {
            List<List<GetCafesRes>> cafeResList = cafeService.getCafeByName(cafeName, userId, distanceReq);
            return new SuccessResponse<>(cafeResList);
        } catch (CafeException e) {
            throw new CafeException(e.getError());
        }
    }

    @PostMapping("/update/{cafeId}/master/{masterId}")
    @ApiOperation(value = "카페 정보 수정", notes = "특정 ID의 카페 정보를 수정한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cafeId", value = "카페 ID"),
            @ApiImplicitParam(name = "masterId", value = "마스터 ID")
    })
    public SuccessResponse<String> updateCafe(@PathVariable Long cafeId,
                                              @PathVariable Long masterId,
                                              @RequestBody @Valid PostCafeReq cafeReq) throws CafeException, JsonProcessingException {
        try {
            cafeService.updateCafe(cafeId, masterId, cafeReq);
            return new SuccessResponse<>("카페 수정 완료");
        } catch (CafeException e) {
            throw new CafeException(e.getError());
        }

    }

    @PostMapping("/delete/{cafeId}/master/{masterId}")
    @ApiOperation(value = "카페 삭제 (상태 변경)", notes = "특정 ID의 카페 상태를 INACTIVE로 변경한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cafeId", value = "카페 ID"),
            @ApiImplicitParam(name = "masterId", value = "마스터 ID")
    })
    public SuccessResponse<String> deleteCafe(@PathVariable Long cafeId,
                                              @PathVariable Long masterId) throws CafeException {
        try {
            cafeService.deleteCafe(cafeId, masterId);
            return new SuccessResponse<>("카페 삭제 완료");
        } catch (CafeException e) {
            throw new CafeException(e.getError());
        }
    }
}
