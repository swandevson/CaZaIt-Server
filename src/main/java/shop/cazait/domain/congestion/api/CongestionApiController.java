package shop.cazait.domain.congestion.api;

import static shop.cazait.global.error.status.ErrorStatus.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.cazait.domain.congestion.dto.PostCongestionReq;
import shop.cazait.domain.congestion.dto.PostCongestionRes;
import shop.cazait.domain.congestion.exception.CongestionException;
import shop.cazait.domain.congestion.service.CongestionService;
import shop.cazait.global.common.response.SuccessResponse;

@Api(tags = "혼잡도 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/congestions")
public class CongestionApiController {

    private final CongestionService congestionService;

    @ApiOperation(value = "혼잡도 등록 및 수정", notes = "마스터 ID, 카페 ID, 혼잡도 상태를 받아 혼잡도를 수정 또는 등록한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "masterId", value = "마스터 ID"),
            @ApiImplicitParam(name = "cafeId", value = "카페 ID")
    })
    @PostMapping("/master/{masterId}/cafe/{cafeId}")
    public SuccessResponse<PostCongestionRes> addCongestion(@PathVariable(name = "masterId") Long masterId,
                                                            @PathVariable(name = "cafeId") Long cafeId,
                                                            @RequestBody @Valid PostCongestionReq postCongestionReq)
            throws CongestionException {

        if (postCongestionReq.getCongestionStatus().isBlank()) {
            throw new CongestionException(CONGESTION_STATUS_EMPTY);
        }

        PostCongestionRes result = congestionService.addAndUpdateCongestion(cafeId, postCongestionReq);
        return new SuccessResponse<>(result);

    }

}
