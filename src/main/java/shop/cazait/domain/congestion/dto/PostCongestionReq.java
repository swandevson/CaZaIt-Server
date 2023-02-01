package shop.cazait.domain.congestion.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.cazait.domain.cafe.entity.Cafe;
import shop.cazait.domain.congestion.entity.Congestion;
import shop.cazait.domain.congestion.entity.CongestionStatus;

@ApiModel(value = "혼잡도 등록(수정) Request", description = "등록 및 수정할 혼잡도 정보")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCongestionReq {

    @ApiModelProperty(value = "혼잡도 상태", required = true, example = "FREE")
    @NotBlank(message = "혼잡도를 입력해주세요.")
    private String congestionStatus;

    public static Congestion toEntity(Cafe cafe, CongestionStatus congestionStatus) {
        return Congestion.builder()
                .cafe(cafe)
                .congestionStatus(congestionStatus)
                .build();
    }

}
