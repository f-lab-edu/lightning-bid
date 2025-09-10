package com.lightningbid.item.domain.repository.impl;

import com.lightningbid.auction.domain.model.Auction;
import com.lightningbid.common.dto.CursorResult;
import com.lightningbid.item.domain.enums.ItemStatus;
import com.lightningbid.item.domain.model.Item;
import com.lightningbid.item.domain.repository.ItemRepositoryCustom;
import com.lightningbid.item.web.dto.response.ItemSummaryDto;
import com.lightningbid.user.domain.model.User;
import com.lightningbid.user.web.dto.response.UserResponseDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.lightningbid.auction.domain.model.QAuction.auction;
import static com.lightningbid.item.domain.model.QItem.item;
import static com.lightningbid.user.domain.model.QUser.user;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public CursorResult<ItemSummaryDto> search(String title, String sortColumn, String sortDirection, String cursor, Pageable pageable) {

        List<Item> items = queryFactory
                .selectFrom(item)
                .join(item.auction, auction).fetchJoin()
                .join(item.user, user).fetchJoin()
                .where(
                        item.status.notIn(ItemStatus.CANCELED, ItemStatus.EXPIRED, ItemStatus.FAILED),
                        titleContains(title),
                        cursorCondition(cursor, sortColumn, sortDirection)
                )
                .limit(pageable.getPageSize() + 1)
                .orderBy(getOrderSpecifiers(sortColumn, sortDirection))
                .fetch();

        boolean hasNext = items.size() > pageable.getPageSize();

        // pageSize + 1 개를 조회 후, 다음 데이터가 존재 하는지 확인. 존재하면 마지막 데이터 제거.
        if (hasNext)
            items.removeLast();

        List<ItemSummaryDto> content = items.stream()
                .map(this::mapToItemSummaryDto)
                .toList();

        String nextCursor = null;
        if (hasNext) // 조회된 데이터의 마지막 값을 cursor로 생성해서 리턴한다.
            nextCursor = generatePlainCursor(items.getLast(), sortColumn);

        return new CursorResult<>(content, hasNext, nextCursor);
    }

    private ItemSummaryDto mapToItemSummaryDto(Item item) {

        User seller = item.getUser();
        Auction auction = item.getAuction();

        return ItemSummaryDto.builder()
                .itemId(item.getId())
                .auctionId(auction.getId())
                .title(item.getTitle())
//                .thumbnailUrl()
                .location(item.getLocation())
                .seller(UserResponseDto.builder()
                        .userId(seller.getId())
                        .nickname(seller.getNickname())
//                        .profileImageUrl(seller.getProfileUrl())
                        .build())
                .price(auction.getStartPrice())
                .currentBid(auction.getCurrentBid())
                .status(item.getStatus().getKoreanCode())
                .createdAt(item.getCreatedAt())
                .viewCount(item.getViewCount())
                .likeCount(item.getLikeCount())
                .bidCount(auction.getBidCount())
                .build();
    }

    private BooleanExpression titleContains(String title) {

        return title != null && !title.isEmpty() ? item.title.containsIgnoreCase(title) : null;
    }

    private BooleanExpression cursorCondition(String cursor, String sortColumn, String sortDirection) {

        if (cursor == null) return null;

        String[] parts = cursor.split("_");
        if (parts.length < 2) return null;

        String value = parts[0];
        long id = Long.parseLong(parts[1]);

        boolean isDesc = sortDirection.equalsIgnoreCase("desc");

        switch (sortColumn) {
            case "viewCount":
                int viewCount = Integer.parseInt(value);
                return isDesc ? item.viewCount.lt(viewCount).or(item.viewCount.eq(viewCount).and(item.id.lt(id)))
                        : item.viewCount.gt(viewCount).or(item.viewCount.eq(viewCount).and(item.id.gt(id)));
            case "likeCount":
                int likeCount = Integer.parseInt(value);
                return isDesc ? item.likeCount.lt(likeCount).or(item.likeCount.eq(likeCount).and(item.id.lt(id)))
                        : item.likeCount.gt(likeCount).or(item.likeCount.eq(likeCount).and(item.id.gt(id)));
            case "createdAt":
                LocalDateTime createdAt = LocalDateTime.parse(value);
                return isDesc ? item.createdAt.lt(createdAt).or(item.createdAt.eq(createdAt).and(item.id.lt(id)))
                        : item.createdAt.gt(createdAt).or(item.createdAt.eq(createdAt).and(item.id.gt(id)));
            default:
                return null;
        }
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(String sortColumn, String sortDirection) {

        Order order = sortDirection.equalsIgnoreCase("desc") ? Order.DESC : Order.ASC;
        OrderSpecifier<Long> idOrder = new OrderSpecifier<>(Order.DESC, item.id);

        switch (sortColumn) {
            case "viewCount":
                return new OrderSpecifier[]{new OrderSpecifier<>(order, item.viewCount), idOrder};
            case "likeCount":
                return new OrderSpecifier[]{new OrderSpecifier<>(order, item.likeCount), idOrder};
            case "createdAt":
                return new OrderSpecifier[]{new OrderSpecifier<>(order, item.createdAt), idOrder};
            default:
                return new OrderSpecifier[]{new OrderSpecifier<>(Order.DESC, item.createdAt), idOrder};
        }
    }

    // 일반 텍스트 커서 생성
    private String generatePlainCursor(Item lastItem, String sortColumn) {

        String rawCursor;
        switch (sortColumn) {
            case "viewCount":
                rawCursor = lastItem.getViewCount() + "_" + lastItem.getId();
                break;
            case "likeCount":
                rawCursor = lastItem.getLikeCount() + "_" + lastItem.getId();
                break;
            default: // "createdAt"
                rawCursor = lastItem.getCreatedAt().toString() + "_" + lastItem.getId();
                break;
        }
        return rawCursor;
    }
}