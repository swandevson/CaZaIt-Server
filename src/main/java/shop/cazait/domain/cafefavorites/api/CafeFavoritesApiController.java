package shop.cazait.domain.cafefavorites.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.cazait.domain.cafefavorites.dto.PostCafeFavoritesRes;
import shop.cazait.domain.cafefavorites.service.CafeFavoritesService;
import shop.cazait.global.common.response.BaseResponse;

@Api(tags = "즐겨찾기 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cafe_favorites")
public class CafeFavoritesApiController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CafeFavoritesService cafeFavoritesService;

    @ApiOperation(value = "즐겨찾기 등록", notes = "사용자 ID와 카페 ID를 받아 즐겨찾기 등록")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "사용자 ID"),
            @ApiImplicitParam(name = "cafeId", value = "카페 ID")
    })
    @PostMapping("/user/{userId}/favorites/{cafeId}")
    public BaseResponse<PostCafeFavoritesRes> addCafeFavorites(@PathVariable(name = "userId") Long userId,
                                                              @PathVariable(name = "cafeId") Long cafeId) {

        PostCafeFavoritesRes postCafeFavoritesRes = cafeFavoritesService.addCafeFavorites(userId, cafeId);
        return new BaseResponse<>(postCafeFavoritesRes);

    }



}
