package shop.cazait.domain.cafemenu.service;

import static shop.cazait.domain.cafemenu.exception.CafeMenuErrorStatus.INVALID_MENU;
import static shop.cazait.domain.cafemenu.exception.CafeMenuErrorStatus.NOT_REGISTER_MENU;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.cazait.domain.cafe.entity.Cafe;
import shop.cazait.domain.cafemenu.dto.PostCafeMenuReq;
import shop.cazait.domain.cafemenu.dto.PostCafeMenuRes;
import shop.cazait.domain.cafemenu.dto.PutCafeMenuReq;
import shop.cazait.domain.cafemenu.dto.PutCafeMenuRes;
import shop.cazait.domain.cafemenu.dto.GetCafeMenuRes;
import shop.cazait.domain.cafemenu.entity.CafeMenu;
import shop.cazait.domain.cafemenu.exception.CafeMenuException;
import shop.cazait.domain.cafemenu.repository.CafeMenuRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class CafeMenuService {

    private final CafeRepository cafeRepository;
    private final CafeMenuRepository cafeMenuRepository;

    /**
     * 카페 메뉴 조회
     */
    @Transactional(readOnly = true)
    public List<GetCafeMenuRes> getCafeMenus(Long cafeId) {

        List<CafeMenu> findMenus;

        try {
            findMenus = cafeMenuRepository.findAllByCafeId(cafeId);
        } catch (CafeMenuException exception) {
            throw new CafeMenuException(NOT_REGISTER_MENU);
        }

        return GetCafeMenuRes.of(findMenus);

    }


    /**
     * 카페 메뉴 등록
     *
     */
    public List<PostCafeMenuRes> registerMenu(Long cafeId, List<PostCafeMenuReq> postCafeMenuReqs) {

        Cafe findCafe = cafeRepository.findById(cafeId);
        List<CafeMenu> menus = PostCafeMenuReq.toEntity(findCafe, postCafeMenuReqs);
        List<CafeMenu> addMenus = cafeMenuRepository.saveAll(menus);

        return PostCafeMenuRes.of(addMenus);

    }

    /**
     * 카페 메뉴 수정
     */
    public PutCafeMenuRes updateCafeMenu(Long cafeId, Long cafeMenuId, PutCafeMenuReq putCafeMenuReq) {

        CafeMenu findCafeMenu = cafeMenuRepository.findByMenuAndCafe(cafeMenuId, cafeId);

        if (putCafeMenuReq.getName() != null) {
            findCafeMenu.changeCafeMenuName(putCafeMenuReq.getName());
        }

        if (putCafeMenuReq.getPrice() != -1) {
            findCafeMenu.changeCafeMenuPrice(putCafeMenuReq.getPrice());
        }

        if (putCafeMenuReq.getImageUrl() != null) {
            findCafeMenu.changeCafeMenuImageUrl(putCafeMenuReq.getImageUrl());
        }

        CafeMenu updateCafeMenu = cafeMenuRepository.save(findCafeMenu);

        return PutCafeMenuRes.of(updateCafeMenu);

    }

    /**
     * 카페 메뉴 삭제
     */
    public void deleteCafeMenu(Long cafeMenuId) {

        try {
            cafeMenuRepository.deleteById(cafeMenuId);
        } catch (CafeMenuException exception) {
            throw new CafeMenuException(INVALID_MENU);
        }

    }

}
