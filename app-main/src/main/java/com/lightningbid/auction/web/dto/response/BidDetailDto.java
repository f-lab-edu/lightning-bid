package com.lightningbid.auction.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lightningbid.user.web.dto.response.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BidDetailDto {
    private Long bidId;

    private UserResponseDto bidder;

    private Integer price;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bidAt;
}
