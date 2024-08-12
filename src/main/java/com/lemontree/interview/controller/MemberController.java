package com.lemontree.interview.controller;

import com.lemontree.interview.request.MemberCreate;
import com.lemontree.interview.response.MemberResponse;
import com.lemontree.interview.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 유저 Controller 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 08.
 */
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 유저 생성 요청 메서드입니다.
     *
     * @param request 유저 생성 요청 DTO
     * @return 201 (CREATED), body: 생성된 유저 ID
     */
    @PostMapping("/api/v1/members")
    public ResponseEntity<Map<String, Long>> createMember(@Valid @RequestBody MemberCreate request) {

        Long memberId = memberService.createMember(request);
        Map<String, Long> response = Map.of("memberId", memberId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 유저 조회 메서드입니다.
     *
     * @param memberId 조회할 유저 ID
     * @return 200 (OK), body: 유저 응답 DTO
     */
    @GetMapping("/api/v1/members/{memberId}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable("memberId") Long memberId) {
        return ResponseEntity.ok(memberService.getMember(memberId));
    }
}
