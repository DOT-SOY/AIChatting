package com.desk.controller;

import com.desk.domain.TicketState;
import com.desk.dto.TicketFilterDTO;
import com.desk.dto.TicketReceivedListDTO;
import com.desk.service.PersonalTicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.web.PageableDefault;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/tickets/received")
public class PersonalTicketController {

    private final PersonalTicketService personalTicketService;

    // 받은함 페이지 조회 --- receiver 기준으로 Ticket+내 TicketPersonal(tpRead/tpState) 정보를 필터/페이징 적용해 반환
    @GetMapping
    public ResponseEntity<Page<TicketReceivedListDTO>> listInbox(
            @RequestParam String receiver,
            @ModelAttribute TicketFilterDTO filter,
            @PageableDefault(size = 10, sort = "pno", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info("[PersonalTicketController#listRecieveTicket] receiver={}, filter={}, pageable={}",
                receiver, filter, pageable);

        Page<TicketReceivedListDTO> page = personalTicketService.listRecieveTicket(receiver, filter, pageable);
        return ResponseEntity.ok(page);
    }

    // 받은 티켓 단일 조회(tpno) --- receiver 소유 검증 후 조회하며 markAsRead=true이면 tpRead를 true로 변경
    @GetMapping("/by-pno/{pno}")
    public ResponseEntity<TicketReceivedListDTO> readInboxByTpno(
            @PathVariable Long tpno,
            @RequestParam String receiver,
            @RequestParam(defaultValue = "true") boolean markAsRead
    ) {
        log.info("[PersonalTicketController#readInboxByTpno] tpno={}, receiver={}, markAsRead={}",
                tpno, receiver, markAsRead);

        TicketReceivedListDTO dto = personalTicketService.readRecieveTicket(tpno, receiver, markAsRead);
        return ResponseEntity.ok(dto);
    }

    // 받은 티켓 단일 조회(tno) --- receiver+tno로 대상 TicketPersonal을 찾아 tpno 조회로 위임하고 markAsRead 옵션을 적용
    @GetMapping("/by-tno/{tno}")
    public ResponseEntity<TicketReceivedListDTO> readInboxByTno(
            @PathVariable Long tno,
            @RequestParam String receiver,
            @RequestParam(defaultValue = "true") boolean markAsRead
    ) {
        log.info("[PersonalTicketController#readRecieveTicketByTno] tno={}, receiver={}, markAsRead={}",
                tno, receiver, markAsRead);

        TicketReceivedListDTO dto = personalTicketService.readRecieveTicketByTno(tno, receiver, markAsRead);
        return ResponseEntity.ok(dto);
    }

    // 진행상태 변경 --- receiver가 소유한 tpno의 tpState를 요청 state로 변경해 반환
    @PatchMapping("/{pno}/state")
    public ResponseEntity<TicketReceivedListDTO> changeState(
            @PathVariable Long tpno,
            @RequestParam String receiver,
            @RequestParam TicketState state
    ) {
        log.info("[PersonalTicketController#changeState] tpno={}, receiver={}, state={}",
                tpno, receiver, state);

        TicketReceivedListDTO dto = personalTicketService.changeState(tpno, receiver, state);
        return ResponseEntity.ok(dto);
    }
}
