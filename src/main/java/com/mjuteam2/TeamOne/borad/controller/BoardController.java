package com.mjuteam2.TeamOne.borad.controller;

import com.mjuteam2.TeamOne.borad.domain.Board;
import com.mjuteam2.TeamOne.borad.dto.request.AppealBoardForm;
import com.mjuteam2.TeamOne.borad.dto.request.FreeBoardForm;
import com.mjuteam2.TeamOne.borad.dto.request.WantedBoardForm;
import com.mjuteam2.TeamOne.borad.dto.response.BoardListResponse;
import com.mjuteam2.TeamOne.borad.dto.response.BoardResponse;
import com.mjuteam2.TeamOne.borad.exception.BoardException;
import com.mjuteam2.TeamOne.borad.service.BoardService;
import com.mjuteam2.TeamOne.member.domain.Member;
import com.mjuteam2.TeamOne.member.repository.MemberRepository;
import com.mjuteam2.TeamOne.member.service.MemberService;
import com.mjuteam2.TeamOne.util.dto.BoolResponse;
import com.mjuteam2.TeamOne.util.dto.ErrorResponse;
import com.mjuteam2.TeamOne.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import static com.mjuteam2.TeamOne.util.dto.ErrorResponse.convertJson;
import static com.mjuteam2.TeamOne.util.dto.RestResponse.badRequest;
import static com.mjuteam2.TeamOne.util.dto.RestResponse.success;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    private void logError(List<FieldError> errors) {
        log.error("Board Errors = {}", errors);
    }

    /**
     * ????????? ?????? ??? PathVariable??? ?????? ?????? ???????????????? form??? ????????? ????????? ?????? ?????????
     */
    // ???????????????
    @PostMapping("/wanted")
    public ResponseEntity<?> createWantedBoard(HttpServletRequest request,
                                         @Valid @RequestBody WantedBoardForm form,
                                         BindingResult bindingResult) throws LoginException {
        if (bindingResult.hasErrors()) {
            logError(bindingResult.getFieldErrors());
            return badRequest(convertJson(bindingResult.getFieldErrors()));
        }
        Member loginMember = memberService.getLoginMember(request);
        Board savedBoard = boardService.save(loginMember, form);
        return success(savedBoard.toResponse());
    }

    // ????????????
    @PostMapping("/appeal")
    public ResponseEntity<?> createAppealBoard(HttpServletRequest request,
                                         @Valid @RequestBody AppealBoardForm form,
                                         BindingResult bindingResult) throws LoginException {
        if (bindingResult.hasErrors()) {
            logError(bindingResult.getFieldErrors());
            return badRequest(convertJson(bindingResult.getFieldErrors()));
        }
        Member loginMember = memberService.getLoginMember(request);
        Board savedBoard = boardService.save(loginMember, form);
        return success(savedBoard.toResponse());
    }

    // ???????????????
    @PostMapping("/free")
    public ResponseEntity<?> createFreeBoard(HttpServletRequest request,
                                             @Valid @RequestBody FreeBoardForm form,
                                             BindingResult bindingResult) throws LoginException {
        if (bindingResult.hasErrors()) {
            logError(bindingResult.getFieldErrors());
            return badRequest(convertJson(bindingResult.getFieldErrors()));
        }
        Member loginMember = memberService.getLoginMember(request);
        Board savedBoard = boardService.save(loginMember, form);
        return success(savedBoard.toResponse());
    }

    // ??????????????? - no login
    @PostMapping("/new/free/no-login")
    public ResponseEntity<?> createFreeBoardNoLogin(@Valid @RequestBody FreeBoardForm form, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            logError(bindingResult.getFieldErrors());
            return badRequest(convertJson(bindingResult.getFieldErrors()));
        }
        Board savedBoard = boardService.save(memberRepository.findByEmail("mopil1102@naver.com").get(), form);
        return success(savedBoard.toResponse());
    }


    /**
     * ????????? ?????? ????????? ?????? X
     */
    // ????????? id??? ????????? ??????
    @GetMapping("/{boardId}")
    public ResponseEntity<?> findByBoardId(@PathVariable Long boardId) {
        Board findBoard = boardService.findByBoardId(boardId);
        return success(findBoard.toResponse());
    }

    // ?????? ???????????? ?????? ????????? ??????
    @GetMapping("/all")
    public ResponseEntity<?> findAllBoards() {
        BoardListResponse boards = boardService.findAll();
        return success(boards);
    }

    // ?????? ???????????? ?????? ????????? ?????? (?????????)
    @GetMapping("/all/paging")
    public ResponseEntity<?> findAllBoards(@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        BoardListResponse boards = boardService.findAll(pageable);
        return success(boards);
    }
    
    // ????????? ???????????? ?????? ??????
    @GetMapping("/all/{boardType}")
    public ResponseEntity<?> findAllByType(@PathVariable String boardType) {
        BoardListResponse allByType = boardService.findAllByType(boardType);
        return success(allByType);
    }

    // ???????????? ???????????? ??? ????????? ?????? ?????? (?????? ??????)
    @GetMapping("/written-all/{boardType}")
    public ResponseEntity<?> findWrittenAllByType(HttpServletRequest request, @PathVariable String boardType) throws LoginException {
        Member loginMember = memberService.getLoginMember(request);
        BoardListResponse writtenAllByType = boardService.findWrittenAllByType(loginMember, boardType);
        return success(writtenAllByType);
    }

    // ???????????? ???????????? ??? ????????? ?????? ?????? (?????? ??????)
    @GetMapping("/written-all/{boardType}/member/{memberId}")
    public ResponseEntity<?> findWrittenAllByType(@PathVariable String boardType, @PathVariable Long memberId) {
        BoardListResponse writtenAllByType = boardService.findWrittenAllByType11(memberId, boardType);
        return success(writtenAllByType);
    }

    // ????????? ????????? ???????????? ?????? (?????? ??????)
    @GetMapping("/search/{searchWay}/{keyword}")
    public ResponseEntity<?> search(@PathVariable("searchWay") String searchWay, @PathVariable("keyword") String keyword) {
        BoardListResponse search = boardService.search(searchWay, keyword);
        log.info("?????? = {}", searchWay);
        return success(search);
    }


    /**
     * ????????? ??????
     */
    // ???????????????
    @PutMapping("/wanted/{boardId}")
    public ResponseEntity<?> updateWantedBoard(HttpServletRequest request,
                                             @PathVariable Long boardId,
                                             @Valid @RequestBody WantedBoardForm form,
                                             BindingResult bindingResult) throws LoginException {
        if (bindingResult.hasErrors()) {
            logError(bindingResult.getFieldErrors());
            return badRequest(convertJson(bindingResult.getFieldErrors()));
        }
        Member loginMember = memberService.getLoginMember(request);
        Board updatedBoard = boardService.update(loginMember, boardId, form);
        return success(updatedBoard.toResponse());

    }

    // ????????????
    @PutMapping("/appeal/{boardId}")
    public ResponseEntity<?> updateAppealBoard(HttpServletRequest request,
                                               @PathVariable Long boardId,
                                               @Valid @RequestBody AppealBoardForm form,
                                               BindingResult bindingResult) throws LoginException {
        if (bindingResult.hasErrors()) {
            logError(bindingResult.getFieldErrors());
            return badRequest(convertJson(bindingResult.getFieldErrors()));
        }
        Member loginMember = memberService.getLoginMember(request);
        Board updatedBoard = boardService.update(loginMember, boardId, form);
        return success(updatedBoard.toResponse());

    }

    // ??????
    @PutMapping("/free/{boardId}")
    public ResponseEntity<?> updateFreeBoard(HttpServletRequest request,
                                               @PathVariable Long boardId,
                                               @Valid @RequestBody FreeBoardForm form,
                                               BindingResult bindingResult) throws LoginException {
        if (bindingResult.hasErrors()) {
            logError(bindingResult.getFieldErrors());
            return badRequest(convertJson(bindingResult.getFieldErrors()));
        }
        Member loginMember = memberService.getLoginMember(request);
        Board updatedBoard = boardService.update(loginMember, boardId, form);
        return success(updatedBoard.toResponse());

    }


    /**
     * ????????? ??????
     */
    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> deleteBoard(HttpServletRequest request, @PathVariable Long boardId) throws LoginException {
        memberService.getLoginMember(request);
        return success(new BoolResponse(boardService.deleteBoard(boardId)));
    }

    /**
     * ????????? ?????? ??????
     */
    @PutMapping("/finish/{boardId}")
    public ResponseEntity<?> finishBoard(@PathVariable Long boardId) {
        BoardResponse board = boardService.finishBoard(boardId);
        return success(board);
    }

    /**
     *  ?????? ??????
     */
    // ????????? ??????
    @ExceptionHandler(BoardException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> boardExHandle(BoardException e) {
        log.error("????????? ?????? ?????? : ????????? ??????");
        return badRequest(new ErrorResponse(ErrorCode.BOARD_ERROR, e.getMessage()));
    }

}
