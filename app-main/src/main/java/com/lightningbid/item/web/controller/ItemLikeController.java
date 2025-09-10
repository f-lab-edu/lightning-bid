package com.lightningbid.item.web.controller;

import com.lightningbid.auth.dto.CustomOAuth2User;
import com.lightningbid.common.dto.CommonResponseDto;
import com.lightningbid.item.service.ItemLikeAsyncService;
import com.lightningbid.item.service.ItemLikeService;
import com.lightningbid.item.web.dto.response.ItemLikeResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/items/{itemId}/like")
@RestController
public class ItemLikeController {

    private final ItemLikeAsyncService itemLikeAsyncService;

    @PostMapping
    public CompletableFuture<ResponseEntity<CommonResponseDto<ItemLikeResponseDto>>> likeItem(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable Long itemId) {

        // [Tomcat 스레드] 가 컨트롤러 메서드 실행.
        log.info("1. Controller 진입, 스레드: {}", Thread.currentThread().getName());

        // [Tomcat 스레드] 아래 메서드는 다른 스레드에게 오래 걸리는 작업을 시작시키고, 아직 결과가 없는 비어있는 CompletableFuture를 즉시 반환.
        CompletableFuture<ItemLikeResponseDto> future = itemLikeAsyncService.likeItemAsync(user.getId(), itemId);

        // [Tomcat 스레드] - 콜백 등록
        // 'future' 가 나중에 완료되면 실행될 로직(람다)을 콜백으로 등록.
        // .thenApply() 호출은 새로운 CompletableFuture를 생성하며, 이 객체는 최종 결과를 담을 그릇이 된다.
        CompletableFuture<ResponseEntity<CommonResponseDto<ItemLikeResponseDto>>> resultFuture =
                future
                        .thenApply(responseDto ->
                                ResponseEntity.ok(
                                        CommonResponseDto.success(
                                                HttpStatus.OK.value(),
                                                responseDto.getIsLiked() ? "좋아요 처리가 완료되었습니다." : "좋아를 취소 처리했습니다.",
                                                responseDto)
                                )
                        );

        // [Tomcat 스레드] - 임무 종료 및 반환
        // Tomcat 스레드는 최종 결과가 아닌, '작업이 예약된' resultFuture 객체를 Spring MVC에 반환하고
        // 즉시 스레드 풀로 복귀하여 다른 요청을 처리하러 간다.
        log.info("Tomcat 스레드 종료, 스레드 풀로 복귀");

        return resultFuture;

        /*
         * 비동기 작업 완료 후
         *
         * [Async 스레드] - 비동기 작업 완료
         * itemLikeAsyncService.likeItemAsync()의 실제 로직이 Async 스레드에서
         * 완료되고, 그 결과(ItemLikeResponseDto)가 'future' 객체에 채워진다. (CompletableFuture.completedFuture(itemLikeService.likeItem(userId, itemId));)
         *
         * [Async 스레드] - 콜백 실행
         * 'future' 가 완료되었으므로, 위에서 예약했던 .thenApply()의 람다 함수가
         * Async 스레드에서 실행된다. 이 결과로 최종 ResponseEntity가 생성된다.
         *
         * [Spring MVC] - 최종 응답 처리
         * 'resultFuture' 가 ResponseEntity로 완료된 것을 Spring MVC가 감지.
         * 이후 Async Dispatch를 시작하여, 별도의 스레드를 통해 최종 응답을 클라이언트에게 전송한다.
         */
    }
}
