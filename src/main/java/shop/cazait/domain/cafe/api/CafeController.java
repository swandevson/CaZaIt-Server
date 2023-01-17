package shop.cazait.domain.cafe.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shop.cazait.domain.cafe.dto.GetCafeRes;
import shop.cazait.domain.cafe.dto.PostCafeReq;
import shop.cazait.domain.cafe.service.CafeService;
import shop.cazait.global.common.response.BaseResponse;
import shop.cazait.global.common.status.BaseStatus;

import java.util.List;

@RestController
@RequestMapping("/api/cafes")
@RequiredArgsConstructor
public class CafeController {

    private final CafeService cafeService;

    @PostMapping("/add")
    public BaseResponse<String> addCafe(@RequestBody PostCafeReq cafeReq) {
        this.cafeService.addCafe(cafeReq);
        return new BaseResponse<>("카페 등록 완료");
    }

    @GetMapping("/all")
    public BaseResponse<List<GetCafeRes>> getCafeByStatus() {
        List<GetCafeRes> cafeResList = this.cafeService.getCafeByStatus(BaseStatus.ACTIVE);
        return new BaseResponse<>(cafeResList);
    }

    @GetMapping("/id/{cafeId}")
    public BaseResponse<GetCafeRes> getCafeById(@PathVariable Long cafeId) {
        GetCafeRes cafeRes = this.cafeService.getCafeById(cafeId);
        return new BaseResponse<>(cafeRes);
    }

    @GetMapping("/name/{cafeName}")
    public BaseResponse<List<GetCafeRes>> getCafeByName(@PathVariable String cafeName) {
        List<GetCafeRes> cafeResList = this.cafeService.getCafeByName(cafeName);
        return new BaseResponse<>(cafeResList);
    }

    @PostMapping("/update/{cafeId}")
    public BaseResponse<String> updateCafe(@PathVariable Long cafeId, @RequestBody PostCafeReq cafeReq) {
        this.cafeService.updateCafe(cafeId, cafeReq);
        return new BaseResponse<>("카페 수정 완료");
    }
}