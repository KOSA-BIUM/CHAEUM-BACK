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

@Service
@RequiredArgsConstructor
public class MealCardAppService {

    private final MealCardRepository mealCardRepository;
    private final MealItemRepository mealItemRepository;
    private final CalendarAppService calendarAppService;

    // Basic getters
    @Transactional(readOnly = true)
    public Optional<MealCardResponse> getByMealCardId(String mealCardId) {
        if (mealCardId == null || mealCardId.isBlank()) throw new IllegalArgumentException("mealCardId is required");
        return mealCardRepository.findByMealCardId(MealCardId.of(mealCardId)).map(MealCardResponse::from);
    }

    @Transactional(readOnly = true)
    public List<MealCardResponse> listByCalendarId(String calendarId) {
        if (calendarId == null || calendarId.isBlank()) throw new IllegalArgumentException("calendarId is required");
        return mealCardRepository.findListByCalendarId(CalendarId.of(calendarId)).stream().map(MealCardResponse::from).toList();
    }

    // For calendar page: list only the mealCards for the month
    @Transactional(readOnly = true)
    public List<MealCardResponse> listByUserIdAndYearMonth(String userId, String yearMonth) {
        if (userId == null || userId.isBlank()) throw new IllegalArgumentException("userId is required");
        if (yearMonth == null || yearMonth.isBlank()) throw new IllegalArgumentException("yearMonth is required");
        var calOpt = calendarAppService.getByUserIdAndYearMonth(userId, yearMonth);
        return calOpt.map(c -> c.getMealCards() == null ? List.<MealCardResponse>of() : c.getMealCards())
                     .orElse(List.of());
    }

    @Transactional(readOnly = true)
    public Optional<MealCardResponse> getByCalendarIdAndRecordDateAndDivision(String calendarId, LocalDateTime recordDate, String division) {
        if (calendarId == null || calendarId.isBlank()) throw new IllegalArgumentException("calendarId is required");
        if (recordDate == null) throw new IllegalArgumentException("recordDate is required");
        if (division == null || division.isBlank()) throw new IllegalArgumentException("division is required");
        return mealCardRepository.findByCalendarIdAndRecordDateAndDivision(CalendarId.of(calendarId), recordDate, division).map(MealCardResponse::from);
    }

    // Detail view by mealCardId (loads items using a second query)
    @Transactional(readOnly = true)
    public Optional<MealCardResponse> getDetailByMealCardId(String mealCardId) {
        if (mealCardId == null || mealCardId.isBlank()) throw new IllegalArgumentException("mealCardId is required");
        var base = mealCardRepository.findByMealCardId(MealCardId.of(mealCardId));
        if (base.isEmpty()) return Optional.empty();
        var mc = base.get();
        var withItems = mealCardRepository.findByCalendarIdAndRecordDateAndDivision(mc.getCalendarId(), mc.getRecordDate(), mc.getDivision().name());
        return withItems.map(MealCardResponse::from).or(()->base.map(MealCardResponse::from));
    }

    // Create a MealCard; if calendar for userId+yearMonth doesn't exist, create it first
    @Transactional
    public MealCardResponse create(String userId, String yearMonth, MealCardRequest request) {
        if (userId == null || userId.isBlank()) throw new IllegalArgumentException("userId is required");
        if (yearMonth == null || yearMonth.isBlank()) throw new IllegalArgumentException("yearMonth is required");
        if (request == null) throw new IllegalArgumentException("request is required");
        if (request.getRecordDate() == null) throw new IllegalArgumentException("recordDate is required");
        if (request.getDivision() == null || request.getDivision().isBlank()) throw new IllegalArgumentException("division is required");

        // 1) Ensure calendar exists (idempotent)
        var calRes = calendarAppService.ensureExists(CalendarRequest.builder().userId(userId).yearMonth(yearMonth).build());
        String calendarId = calRes.getCalendarId();

        // 2) Guard: duplication per (calendarId, recordDate, division)
        Optional<MealCard> dup = mealCardRepository.findByCalendarIdAndRecordDateAndDivision(CalendarId.of(calendarId), request.getRecordDate(), request.getDivision());
        if (dup.isPresent()) {
            throw new DomainException("MealCard already exists for recordDate=" + request.getRecordDate() + ", division=" + request.getDivision());
        }

        // 3) Create and persist
        MealCard created = MealCard.create(
            CalendarId.of(calendarId),
            request.getRecordDate(),
            MealCardType.valueOf(request.getDivision()),
            List.of()
        );
        mealCardRepository.save(created);
        return MealCardResponse.from(created);
    }
    
    // Variant: Use existing calendarId directly (recommended after calendar page loads)
    @Transactional
    public MealCardResponse createWithItemsByCalendarId(String calendarId, MealCardWithItemsRequest request) {
        if (calendarId == null || calendarId.isBlank()) throw new IllegalArgumentException("calendarId is required");
        if (request == null) throw new IllegalArgumentException("request is required");
        if (request.getRecordDate() == null) throw new IllegalArgumentException("recordDate is required");
        if (request.getDivision() == null || request.getDivision().isBlank()) throw new IllegalArgumentException("division is required");

        // Optional: verify calendar existence (and ownership if needed)
        var calOpt = calendarAppService.getByCalendarId(calendarId);
        if (calOpt.isEmpty()) {
            throw new DomainException("Calendar not found: " + calendarId);
        }

        var dup = mealCardRepository.findByCalendarIdAndRecordDateAndDivision(CalendarId.of(calendarId), request.getRecordDate(), request.getDivision());
        if (dup.isPresent()) {
            throw new DomainException("MealCard already exists for recordDate=" + request.getRecordDate() + ", division=" + request.getDivision());
        }

        MealCard created = MealCard.create(
            CalendarId.of(calendarId),
            request.getRecordDate(),
            MealCardType.valueOf(request.getDivision()),
            List.of()
        );
        mealCardRepository.save(created);

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

        var withItems = mealCardRepository.findByCalendarIdAndRecordDateAndDivision(CalendarId.of(calendarId), request.getRecordDate(), request.getDivision())
            .orElse(created);
        return MealCardResponse.from(withItems);
    }

    // Variant: Use userId + yearMonth (when calendarId is not yet known)
    @Transactional
    public MealCardResponse createWithItems(String userId, String yearMonth, MealCardWithItemsRequest request) {
        if (userId == null || userId.isBlank()) throw new IllegalArgumentException("userId is required");
        if (yearMonth == null || yearMonth.isBlank()) throw new IllegalArgumentException("yearMonth is required");
        if (request == null) throw new IllegalArgumentException("request is required");
        if (request.getRecordDate() == null) throw new IllegalArgumentException("recordDate is required");
        if (request.getDivision() == null || request.getDivision().isBlank()) throw new IllegalArgumentException("division is required");

        // 1) Ensure calendar exists (idempotent)
        var calRes = calendarAppService.ensureExists(CalendarRequest.builder().userId(userId).yearMonth(yearMonth).build());
        String calendarId = calRes.getCalendarId();

        // 2) Duplicate guard
        var dup = mealCardRepository.findByCalendarIdAndRecordDateAndDivision(CalendarId.of(calendarId), request.getRecordDate(), request.getDivision());
        if (dup.isPresent()) {
            throw new DomainException("MealCard already exists for recordDate=" + request.getRecordDate() + ", division=" + request.getDivision());
        }

        // 3) Create base meal card
        MealCard created = MealCard.create(
            CalendarId.of(calendarId),
            request.getRecordDate(),
            MealCardType.valueOf(request.getDivision()),
            List.of()
        );
        mealCardRepository.save(created);

        // 4) Create items if present
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

        var withItems = mealCardRepository.findByCalendarIdAndRecordDateAndDivision(CalendarId.of(calendarId), request.getRecordDate(), request.getDivision())
            .orElse(created);
        return MealCardResponse.from(withItems);
    }

    @Transactional
    public MealCardResponse update(String mealCardId, MealCardRequest request) {
        if (mealCardId == null || mealCardId.isBlank()) throw new IllegalArgumentException("mealCardId is required");
        if (request == null) throw new IllegalArgumentException("request is required");

        MealCard existing = mealCardRepository.findByMealCardId(MealCardId.of(mealCardId))
                .orElseThrow(() -> new DomainException("MealCard not found: " + mealCardId));

        // Apply new values (all are required fields for MealCard)
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

    @Transactional
    public void delete(String mealCardId) {
        if (mealCardId == null || mealCardId.isBlank()) throw new IllegalArgumentException("mealCardId is required");
        mealCardRepository.delete(MealCardId.of(mealCardId));
    }

    private Integer toPrimitive(Integer v) { return v == null ? 0 : v; }

    // Composite update: update meal card fields, optionally move to another calendar, and fully replace items list
    @Transactional
    public MealCardResponse updateWithItems(String mealCardId, MealCardWithItemsRequest request) {
        if (mealCardId == null || mealCardId.isBlank()) throw new IllegalArgumentException("mealCardId is required");
        if (request == null) throw new IllegalArgumentException("request is required");

        MealCard existing = mealCardRepository.findByMealCardId(MealCardId.of(mealCardId))
            .orElseThrow(() -> new DomainException("MealCard not found: " + mealCardId));

        // 1) Resolve target calendar (stay, explicit calendarId, or ensure via userId+yearMonth)
        String targetCalendarId = existing.getCalendarId().value();
        if (request.getUserId() != null && !request.getUserId().isBlank()
                && request.getYearMonth() != null && !request.getYearMonth().isBlank()) {
            var cal = calendarAppService.ensureExists(CalendarRequest.builder().userId(request.getUserId()).yearMonth(request.getYearMonth()).build());
            targetCalendarId = cal.getCalendarId();
        }

        // 2) Compute new card fields (null → keep existing)
        LocalDateTime newRecordDate = request.getRecordDate() != null ? request.getRecordDate() : existing.getRecordDate();
        String newDivision = request.getDivision() != null && !request.getDivision().isBlank() ? request.getDivision() : existing.getDivision().name();

        // 3) Duplicate guard on target calendar
        var conflict = mealCardRepository.findByCalendarIdAndRecordDateAndDivision(CalendarId.of(targetCalendarId), newRecordDate, newDivision)
            .filter(mc -> !mc.getId().value().equals(existing.getId().value()));
        if (conflict.isPresent()) {
            throw new DomainException("MealCard already exists for recordDate=" + newRecordDate + ", division=" + newDivision);
        }

        // 4) Update base meal card
        MealCard updated = MealCard.reconstruct(
            existing.getId(),
            CalendarId.of(targetCalendarId),
            newRecordDate,
            MealCardType.valueOf(newDivision),
            existing.getMealItems() // items will be replaced below
        );
        mealCardRepository.save(updated);

        // 5) Replace items if provided (full replacement semantics)
        if (request.getItems() != null) {
            // Load current items
            var currentItems = mealItemRepository.findByMealCardId(updated.getId());
            var currentById = currentItems.stream().collect(Collectors.toMap(i -> i.getId().value(), i -> i));

            // Track seen IDs to detect deletions
            Set<String> seen = new HashSet<>();

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

            // delete missing items (those not seen)
            for (MealItem old : currentItems) {
                if (!seen.contains(old.getId().value())) {
                    mealItemRepository.delete(old.getId());
                }
            }
        }

        // 6) Return fresh view (with items)
        var withItems = mealCardRepository.findByCalendarIdAndRecordDateAndDivision(CalendarId.of(targetCalendarId), newRecordDate, newDivision)
            .orElse(updated);
        return MealCardResponse.from(withItems);
    }
}
