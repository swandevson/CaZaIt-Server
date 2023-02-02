package shop.cazait.domain.cafe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.cazait.domain.cafe.dto.*;
import shop.cazait.domain.cafe.entity.Cafe;
import shop.cazait.domain.cafe.entity.Coordinate;
import shop.cazait.domain.cafe.exception.CafeException;
import shop.cazait.domain.cafe.repository.CafeRepository;
import shop.cazait.domain.checklog.service.CheckLogService;
import shop.cazait.domain.congestion.entity.Congestion;
import shop.cazait.domain.congestion.entity.CongestionStatus;
import shop.cazait.domain.favorites.entity.Favorites;
import shop.cazait.domain.favorites.repository.FavoritesRepository;
import shop.cazait.domain.master.entity.Master;
import shop.cazait.domain.master.repository.MasterRepository;
import shop.cazait.domain.user.exception.UserException;
import shop.cazait.global.common.status.BaseStatus;
import shop.cazait.global.error.status.ErrorStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CafeService {

    private final CoordinateService coordinateService;
    private final CafeRepository cafeRepository;
    private final MasterRepository masterRepository;
    private final CheckLogService checkLogService;
    private final FavoritesRepository favoritesRepository;

    public void addCafe(Long masterId, PostCafeReq cafeReq) throws JsonProcessingException {

        CoordinateVO coordinateVO = coordinateService.getCoordinateFromAddress(cafeReq.getAddress());
        Coordinate coordinate = Coordinate.builder()
                .longitude(coordinateVO.getDocuments().get(0).getLongitude())
                .latitude(coordinateVO.getDocuments().get(0).getLatitude())
                .build();

        Cafe cafe = Cafe.builder()
                .name(cafeReq.getName())
                .address(cafeReq.getAddress())
                .coordinate(coordinate)
                .build();

        Congestion tmp = Congestion.builder()
                .cafe(cafe)
                .congestionStatus(CongestionStatus.NONE)
                .build();
        cafe.initCongestion(tmp);

        cafeRepository.save(cafe);

        Master master = masterRepository.findById(masterId).orElseThrow(() -> new CafeException(ErrorStatus.NOT_EXIST_MASTER));
        master.setCafe(cafe);
    }

    @Transactional(readOnly = true)
    public List<GetCafesRes> getCafeByStatus(Long userId, PostDistanceReq distanceReq) throws CafeException {
        List<Cafe> cafeList = cafeRepository.findByStatus(BaseStatus.ACTIVE);
        if (cafeList.size() == 0) {
            throw new CafeException(ErrorStatus.NOT_EXIST_CAFE);
        }
        List<GetCafesRes> getCafesRes = readCafeList(userId, cafeList, distanceReq);
        getCafesRes = sortCafeList(getCafesRes, distanceReq);
        return getCafesRes;
    }

    @Transactional(readOnly = true)
    public GetCafeRes getCafeById(Long userId, Long cafeId) throws CafeException, UserException {

        Cafe cafe = cafeRepository.findById(cafeId).orElseThrow(() -> new CafeException(ErrorStatus.INVALID_CAFE_ID));
        String logResult = checkLogService.addVisitLog(userId, cafeId);    // 최근 본 카페 등록
        return GetCafeRes.of(cafe, logResult);
    }

    @Transactional(readOnly = true)
    public List<GetCafesRes> getCafeByName(String name, Long userId, PostDistanceReq distanceReq) throws CafeException {
        List<Cafe> cafeList = cafeRepository.findByNameContainingIgnoreCase(name);
        if (cafeList.size() == 0) {
            throw new CafeException(ErrorStatus.INVALID_CAFE_NAME);
        }
        cafeList.removeIf(cafe -> cafe.getStatus() == BaseStatus.INACTIVE);
        List<GetCafesRes> getCafesRes = readCafeList(userId, cafeList, distanceReq);
        getCafesRes = sortCafeList(getCafesRes, distanceReq);
        return getCafesRes;
    }

    public void updateCafe(Long cafeId, Long masterId, PostCafeReq cafeReq) throws CafeException, JsonProcessingException {

        CoordinateVO coordinateVO = coordinateService.getCoordinateFromAddress(cafeReq.getAddress());
        Coordinate coordinate = Coordinate.builder()
                .longitude(coordinateVO.getDocuments().get(0).getLongitude())
                .latitude(coordinateVO.getDocuments().get(0).getLatitude())
                .build();

        Cafe cafe = cafeRepository.findById(cafeId).orElseThrow(() -> new CafeException(ErrorStatus.INVALID_CAFE_ID));
        Master master = masterRepository.findById(masterId).orElseThrow(() -> new CafeException(ErrorStatus.NOT_EXIST_MASTER));
        if (!(master.getCafe().getId().equals(cafe.getId()))) {
            throw new CafeException(ErrorStatus.NOT_OPERATE_CAFE);
        }
        cafe.changeInfo(cafeReq, coordinate);
        cafeRepository.save(cafe);
    }

    public void deleteCafe(Long cafeId, Long masterId) throws CafeException {
        Cafe cafe = cafeRepository.findById(cafeId).orElseThrow(() -> new CafeException(ErrorStatus.INVALID_CAFE_ID));
        Master master = masterRepository.findById(masterId).orElseThrow(() -> new CafeException(ErrorStatus.NOT_EXIST_MASTER));
        if (!(master.getCafe().getId().equals(cafe.getId()))) {
            throw new CafeException(ErrorStatus.NOT_OPERATE_CAFE);
        }
        cafe.changeCafeStatus(BaseStatus.INACTIVE);
        cafeRepository.save(cafe);
    }

    private List<GetCafesRes> readCafeList(Long userId, List<Cafe> cafeList, PostDistanceReq distanceReq) {
        List<Favorites> favoritesList = favoritesRepository.findAllByUserId(userId).get();
        List<GetCafesRes> cafeResList = new ArrayList<>();
        for (Cafe cafe : cafeList) {
            boolean favorite = false;
            for (Favorites favorites : favoritesList) {
                if (cafe.getId().equals(favorites.getCafe().getId())) {
                    favorite = true;
                    break;
                }
            }
            int distance = DistanceService.distance(cafe.getCoordinate().getLatitude(), cafe.getCoordinate().getLongitude(),
                    distanceReq.getLatitude(), distanceReq.getLongitude());

            GetCafesRes cafeRes = GetCafesRes.of(cafe, distance, favorite);
            cafeResList.add(cafeRes);
        }
        return cafeResList;
    }

    private List<GetCafesRes> sortCafeList(List<GetCafesRes> getCafesRes, PostDistanceReq distanceReq) {
        String sort = distanceReq.getSort();
        if (sort.equals("distance")) {
            getCafesRes.sort((c1, c2) -> c2.getDistance() - c1.getDistance());
            Collections.reverse(getCafesRes);
        }
        else {
            getCafesRes.sort((c1, c2) -> c2.getCongestionStatus().getLevel() - c1.getCongestionStatus().getLevel());
        }

        int limit = Integer.parseInt(distanceReq.getLimit());
        if (limit != 0) {
            getCafesRes.removeIf(cafesRes -> cafesRes.getDistance() > limit);
        }
        return getCafesRes;
    }

}
