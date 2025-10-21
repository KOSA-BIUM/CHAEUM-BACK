package com.bium.chaeum.application.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bium.chaeum.application.request.CalendarRequest;
import com.bium.chaeum.application.request.MealCardRequest;
import com.bium.chaeum.application.request.MealCardWithItemsRequest;
import com.bium.chaeum.application.request.MealItemCreateRequest;
import com.bium.chaeum.application.response.MealCardResponse;
import com.bium.chaeum.domain.model.entity.MealCard;
import com.bium.chaeum.domain.model.entity.MealCardType;
import com.bium.chaeum.domain.model.entity.MealItem;
import com.bium.chaeum.domain.model.repository.MealCardRepository;
import com.bium.chaeum.domain.model.repository.MealItemRepository;
import com.bium.chaeum.domain.model.vo.CalendarId;
import com.bium.chaeum.domain.model.vo.MealCardId;
import com.bium.chaeum.domain.shared.error.DomainException;

import lombok.RequiredArgsConstructor;

// MealCardAppService는 식사 기록(MealCard) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다. (author: 나규태 + ChatGPT)
@Service
@RequiredArgsConstructor
public class MealCardAppService {

    private final MealCardRepository mealCardRepository;
    private final MealItemRepository mealItemRepository;
    private final CalendarAppService calendarAppService;

    // 특정 mealCardId를 사용한 조회
    @Transactional(readOnly = true)
    public Optional<MealCardResponse> getByMealCardId(String mealCardId) {
        if (mealCardId == null || mealCardId.isBlank()) throw new IllegalArgumentException("mealCardId is required");
        return mealCardRepository.findByMealCardId(MealCardId.of(mealCardId)).map(MealCardResponse::from);
    }

    // 특정 캘린더 아이디를 사용한 목록 조회
    @Transactional(readOnly = true)
    public List<MealCardResponse> listByCalendarId(String calendarId) {
        if (calendarId == null || calendarId.isBlank()) throw new IllegalArgumentException("calendarId is required");
        return mealCardRepository.findListByCalendarId(CalendarId.of(calendarId)).stream().map(MealCardResponse::from).toList();
    }

    // 특정 사용자 아이디 및 연월을 사용한 목록 조회
    @Transactional(readOnly = true)
    public List<MealCardResponse> listByUserIdAndYearMonth(String userId, String yearMonth) {
        if (userId == null || userId.isBlank()) throw new IllegalArgumentException("userId is required");
        if (yearMonth == null || yearMonth.isBlank()) throw new IllegalArgumentException("yearMonth is required");
        var calOpt = calendarAppService.getByUserIdAndYearMonth(userId, yearMonth);
        return calOpt.map(c -> c.getMealCards() == null ? List.<MealCardResponse>of() : c.getMealCards())
                     .orElse(List.of());
    }

    // 특정 캘린더 아이디, 기록 일시, 구분을 사용한 조회
    @Transactional(readOnly = true)
    public Optional<MealCardResponse> getByCalendarIdAndRecordDateAndDivision(String calendarId, LocalDateTime recordDate, String division) {
        if (calendarId == null || calendarId.isBlank()) throw new IllegalArgumentException("calendarId is required");
        if (recordDate == null) throw new IllegalArgumentException("recordDate is required");
        if (division == null || division.isBlank()) throw new IllegalArgumentException("division is required");
        return mealCardRepository.findByCalendarIdAndRecordDateAndDivision(CalendarId.of(calendarId), recordDate, division).map(MealCardResponse::from);
    }

    // 상세 조회 (아이템 포함)
    @Transactional(readOnly = true)
    public Optional<MealCardResponse> getDetailByMealCardId(String mealCardId) {
        if (mealCardId == null || mealCardId.isBlank()) throw new IllegalArgumentException("mealCardId is required");
        var base = mealCardRepository.findByMealCardId(MealCardId.of(mealCardId));
        if (base.isEmpty()) return Optional.empty();
        var mc = base.get();
        var withItems = mealCardRepository.findByCalendarIdAndRecordDateAndDivision(mc.getCalendarId(), mc.getRecordDate(), mc.getDivision().name());
        return withItems.map(MealCardResponse::from).or(()->base.map(MealCardResponse::from));
    }

    //  기본 생성 (아이템 없음)
    @Transactional
    public MealCardResponse create(String userId, String yearMonth, MealCardRequest request) {
        if (userId == null || userId.isBlank()) throw new IllegalArgumentException("userId is required");
        if (yearMonth == null || yearMonth.isBlank()) throw new IllegalArgumentException("yearMonth is required");
        if (request == null) throw new IllegalArgumentException("request is required");
        if (request.getRecordDate() == null) throw new IllegalArgumentException("recordDate is required");
        if (request.getDivision() == null || request.getDivision().isBlank()) throw new IllegalArgumentException("division is required");

        // 기존 캘린더가 존재 하는지 확인 및 생성
        var calRes = calendarAppService.ensureExists(CalendarRequest.builder().userId(userId).yearMonth(yearMonth).build());
        String calendarId = calRes.getCalendarId();

        // 중복 방지
        Optional<MealCard> dup = mealCardRepository.findByCalendarIdAndRecordDateAndDivision(CalendarId.of(calendarId), request.getRecordDate(), request.getDivision());
        if (dup.isPresent()) {
            throw new DomainException("MealCard already exists for recordDate=" + request.getRecordDate() + ", division=" + request.getDivision());
        }

        // mealCard 생성
        MealCard created = MealCard.create(
            CalendarId.of(calendarId),
            request.getRecordDate(),
            MealCardType.valueOf(request.getDivision()),
            List.of()
        );
        mealCardRepository.save(created);
        return MealCardResponse.from(created);
    }
    
    // 캘린더 아이디를 사용한 생성 (아이템 포함)
    @Transactional
    public MealCardResponse createWithItemsByCalendarId(String calendarId, MealCardWithItemsRequest request) {
        if (calendarId == null || calendarId.isBlank()) throw new IllegalArgumentException("calendarId is required");
        if (request == null) throw new IllegalArgumentException("request is required");
        if (request.getRecordDate() == null) throw new IllegalArgumentException("recordDate is required");
        if (request.getDivision() == null || request.getDivision().isBlank()) throw new IllegalArgumentException("division is required");

        // 캘린더 존재 확인
        var calOpt = calendarAppService.getByCalendarId(calendarId);
        if (calOpt.isEmpty()) {
            throw new DomainException("Calendar not found: " + calendarId);
        }

        // 중복 방지
        var dup = mealCardRepository.findByCalendarIdAndRecordDateAndDivision(CalendarId.of(calendarId), request.getRecordDate(), request.getDivision());
        if (dup.isPresent()) {
            throw new DomainException("MealCard already exists for recordDate=" + request.getRecordDate() + ", division=" + request.getDivision());
        }

        // 기본 mealCard 생성
        MealCard created = MealCard.create(
            CalendarId.of(calendarId),
            request.getRecordDate(),
            MealCardType.valueOf(request.getDivision()),
            List.of()
        );
        mealCardRepository.save(created);

        // 아이템 생성 (있을 경우)
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (MealItemCreateRequest itemReq : request.getItems()) {
                if (itemReq == null) continue;
                if (itemReq.getName() == null || itemReq.getName().isBlank()) continue;
                MealItem item = MealItem.create(
                    created.getId(),
                    itemReq.getName(),
                    itemReq.getIngredient(),
                    toPrimitive(itemReq.getCarbohydrate()),
                    toPrimitive(itemReq.getProtein()),
                    toPrimitive(itemReq.getFat()),
                    toPrimitive(itemReq.getSodium()),
                    toPrimitive(itemReq.getCalorie())
                );
                mealItemRepository.save(item);
            }
        }

        // 최종 생성된 mealCard (아이템 포함) 조회 및 반환
        var withItems = mealCardRepository.findByCalendarIdAndRecordDateAndDivision(CalendarId.of(calendarId), request.getRecordDate(), request.getDivision())
            .orElse(created);
        return MealCardResponse.from(withItems);
    }

    // 캘린더 아이디를 사용하지 않은 생성 (아이템 포함)
    @Transactional
    public MealCardResponse createWithItems(String userId, String yearMonth, MealCardWithItemsRequest request) {
        if (userId == null || userId.isBlank()) throw new IllegalArgumentException("userId is required");
        if (yearMonth == null || yearMonth.isBlank()) throw new IllegalArgumentException("yearMonth is required");
        if (request == null) throw new IllegalArgumentException("request is required");
        if (request.getRecordDate() == null) throw new IllegalArgumentException("recordDate is required");
        if (request.getDivision() == null || request.getDivision().isBlank()) throw new IllegalArgumentException("division is required");

        // 기존 캘린더 존재 확인 및 생성
        var calRes = calendarAppService.ensureExists(CalendarRequest.builder().userId(userId).yearMonth(yearMonth).build());
        String calendarId = calRes.getCalendarId();

        // 중복 방지
        var dup = mealCardRepository.findByCalendarIdAndRecordDateAndDivision(CalendarId.of(calendarId), request.getRecordDate(), request.getDivision());
        if (dup.isPresent()) {
            throw new DomainException("MealCard already exists for recordDate=" + request.getRecordDate() + ", division=" + request.getDivision());
        }

        // 기본 mealCard 생성
        MealCard created = MealCard.create(
            CalendarId.of(calendarId),
            request.getRecordDate(),
            MealCardType.valueOf(request.getDivision()),
            List.of()
        );
        mealCardRepository.save(created);

        // 아이템 생성 (있을 경우)
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (MealItemCreateRequest itemReq : request.getItems()) {
                if (itemReq == null) continue;
                if (itemReq.getName() == null || itemReq.getName().isBlank()) continue;
                MealItem item = MealItem.create(
                    created.getId(),
                    itemReq.getName(),
                    itemReq.getIngredient(),
                    toPrimitive(itemReq.getCarbohydrate()),
                    toPrimitive(itemReq.getProtein()),
                    toPrimitive(itemReq.getFat()),
                    toPrimitive(itemReq.getSodium()),
                    toPrimitive(itemReq.getCalorie())
                );
                mealItemRepository.save(item);
            }
        }

        // 최종 생성된 mealCard (아이템 포함) 조회 및 반환
        var withItems = mealCardRepository.findByCalendarIdAndRecordDateAndDivision(CalendarId.of(calendarId), request.getRecordDate(), request.getDivision())
            .orElse(created);
        return MealCardResponse.from(withItems);
    }

    // 단순 업데이트 (아이템 미포함)
    @Transactional
    public MealCardResponse update(String mealCardId, MealCardRequest request) {
        if (mealCardId == null || mealCardId.isBlank()) throw new IllegalArgumentException("mealCardId is required");
        if (request == null) throw new IllegalArgumentException("request is required");

        // 기존 mealCard 조회
        MealCard existing = mealCardRepository.findByMealCardId(MealCardId.of(mealCardId))
                .orElseThrow(() -> new DomainException("MealCard not found: " + mealCardId));

        // 업데이트할 필드 계산 (null이면 기존 값 유지)
        String newDivision = request.getDivision() != null ? request.getDivision() : existing.getDivision().name();
        LocalDateTime newRecordDate = request.getRecordDate() != null ? request.getRecordDate() : existing.getRecordDate();
        String newCalendarId = request.getCalendarId() != null ? request.getCalendarId() : existing.getCalendarId().value();

        MealCard updated = MealCard.reconstruct(
            existing.getId(),
            CalendarId.of(newCalendarId),
            newRecordDate,
            MealCardType.valueOf(newDivision),
            existing.getMealItems()
        );
        mealCardRepository.save(updated);
        return MealCardResponse.from(updated);
    }

    // 삭제
    @Transactional
    public void delete(String mealCardId) {
        if (mealCardId == null || mealCardId.isBlank()) throw new IllegalArgumentException("mealCardId is required");
        mealCardRepository.delete(MealCardId.of(mealCardId));
    }

    // null 안전 처리를 위한 헬퍼 메서드
    private Integer toPrimitive(Integer v) { return v == null ? 0 : v; }

    // 복합 업데이트: meal card + items (아이템 전체 교체)
    @Transactional
    public MealCardResponse updateWithItems(String mealCardId, MealCardWithItemsRequest request) {
        if (mealCardId == null || mealCardId.isBlank()) throw new IllegalArgumentException("mealCardId is required");
        if (request == null) throw new IllegalArgumentException("request is required");

        // 기존 mealCard 조회
        MealCard existing = mealCardRepository.findByMealCardId(MealCardId.of(mealCardId))
            .orElseThrow(() -> new DomainException("MealCard not found: " + mealCardId));

        // 삭제 및 생성에 대비한 캘린더 아이디 결정
        String targetCalendarId = existing.getCalendarId().value();
        if (request.getUserId() != null && !request.getUserId().isBlank()
                && request.getYearMonth() != null && !request.getYearMonth().isBlank()) {
            var cal = calendarAppService.ensureExists(CalendarRequest.builder().userId(request.getUserId()).yearMonth(request.getYearMonth()).build());
            targetCalendarId = cal.getCalendarId();
        }

        // 업데이트할 필드 계산 (null이면 기존 값 유지)
        LocalDateTime newRecordDate = request.getRecordDate() != null ? request.getRecordDate() : existing.getRecordDate();
        String newDivision = request.getDivision() != null && !request.getDivision().isBlank() ? request.getDivision() : existing.getDivision().name();

        // 중복 방지
        var conflict = mealCardRepository.findByCalendarIdAndRecordDateAndDivision(CalendarId.of(targetCalendarId), newRecordDate, newDivision)
            .filter(mc -> !mc.getId().value().equals(existing.getId().value()));
        if (conflict.isPresent()) {
            throw new DomainException("MealCard already exists for recordDate=" + newRecordDate + ", division=" + newDivision);
        }

        // mealCard 업데이트
        MealCard updated = MealCard.reconstruct(
            existing.getId(),
            CalendarId.of(targetCalendarId),
            newRecordDate,
            MealCardType.valueOf(newDivision),
            existing.getMealItems() // items will be replaced below
        );
        mealCardRepository.save(updated);

        // 아이템 전체 교체 처리
        if (request.getItems() != null) {
            // Load current items
            var currentItems = mealItemRepository.findByMealCardId(updated.getId());
            var currentById = currentItems.stream().collect(Collectors.toMap(i -> i.getId().value(), i -> i));
            
            Set<String> seen = new HashSet<>();

            // mealItems 처리 (생성, 수정)
            for (MealItemCreateRequest ir : request.getItems()) {
                if (ir == null) continue;
                if (ir.getMealItemId() == null || ir.getMealItemId().isBlank()) {
                    // create
                    MealItem createdItem = MealItem.create(
                        updated.getId(),
                        ir.getName(),
                        ir.getIngredient(),
                        toPrimitive(ir.getCarbohydrate()),
                        toPrimitive(ir.getProtein()),
                        toPrimitive(ir.getFat()),
                        toPrimitive(ir.getSodium()),
                        toPrimitive(ir.getCalorie())
                    );
                    mealItemRepository.save(createdItem);
                } else {
                    // update/merge
                    seen.add(ir.getMealItemId());
                    var existingItem = currentById.get(ir.getMealItemId());
                    if (existingItem == null) {
                        // id가 있는데 다른 카드의 아이템이거나 없는 경우 → 새로 생성으로 취급
                        MealItem createdItem = MealItem.create(
                            updated.getId(),
                            ir.getName(),
                            ir.getIngredient(),
                            toPrimitive(ir.getCarbohydrate()),
                            toPrimitive(ir.getProtein()),
                            toPrimitive(ir.getFat()),
                            toPrimitive(ir.getSodium()),
                            toPrimitive(ir.getCalorie())
                        );
                        mealItemRepository.save(createdItem);
                    } else {
                        MealItem merged = MealItem.reconstruct(
                            existingItem.getId(),
                            updated.getId(),
                            ir.getName() != null && !ir.getName().isBlank() ? ir.getName() : existingItem.getName(),
                            ir.getIngredient() != null ? ir.getIngredient() : existingItem.getIngredient(),
                            ir.getCarbohydrate() != null ? ir.getCarbohydrate() : existingItem.getCarbohydrate(),
                            ir.getProtein() != null ? ir.getProtein() : existingItem.getProtein(),
                            ir.getFat() != null ? ir.getFat() : existingItem.getFat(),
                            ir.getSodium() != null ? ir.getSodium() : existingItem.getSodium(),
                            ir.getCalorie() != null ? ir.getCalorie() : existingItem.getCalorie()
                        );
                        mealItemRepository.save(merged);
                    }
                }
            }

            // 남은 아이템 삭제
            for (MealItem old : currentItems) {
                if (!seen.contains(old.getId().value())) {
                    mealItemRepository.delete(old.getId());
                }
            }
        }

        // 최종 업데이트된 mealCard (아이템 포함) 조회 및 반환
        var withItems = mealCardRepository.findByCalendarIdAndRecordDateAndDivision(CalendarId.of(targetCalendarId), newRecordDate, newDivision)
            .orElse(updated);
        return MealCardResponse.from(withItems);
    }
}
